package bank_integrations.db;

import bank.db.BankDb;
import bank.db.Customer;
import java.sql.SQLException;
import java.util.Optional;

public class TestUserLogin {

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

            runValidLogin(db);
            runInvalidLogin(db);
        } catch (Exception e) {
            System.err.println("[UserLogin] Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Scenario 1: valid logins (admin + non-admin).
     */
    private static void runValidLogin(BankDb db) throws SQLException {
        System.out.println("UserLogin Integration: valid logins");

        // Admin user: Harry Styles (id=1, is_admin = true, password 'harry')
        testSuccessfulLogin(db, "harry.styles@email.com", "harry", true);

        // Non-admin user: Sabrina Carpenter (id=2, is_admin = false, password 'my')
        testSuccessfulLogin(db, "sabrina.carpenter@email.com", "my", false);

        System.out.println("End of valid login scenarios\n");
    }

    /**
     * Scenario 2: invalid credentials (wrong password + unknown email).
     */
    private static void runInvalidLogin(BankDb db) throws SQLException {
        System.out.println("UserLogin Integration: invalid logins");

        // Existing email, wrong password
        testFailedLogin(
            db,
            "harry.styles@email.com",
            "WRONG_PASSWORD",
            "existing email with wrong password"
        );

        // Non-existent email
        testFailedLogin(
            db,
            "nonexistent.user@email.com",
            "whatever",
            "non-existent email"
        );

        System.out.println(" End of invalid login scenarios\n");
    }

    // helper methods for login check

    /**
     *
     * @param db
     * @param email
     * @param password
     * @param expectedAdmin
     * @throws SQLException
     */
    private static void testSuccessfulLogin(
        BankDb db,
        String email,
        String password,
        boolean expectedAdmin
    )
        throws SQLException {
        System.out.println("\n[Valid] Trying login for: " + email);

        Optional<Customer> Customer1 = db.customerLogin(email, password);

        if (Customer1.isEmpty()) {
            System.out.println(
                " ERROR: expected successful login, got Optional.empty()"
            );
            return;
        }

        Customer customer = Customer1.get();
        System.out.println("Login successful. Customer: " + customer);
        System.out.println("isAdmin = " + customer.isAdmin());
        System.out.println("id      = " + customer.getId());

        if (customer.isAdmin() != expectedAdmin) {
            System.out.println(
                " ERROR: expected isAdmin=" +
                expectedAdmin +
                " but was " +
                customer.isAdmin()
            );
        }
    }

    /**
     *
     * @param db
     * @param email
     * @param password
     * @param description
     * @throws SQLException
     */
    private static void testFailedLogin(
        BankDb db,
        String email,
        String password,
        String description
    )
        throws SQLException {
        System.out.println(
            "\n[Invalid] Trying login (" + description + "): " + email
        );

        Optional<Customer> Customer = db.customerLogin(email, password);

        if (Customer.isPresent()) {
            System.out.println(
                "  ERROR: expected failed login but got: " + Customer.get()
            );
        } else {
            System.out.println(
                "  Correctly failed to login (Optional.empty())."
            );
        }
    }
}
