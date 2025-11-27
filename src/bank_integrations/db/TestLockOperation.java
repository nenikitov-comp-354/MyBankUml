package bank_integrations.db;

import bank.db.*;
import bank.db.operation.Operation;
import bank.db.operation.OperationLock;
import bank.db.operation.OperationTransaction;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class TestLockOperation {

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
    private static void runLockUnlock(BankDb db) throws SQLException {
        System.out.println("LockOperation Integration: lock/unlock");

        Map<Integer, Account> accounts = db.getAccounts();
        if (accounts.size() < 2) {
            System.out.println(
                "Not enough accounts in DB to run this scenario."
            );
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

        //should print out unlocked
        System.out.println(
            "Initial isLocked: target=" +
            initialTargetLocked +
            ", other=" +
            initialOtherLocked
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

    /**
     * @brief Missing account testing, creates a fakeID and fake account in  order to test
     * the missing account exception
     * @param db
     * @throws SQLException
     */
    private static void runMissingAccount(BankDb db) throws SQLException {
        System.out.println(
            "LockOperation Integration: missing account exception test"
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

        Account fakeAccount = new AccountChequing(
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
                "Correctly caught UnsupportedOperationException: " +
                e.getMessage()
            );
        }

        System.out.println("End of missing-account test");
    }

    /**
     * @brief Helper that re-loads accounts from the DB (via a new BankDb) and prints / checks
     * the lock state for the given account id.
     * @param accountId
     * @param expected
     * @throws SQLException
     */
    private static void verifyLockedStateInDb(int accountId, boolean expected)
        throws SQLException {
        BankDb verifyDb = new BankDb(
            "localhost",
            Optional.empty(),
            "bank",
            "admin",
            Optional.of("admin")
        );
        verifyDb.connect();

        Account fromDb = verifyDb.getAccounts().get(accountId);
        if (fromDb == null) {
            System.out.println(
                "  [DB] Account " +
                accountId +
                " not found when verifying lock state."
            );
            return;
        }

        System.out.println(
            "  [DB] account " +
            accountId +
            " is_locked=" +
            fromDb.isLocked() +
            " (expected " +
            expected +
            ")"
        );
    }
}
