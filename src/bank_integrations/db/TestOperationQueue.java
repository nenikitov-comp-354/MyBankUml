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

public class TestOperationQueue {

    @Test
    void operationQueueIntegration() throws Exception {
        BankDb db = new BankDb(
            "localhost",
            Optional.empty(),
            "bank",
            "admin",
            Optional.of("admin")
        );
        db.connect();

        runBasicQueue(db);
        runConcurrency(db);
    }

    // optional manual run
    public static void main(String[] args) throws Exception {
        BankDb db = new BankDb(
            "localhost",
            Optional.empty(),
            "bank",
            "admin",
            Optional.of("admin")
        );

        try {
            db.connect();
            runBasicQueue(db);
            runConcurrency(db);
        } catch (Exception e) {
            System.err.println("[OperationQueue] Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @brief 2 transactions, one lock operation, assert
     * balances, lock state  and transaction count
     * @param db
     * @throws SQLException
     */
    private static void runBasicQueue(BankDb db) throws SQLException {
        // System.out.println("Operation Queue Integration: Basic Queue");

        // Getting two existing accounts from the DB
        Map<Integer, Account> accounts = db.getAccounts();
        assertTrue(
            accounts.size() >= 2,
            "Not enough accounts in the database to run this scenario."
        );

        // Assigns both accounts
        Iterator<Account> it = accounts.values().iterator();
        Account acc1 = it.next();
        Account acc2 = it.next();

        // System.out.println("Using accounts:");
        // System.out.println("  acc1 = " + acc1);
        // System.out.println("  acc2 = " + acc2);
        // System.out.println(
        //     "Initial balances: acc1=" +
        //     acc1.getBalance() +
        //     ", acc2=" +
        //     acc2.getBalance()
        // );

        BigDecimal initial1 = acc1.getBalance();
        BigDecimal initial2 = acc2.getBalance();
        BigDecimal amount1 = new BigDecimal("10.00");
        BigDecimal amount2 = new BigDecimal("5.00");

        // creating new transactions
        TransactionInfo trInfo1 = new TransactionInfo(
            acc1,
            acc2,
            amount1,
            LocalDateTime.now()
        );
        TransactionInfo trInfo2 = new TransactionInfo(
            acc2,
            acc1,
            amount2,
            LocalDateTime.now().plusSeconds(1)
        );

        OperationTransaction op1 = new OperationTransaction(trInfo1);
        OperationTransaction op2 = new OperationTransaction(trInfo2);

        // new lock operation
        OperationLock lockAcc1 = new OperationLock(acc1, true);

        int txCountBefore = db.getTransactions().size();

        // adding new operations to the queue
        db.addOperation(op1);
        db.addOperation(op2);
        db.addOperation(lockAcc1);

        // process operations
        // System.out.println("Processing operations in queue");
        db.processOperations();
        // System.out.println("Done processing.");

        // get final balance and lock status to confirm queue operations
        // System.out.println(
        //     "Final balances: acc1=" +
        //     acc1.getBalance() +
        //     ", acc2=" +
        //     acc2.getBalance()
        // );
        // System.out.println("Lock status of acc1: " + acc1.isLocked());

        // System.out.println("Transactions in BankDb:");

        BigDecimal final1 = acc1.getBalance();
        BigDecimal final2 = acc2.getBalance();
        // for (Transaction t : db.getTransactions().values()) {
        //     System.out.println("  " + t);
        // }

        // ASSERT: at least one of the balances changed
        boolean acc1Changed = final1.compareTo(initial1) != 0;
        boolean acc2Changed = final2.compareTo(initial2) != 0;
        assertTrue(
            acc1Changed || acc2Changed,
            "Expected at least one account balance to change after queue processing."
        );

        // ASSERT: acc1 is locked
        assertTrue(
            acc1.isLocked(),
            "acc1 should be locked after OperationLock(true)."
        );

        // ASSERT: two new transactions were recorded
        int txCountAfter = db.getTransactions().size();
        assertEquals(
            txCountBefore + 2,
            txCountAfter,
            "Expected 2 new transactions after basic queue operations."
        );
    }

    /**
     * @brief second test where there is concurrency
     * @param db
     * @throws SQLException
     * @throws InterruptedException
     */
    private static void runConcurrency(BankDb db)
        throws SQLException, InterruptedException {
        // System.out.println("\nOperation Queue Integration: Concurrency");

        Map<Integer, Account> accounts = db.getAccounts();
        assertTrue(
            accounts.size() >= 2,
            "Not enough accounts in the database to run concurrency scenario."
        );

        // assign accounts
        Iterator<Account> it = accounts.values().iterator();
        Account acc1 = it.next();
        Account acc2 = it.next();

        // System.out.println("Using accounts for concurrency:");
        // System.out.println("  acc1 = " + acc1);
        // System.out.println("  acc2 = " + acc2);

        //  new balance and amount vars
        BigDecimal initial1 = acc1.getBalance();
        BigDecimal initial2 = acc2.getBalance();

        BigDecimal amountT1 = new BigDecimal("2.50");
        BigDecimal amountT2 = new BigDecimal("1.25");

        int txCountBefore = db.getTransactions().size();

        // Two threads adding operations to the same queue
        Thread t1 = new Thread(
            () -> {
                try {
                    TransactionInfo info = new TransactionInfo(
                        acc1,
                        acc2,
                        amountT1,
                        LocalDateTime.now()
                    );
                    db.addOperation(new OperationTransaction(info));
                } catch (Exception e) {
                    System.err.println(
                        "[Concurrency t1] Error adding operation: " +
                        e.getMessage()
                    );
                }
            }
        );

        Thread t2 = new Thread(
            () -> {
                try {
                    TransactionInfo info = new TransactionInfo(
                        acc2,
                        acc1,
                        amountT2,
                        LocalDateTime.now().plusSeconds(1)
                    );
                    db.addOperation(new OperationTransaction(info));
                } catch (Exception e) {
                    System.err.println(
                        "[Concurrency t2] Error adding operation: " +
                        e.getMessage()
                    );
                }
            }
        );

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        // Process all inside the queue
        // System.out.println("Processing operations added by both threads");
        db.processOperations();
        // System.out.println("Done processing concurrency scenario.");

        // output should be  acc1 -= 1.25, acc2 += 1.25
        // System.out.println(
        //     "Balances after concurrency scenario: acc1=" +
        //     acc1.getBalance() +
        //     ", acc2=" +
        //     acc2.getBalance()
        // );

        // NEW ASSERTIONS
        BigDecimal final1 = acc1.getBalance();
        BigDecimal final2 = acc2.getBalance();

        // ASSERT: at least one of the balances changed
        boolean acc1Changed = final1.compareTo(initial1) != 0;
        boolean acc2Changed = final2.compareTo(initial2) != 0;
        assertTrue(
            acc1Changed || acc2Changed,
            "Expected at least one account balance to change in concurrency scenario."
        );

        // ASSERT: two new transactions recorded
        int txCountAfter = db.getTransactions().size();
        assertEquals(
            txCountBefore + 2,
            txCountAfter,
            "Expected 2 new transactions after concurrency scenario."
        );
    }
}
