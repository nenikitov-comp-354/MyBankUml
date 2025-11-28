package bank_integrations.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bank.db.*;
import bank.db.operation.OperationLock;
import java.sql.SQLException;
import java.util.*;
import org.junit.jupiter.api.Test;

public class TestAdminView {

    @Test
    void adminViewIntegration() throws Exception {
        BankDb db = createDb();
        db.connect();
        run(db);
    }

    //for optional console run
    public static void main(String[] args) throws Exception {
        BankDb db = createDb();
        db.connect();
        run(db);
    }

    // --- helper functions for test ---

    private static BankDb createDb() {
        return new BankDb(
            "localhost",
            Optional.empty(),
            "bank",
            "admin",
            Optional.of("admin")
        );
    }

    /**
     * @brief handles the Admin console (View)
     * @throws SQLException
     */
    public static void run(BankDb db) throws SQLException {
        // System.out.println("\n=== ADMIN VIEW INTEGRATION TEST ===");
        // Login as an admin user
        Optional<Customer> maybeAdmin = db.customerLogin(
            "harry.styles@email.com",
            "harry"
        );

        assertTrue(
            maybeAdmin.isPresent(),
            "[AdminView] Admin login failed for harry.styles@email.com"
        );

        Customer admin = maybeAdmin.get();
        // System.out.println("[AdminView] Logged in as admin: " + admin);
        // System.out.println("  isAdmin = " + admin.isAdmin());

        assertTrue(
            admin.isAdmin(),
            "[AdminView] Logged-in user is not marked as admin"
        );
        // Show what an admin view

        // System.out.println("\n--- Banks ---");
        Map<Integer, Bank> banks = db.getBanks();
        // for (Bank bank : banks.values()) {
        //     System.out.println("  " + bank);
        // }
        assertTrue(
            !banks.isEmpty(),
            "[AdminView] Expected at least one bank in the system"
        );

        // System.out.println("\n--- Branches ---");
        Map<Integer, Branch> branches = db.getBranches();
        // for (Branch branch : branches.values()) {
        //     System.out.println("  " + branch);
        // }
        assertTrue(
            !branches.isEmpty(),
            "[AdminView] Expected at least one branch in the system"
        );

        // System.out.println("\n--- Customers ---");
        Map<Integer, Customer> customers = db.getCustomers();
        // for (Customer customer : customers.values()) {
        //     System.out.println("  " + customer);
        // }
        assertTrue(
            !customers.isEmpty(),
            "[AdminView] Expected at least one customer in the system"
        );

        // System.out.println("\n--- Accounts ---");
        Map<Integer, Account> accounts = db.getAccounts();
        // for (Account acc : accounts.values()) {
        //     System.out.println("  " + acc);
        // }
        assertTrue(
            !accounts.isEmpty(),
            "[AdminView] Expected at least one account in the system"
        );

        // Demonstrate an admin operation: lock the first account

        Account target = accounts.values().iterator().next();
        // System.out.println(
        //     "\n[AdminView] Locking account id=" + target.getId()
        // );
        boolean before = target.isLocked();

        db.addOperation(new OperationLock(target, true));
        db.processOperations();

        assertTrue(
            target.isLocked(),
            "[AdminView] Account was not locked after OperationLock(true)"
        );

        // If it was already locked, the state should stay true anyway
        if (before) {
            assertEquals(
                before,
                target.isLocked(),
                "[AdminView] Locking an already-locked account changed state unexpectedly"
            );
        }
        // System.out.println("=== End AdminView Integration ===");

    }
}
