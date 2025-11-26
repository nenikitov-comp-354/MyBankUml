package bank_integrations.db;

import bank.db.*;
import bank.db.operation.OperationLock;
import bank.db.operation.Operation;
import bank.db.operation.OperationTransaction;

import java.sql.SQLException;
import java.util.*;
import java.time.LocalDate;

public class OperationQueue {
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


    private static void runBasicQueue(BankDb db) throws SQLException {
        System.out.println("Operation Queue Integration: Basic Queue");

        // Getting two existing accounts from the DB
        Map<Integer, Account> accounts = db.getAccounts();
        if (accounts.size() < 2) {
            System.out.println(
                "Not enough accounts in the database to run this scenario."
            );
            return;
        }

        Iterator<Account> it = accounts.values().iterator();
        Account acc1 = it.next();
        Account acc2 = it.next();

        System.out.println("Using accounts:");
        System.out.println("  acc1 = " + acc1);
        System.out.println("  acc2 = " + acc2);
        System.out.println(
            "Initial balances: acc1=" +
            acc1.getBalance() +
            ", acc2=" +
            acc2.getBalance()
        );

        // creating new transactions
        TransactionInfo tx1Info =
            new TransactionInfo(
                acc1,
                acc2,
                new BigDecimal("10.00"),
                LocalDateTime.now()
            );
        TransactionInfo tx2Info =
            new TransactionInfo(
                acc2,
                acc1,
                new BigDecimal("5.00"),
                LocalDateTime.now().plusSeconds(1)
            );

        OperationTransaction op1 = new OperationTransaction(tx1Info);
        OperationTransaction op2 = new OperationTransaction(tx2Info);

        // new lock operation
        OperationLock lockAcc1 = new OperationLock(acc1, true);

        // adding  new operations
        db.addOperation(op1);
        db.addOperation(op2);
        db.addOperation(lockAcc1);

        // process operations
        System.out.println("Processing operations in queue");
        db.processOperations();
        System.out.println("Done processing.");

        // get final balance
        System.out.println(
            "Final balances: acc1=" +
            acc1.getBalance() +
            ", acc2=" +
            acc2.getBalance()
        );
        System.out.println("Lock status of acc1: " + acc1.isLocked());

        System.out.println("Transactions in BankDb:");
        for (Transaction t : db.getTransactions().values()) {
            System.out.println("  " + t);
        }
    }

    // second test where there is concurrency
    private static void runConcurrency(BankDb db) throws SQLException, InterruptedException {
        System.out.println("\nOperation Queue Integration: Concurrency");

        Map<Integer, Account> accounts = db.getAccounts();
        if (accounts.size() < 2) {
            System.out.println(
                "Not enough accounts in the database to run concurrency scenario."
            );
            return;
        }

        Iterator<Account> it = accounts.values().iterator();
        Account acc1 = it.next();
        Account acc2 = it.next();

        System.out.println("Using accounts for concurrency:");
        System.out.println("  acc1 = " + acc1);
        System.out.println("  acc2 = " + acc2);

        // Two threads adding operations to the same queue
        Thread t1 = new Thread(() -> {
            try {
                TransactionInfo info =
                    new TransactionInfo(
                        acc1,
                        acc2,
                        new BigDecimal("2.50"),
                        LocalDateTime.now()
                    );
                db.addOperation(new OperationTransaction(info));
            } catch (Exception e) {
                System.err.println(
                    "[Concurrency t1] Error adding operation: " + e.getMessage()
                );
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                TransactionInfo info =
                    new TransactionInfo(
                        acc2,
                        acc1,
                        new BigDecimal("1.25"),
                        LocalDateTime.now().plusSeconds(1)
                    );
                db.addOperation(new OperationTransaction(info));
            } catch (Exception e) {
                System.err.println(
                    "[Concurrency t2] Error adding operation: " + e.getMessage()
                );
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        // Process whatever is currently in the queue
        System.out.println("Processing operations added by both threads");
        db.processOperations();
        System.out.println("Done processing concurrency scenario.");

        System.out.println(
            "Balances after concurrency scenario: acc1=" +
            acc1.getBalance() +
            ", acc2=" +
            acc2.getBalance()
        );
    }
}
