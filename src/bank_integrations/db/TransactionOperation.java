package bank_integrations.db;

import bank.db.*;
import bank.db.operation.Operation;
import bank.db.operation.OperationLock;
import bank.db.operation.OperationTransaction;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class TransactionOperation {

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

            runValid(db);
            runInvalid(db);
        } catch (Exception e) {
            System.err.println(
                "[TransactionOperationTest] Error: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    /**
     * @brief enqueue valid transactions, process queue, print new balance
     * @param db
     * @throws SQLException
     */
    private static void runValid(BankDb db) throws SQLException {
        System.out.println(
            "=== TransactionOperation Integration: valid transfer ==="
        );

        Map<Integer, Account> accounts = db.getAccounts();
        if (accounts.size() < 2) {
            System.out.println(
                "Not enough accounts in DB to run this scenario."
            );
            return;
        }

        Iterator<Account> it = accounts.values().iterator();
        Account source = it.next();
        Account destination = it.next();

        System.out.println("Source account: " + source);
        System.out.println("Destination account: " + destination);

        // to track # of transactions
        int globalBefore = db.getTransactions().size();
        int sourceBefore = source.getTransactions().size();
        int destBefore = destination.getTransactions().size();

        System.out.println(
            "Before: global=" +
            globalBefore +
            ", sourceTransactions=" +
            sourceBefore +
            ", destTransactions=" +
            destBefore
        );
        // Build transaction info
        TransactionInfo info = new TransactionInfo(
            source,
            destination,
            new BigDecimal("17.79"),
            LocalDateTime.now()
        );

        // Add new transaction operation
        OperationTransaction op = new OperationTransaction(info);
        db.addOperation(op);

        // Process the queue
        db.processOperations();

        int globalAfter = db.getTransactions().size();
        int sourceAfter = source.getTransactions().size();
        int destAfter = destination.getTransactions().size();

        System.out.println(
            "After: global=" +
            globalAfter +
            ", sourceTransactions=" +
            sourceAfter +
            ", destTransactions=" +
            destAfter
        );

        // Print out newest transaction (if you want to visually confirm)
        if (globalAfter > globalBefore) {
            System.out.println("New transactions in BankDb:");
            for (Transaction t : db.getTransactions().values()) {
                System.out.println("  " + t);
            }
        }

        System.out.println("End of valid transfer operation\n");
    }

    /**
     * @brief will run invalid test, will create a fake account which isn't in DB as source
     * will use real account as destination
     * @param db
     * @throws SQLException
     */
    private static void runInvalid(BankDb db) throws SQLException {
        System.out.println(
            " TransactionOperation Integration: invalid account "
        );

        Map<Integer, Customer> customers = db.getCustomers();
        if (customers.isEmpty()) {
            System.out.println(
                "No customers in DB; skipping invalid-account scenario."
            );
            return;
        }

        Customer anyCustomer = customers.values().iterator().next();
        int fakeId = 999_999; // id that does not exist in DB

        // Create an AccountChequing with an id that is NOT in db.getAccounts()
        Account fakeSource = new AccountChequing(
            fakeId,
            "Non-existent account",
            false,
            anyCustomer,
            BigDecimal.ZERO
        );

        // Use a real destination account from the DB
        Map<Integer, Account> accounts = db.getAccounts();
        if (accounts.isEmpty()) {
            System.out.println(
                "No real accounts to use as destination; skipping."
            );
            return;
        }
        Account realDestination = accounts.values().iterator().next();

        TransactionInfo badInfo = new TransactionInfo(
            fakeSource,
            realDestination,
            new BigDecimal("5.00"),
            LocalDateTime.now()
        );

        OperationTransaction op = new OperationTransaction(badInfo);
        db.addOperation(op);

        int globalBefore = db.getTransactions().size();

        try {
            db.processOperations();
            System.out.println(
                "ERROR: expected an exception for non-existent source account, but none was thrown."
            );
        } catch (UnsupportedOperationException e) {
            System.out.println(
                "Correctly caught UnsupportedOperationException: " +
                e.getMessage()
            );
        } catch (RuntimeException e) {
            System.out.println(
                "Caught RuntimeException (implementation-dependent): " +
                e.getMessage()
            );
        }

        int globalAfter = db.getTransactions().size();
        System.out.println(
            "Transactions before/after invalid operation: " +
            globalBefore +
            " -> " +
            globalAfter
        );

        System.out.println("End of invalid-account scenario");
    }
}
