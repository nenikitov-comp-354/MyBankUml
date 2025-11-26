package bank_integrations.db;

import bank.db.*;
import bank.db.operation.OperationLock;
import bank.db.operation.Operation;
import bank.db.operation.OperationTransaction;

import java.sql.SQLException;
import java.util.*;
import java.time.LocalDate;
import java.math.BigDecimal;

public class LockOperation {
    public static void main(String[] args) {
        
        BankDb db = new BankDb(
            "localhost",
            Optional.empty(),
            "bank",
            "admin",
            Optional.of("admin")
        );

        try {
            db.connect();

            runLockUnlock(db);
            runMissingAccount(db);

        } catch (Exception e) {
            System.err.println("[LockOperation] Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * @brief ensures that queue can be written to,
     * locks account, unlocks it
     * @param db
     * @throws SQLException
     */
     private static void runLockUnlockScenario(BankDb db) throws SQLException {
        System.out.println("LockOperation Integration: lock/unlock");

        Map<Integer, Account> accounts = db.getAccounts();
        if (accounts.size() < 2) {
            System.out.println("Not enough accounts in DB to run this scenario.");
            return;
        }

        Iterator<Account> it = accounts.values().iterator();
        Account target = it.next(); // for lock
        Account other = it.next();

        System.out.println("Target account: " + target);
        System.out.println("Other  account: " + other);

        // unlock target if locked
        if (target.isLocked()) {
            db.addOperation(new OperationLock(target, false));
            db.processOperations();
        }

        boolean initialTargetLocked = target.isLocked();
        boolean initialOtherLocked = other.isLocked();

        System.out.println(
            "Initial isLocked: target=" +
            initialTargetLocked +
            ", other=" +
            initialOtherLocked
        );

        // making sure operation doesnt change lock 
        db.addOperation(new OperationLock(target, initialTargetLocked));
        db.processOperations();

        System.out.println(
            "After Operation lock call, isLocked: target=" +
            target.isLocked() +
            ", other=" +
            other.isLocked()
        );

        // lock target account
        db.addOperation(new OperationLock(target, true));
        db.processOperations();

        System.out.println(
            "After lock operation, isLocked: target=" +
            target.isLocked() +
            ", other=" +
            other.isLocked()
        );

        // Verify DB rows using a fresh BankDb instance
        verifyLockedStateInDb(target.getId(), true);
        verifyLockedStateInDb(other.getId(), initialOtherLocked);

        // Now unlock the target account
        db.addOperation(new OperationLock(target, false));
        db.processOperations();

        System.out.println(
            "After unlock operation, isLocked: target=" +
            target.isLocked() +
            ", other=" +
            other.isLocked()
        );

        verifyLockedStateInDb(target.getId(), false);
        verifyLockedStateInDb(other.getId(), initialOtherLocked);

        System.out.println("End of lock/unlock test\n");
    }

    private static void runMissingAccountScenario(BankDb db) throws SQLException {
        System.out.println(
            "LockOperation Integration: missing account exception"
        );

        Map<Integer, Customer> customers = db.getCustomers();
        if (customers.isEmpty()) {
            System.out.println(
                "No customers in DB; skipping missing-account scenario."
            );
            return;
        }

        // Take any existing customer and create a chequing account with a new id
        Customer anyCustomer = customers.values().iterator().next();
        int fakeId = 999_999; // id that does not exist in DB

        Account fakeAccount =
            new AccountChequing(
                fakeId,
                "Non-existent account",
                false,
                anyCustomer,
                BigDecimal.ZERO
            );

        db.addOperation(new OperationLock(fakeAccount, true));

        try {
            db.processOperations();
            System.out.println(
                "ERROR: expected UnsupportedOperationException for missing account, but none was thrown."
            );
        } catch (UnsupportedOperationException e) {
            System.out.println(
                "Correctly caught UnsupportedOperationException: " + e.getMessage()
            );
        }

        System.out.println("End of missing-account test");
    }

}
