package bank_integrations.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bank.db.*;
import bank.db.operation.Operation;
import bank.db.operation.OperationLock;
import bank.db.operation.OperationTransaction;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.Test;

public class TestTransactionOperation {

    @Test
    void transactionOperationIntegration() throws Exception {
        BankDb db = new BankDb(
            "localhost",
            Optional.empty(),
            "bank",
            "admin",
            Optional.of("admin")
        );

        db.connect();

        runValid(db);
        runInvalid(db);
    }

    // optional
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
        // System.out.println("TransactionOperation Integration: valid transfer");

        Map<Integer, Account> accounts = db.getAccounts();

        assertTrue(
            accounts.size() >= 2,
            "Not enough accounts in DB to run valid transaction scenario."
        );

        Iterator<Account> it = accounts.values().iterator();
        Account source = it.next();
        Account destination = it.next();

        // System.out.println("Source account: " + source);
        // System.out.println("Destination account: " + destination);

        // tracking balance before:
        BigDecimal sourceBeforeBalance = source.getBalance();
        BigDecimal destBeforeBalance = destination.getBalance();

        // to track # of transactions
        int globalBefore = db.getTransactions().size();
        int sourceBefore = source.getTransactions().size();
        int destBefore = destination.getTransactions().size();

        // System.out.println(
        //     "Before: global=" +
        //     globalBefore +
        //     ", sourceTransactions=" +
        //     sourceBefore +
        //     ", destTransactions=" +
        //     destBefore
        // );

        BigDecimal amount = new BigDecimal("17.79");
        // Build transaction info
        TransactionInfo info = new TransactionInfo(
            source,
            destination,
            amount,
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

        BigDecimal sourceAfterBalance = source.getBalance();
        BigDecimal destAfterBalance = destination.getBalance();

        // System.out.println(
        //     "After: global=" +
        //     globalAfter +
        //     ", sourceTransactions=" +
        //     sourceAfter +
        //     ", destTransactions=" +
        //     destAfter
        // );

        // Print out newest transaction (if you want to visually confirm)
        // if (globalAfter > globalBefore) {
        //     System.out.println("New transactions in BankDb:");
        //     for (Transaction t : db.getTransactions().values()) {
        //         System.out.println("  " + t);
        //     }
        // }

        // System.out.println("End of valid transfer operation\n");

        // [New] Assertions
        // Transaction counts: exactly one new transaction globally
        assertEquals(
            globalBefore + 1,
            globalAfter,
            "Expected exactly 1 new transaction globally after valid operation."
        );

        // Source and destination should each have one new transaction
        assertEquals(
            sourceBefore + 1,
            sourceAfter,
            "Expected source account to have 1 more transaction."
        );
        assertTrue(
            destAfter >= destBefore,
            "Expected destination account to have 1 more transaction."
        );

        // Direction of balance changes
        assertTrue(
            sourceAfterBalance.compareTo(sourceBeforeBalance) <= 0,
            "Source balance should not increase after sending money."
        );
        assertTrue(
            destAfterBalance.compareTo(destBeforeBalance) >= 0,
            "Destination balance should not decrease after receiving money."
        );
    }

    /**
     * @brief will run invalid test, will create a fake account which isn't in DB as source
     * will use real account as destination
     * @param db
     * @throws SQLException
     */
    private static void runInvalid(BankDb db) throws SQLException {
        // System.out.println(
        //     " TransactionOperation Integration: invalid account "
        // );

        Map<Integer, Customer> customers = db.getCustomers();
        assertTrue(
            !customers.isEmpty(),
            "No customers in DB; cannot run invalid-account scenario."
        );

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
        assertTrue(
            !accounts.isEmpty(),
            "No real accounts in DB; cannot run invalid-account scenario."
        );
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

        boolean threw = false;
        try {
            db.processOperations();
            // System.out.println(
            //     "ERROR: expected an exception for non-existent source account, but none was thrown."
            // );
        } catch (SQLException e) {
            // System.out.println(
            //     "Correctly caught SQLException: " +
            //     e.getMessage()
            // );
            threw = true;
        } catch (UnsupportedOperationException e) {
            System.out.println(
                "Correctly caught UnsupportedOperationException: " +
                e.getMessage()
            );
            threw = true;
        } catch (RuntimeException e) {
            System.out.println(
                "Caught RuntimeException (implementation-dependent): " +
                e.getMessage()
            );
            threw = true;
        }

        // expected exception
        assertTrue(
            threw,
            "Expected an exception when processing transaction with non-existent source account."
        );

        int globalAfter = db.getTransactions().size();
        // System.out.println(
        //     "Transactions before/after invalid operation: " +
        //     globalBefore +
        //     " -> " +
        //     globalAfter
        // );

        // Invalid operation should not add any transaction to BankDb
        assertEquals(
            globalBefore,
            globalAfter,
            "Invalid transaction should not be recorded in BankDb."
        );
        // System.out.println("End of invalid-account scenario");
    }
}
