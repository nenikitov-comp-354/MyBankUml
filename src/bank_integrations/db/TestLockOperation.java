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

public class TestLockOperation {

    @Test
    void lockOperationIntegration() throws Exception {
        BankDb db = createDb();
        db.connect();
        runLockUnlock(db);
    }

    // for manual
    public static void main(String[] args) {
        try {
            BankDb db = createDb();
            db.connect();
            runLockUnlock(db);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -- helper functions --

    private static BankDb createDb() {
        return new BankDb(
            "localhost",
            Optional.<Integer>empty(),
            "bank",
            "admin",
            Optional.of("admin")
        );
    }

    /**
     * @brief ensures that queue can be written to,
     * locks account, unlocks it + NOOP test
     * @param db
     * @throws SQLException
     */
    private static void runLockUnlock(BankDb db) throws SQLException {
        // System.out.println("==== LockOperation Integration: lock/unlock ====");

        Map<Integer, Account> accounts = db.getAccounts();

        assertTrue(
            !accounts.isEmpty(),
            "Not enough accounts in DB to run this scenario."
        );

        Account target = accounts.values().iterator().next();
        int accountId = target.getId();

        // System.out.println("[LockOperation] Using account id=" + accountId);
        // System.out.println("  Initial isLocked = " + target.isLocked());

        boolean originalState = target.isLocked();
        boolean flippedState = !originalState;

        // test 1
        // System.out.println("\n--- TEST 1: flip lock state via OperationLock ---");
        // System.out.println("  Applying OperationLock(account, " + flippedState + ")");

        db.addOperation(new OperationLock(target, flippedState));
        db.processOperations();

        // assert in-memory
        assertEquals(
            flippedState,
            target.isLocked(),
            "In-memory state mismatch after OperationLock"
        );

        // assert stored in DB
        Account reloaded1 = reloadAccount(accountId);
        assertEquals(
            flippedState,
            reloaded1.isLocked(),
            "DB state mismatch after OperationLock"
        );

        // System.out.println("Flip state assertions passed");

        // test 2
        // System.out.println("\n--- TEST 2: NOOP when locking to current state ---");
        boolean currentState = target.isLocked();
        db.addOperation(new OperationLock(target, currentState));
        db.processOperations();

        // in-memory should remain same
        assertEquals(
            currentState,
            target.isLocked(),
            "In-memory state changed during NOOP OperationLock"
        );

        // DB should remain same
        Account reloaded2 = reloadAccount(accountId);
        assertEquals(
            currentState,
            reloaded2.isLocked(),
            "DB state changed during NOOP OperationLock"
        );
        // System.out.println("NOOP state assertions passed");

        // System.out.println("=== End LockOperation Integration ===");
    }

    /**
     * Loads account fresh from DB for verification.
     */
    private static Account reloadAccount(int accountId) throws SQLException {
        BankDb verifyDb = createDb();
        verifyDb.connect();
        return verifyDb.getAccounts().get(accountId);
    }
}
