package bank_integrations.db;

import bank.db.*;
import bank.db.operation.OperationLock;
import java.sql.SQLException;
import java.util.*;

public class TestAdminView {
    private final BankDb db;
    private final Scanner scanner;

    public AdminView(BankDb db) {
        this.db = db;
        this.scanner = new Scanner(System.in);
    }

    /**
     * @brief handles the Admin console (View)
     * @throws SQLException
     */
    public void run() throws SQLException {
        while (true) {
            System.out.println("\n=== ADMIN VIEW INTEGRATION TEST ===");
            System.out.println("1) Search customers");
            System.out.println("2) Lock account");
            System.out.println("3) Unlock account");
            System.out.println("0) Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    handleSearchCustomers();
                    break;
                case "2":
                    handleLockUnlock(true);
                    break;
                case "3":
                    handleLockUnlock(false);
                    break;
                case "0":
                    {
                        System.out.println("Exiting admin integration view.");
                        return;
                    }
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    /**
     *
     * @throws SQLException
     */
    private void handleSearchCustomers() throws SQLException {
        System.out.println("Enter search keywords (space separated): ");
        String line = scanner.nextLine().trim();

        if (line.isEmpty()) {
            System.out.println("No keywords entered.");
            return;
        }

        String[] queries = line.split("\\s+");
        List<Customer> results = db.getCustomersSearch(queries);

        if (results.isEmpty()) {
            System.out.println(
                "No Customer has been found using the keywords."
            );
            return;
        }

        System.out.println("\nMatching Customers found: ");
        for (Customer c : results) {
            System.out.printf(
                "ID=%d | %s %s | email=%s | bank=%s%n",
                c.getId(),
                c.getFirstName(),
                c.getLastName(),
                c.getEmail(),
                c.getBranch().getBank().getName()
            );
        }
    }

    /**
     * @brief will handle all lock operation for admins
     * @param lock
     * @throws SQLException
     */
    private void handleLockUnlock(boolean lock) throws SQLException {
        System.out.print(
            "Enter account ID to " + (lock ? "lock" : "unlock") + ": "
        );
        String input = scanner.nextLine().trim();

        int accountId;
        try {
            accountId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid account id.");
            return;
        }

        Account account = db.getAccounts().get(accountId);
        if (account == null) {
            System.out.println("No account found with id " + accountId);
            return;
        }

        db.addOperation(new OperationLock(account, lock));
        db.processOperations();

        System.out.printf(
            "Account %d is now %s%n",
            accountId,
            account.isLocked() ? "LOCKED" : "UNLOCKED"
        );
    }

    public static void main(String[] args) throws SQLException {
        BankDb db = new BankDb(
            "localhost",
            Optional.empty(),
            "bank",
            "admin",
            Optional.of("admin")
        );

        db.connect();

        AdminView view = new AdminView(db);
        view.run();
    }
}
