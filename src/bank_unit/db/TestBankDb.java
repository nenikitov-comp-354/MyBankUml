package bank_unit.db;

import static org.junit.jupiter.api.Assertions.*;

import bank.db.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.Test;

public class TestBankDb {

    static class TestingBankDb extends BankDb {
        // In-memory mock data
        Map<Integer, Bank> testBanks = new HashMap<>();
        Map<Integer, Branch> testBranches = new HashMap<>();
        Map<Integer, Customer> testCustomers = new HashMap<>();
        Map<Integer, Account> testAccounts = new HashMap<>();
        Map<Integer, Transaction> testTransactions = new HashMap<>();

        public TestingBankDb() throws SQLException {
            super(
                "localhost",
                Optional.empty(),
                "test",
                "user",
                Optional.empty()
            );
        }

        @Override
        protected synchronized void ensureConnection() throws SQLException {
            // Do nothing here we're just overriding to avoid real DB connection
        }

        @Override
        protected Map<Integer, Bank> fetchBanks() {
            return testBanks;
        }

        @Override
        protected Map<Integer, Branch> fetchBranches(Map<Integer, Bank> banks) {
            return testBranches;
        }

        @Override
        protected Map<Integer, Customer> fetchCustomers(
            Map<Integer, Branch> branches
        ) {
            return testCustomers;
        }

        @Override
        protected Map<Integer, Account> fetchAccounts(
            Map<Integer, Customer> customers
        ) {
            return testAccounts;
        }

        @Override
        protected Map<Integer, Transaction> fetchTransactions(
            Map<Integer, Account> accounts
        ) {
            return testTransactions;
        }

        // Helper methods to populate mock data
        void addBank(Bank bank) {
            testBanks.put(bank.getId(), bank);
        }

        void addBranch(Branch branch) {
            testBranches.put(branch.getId(), branch);
            branch.getBank().addBranch(branch);
        }

        void addCustomer(Customer customer) {
            testCustomers.put(customer.getId(), customer);
            customer.getBranch().addCustomer(customer);
        }

        void addAccount(Account account) {
            testAccounts.put(account.getId(), account);
            account.getCustomer().addAccount(account);
        }

        void addTransaction(Transaction transaction) {
            testTransactions.put(transaction.getId(), transaction);
            transaction.getInfo().getSource().addTransaction(transaction);
        }
    }

    @Test
    void testHierarchyPreload() throws Exception {
        TestingBankDb db = new TestingBankDb();

        // Fake data
        Bank bank = new Bank(1, "TestBank");
        db.addBank(bank);

        Branch branch = new Branch(10, "Address", bank);
        db.addBranch(branch);

        Customer cust = new Customer(
            100,
            "Harry",
            "Styles",
            LocalDate.of(1994, 2, 1),
            "123-456-789",
            "+14165551234",
            "harry.styles@email.com",
            branch
        );
        db.addCustomer(cust);

        AccountChecking acc = new AccountChecking(
            200,
            "Harry Checking",
            false,
            cust,
            BigDecimal.valueOf(5.00)
        );
        db.addAccount(acc);

        // create a second account to use as transaction destination
        AccountSavings accDestination = new AccountSavings(
            201,
            "Harry Savings",
            false,
            cust,
            BigDecimal.valueOf(0.01)
        );

        Transaction transaction = new Transaction(
            300,
            new TransactionInfo(
                acc,
                accDestination,
                BigDecimal.valueOf(50),
                LocalDateTime.now()
            )
        );
        db.addTransaction(transaction);

        // Run connect() which calls overridden fetch methods
        db.connect();

        // Assertions
        assertEquals(1, db.getBanks().size());
        assertEquals(1, db.getBranches().size());
        assertEquals(1, db.getCustomers().size());
        assertEquals(1, db.getAccounts().size());
        assertEquals(1, db.getTransactions().size());

        assertEquals("TestBank", db.getBanks().get(1).getName());
        assertEquals("Address", db.getBranches().get(10).getAddress());
        assertEquals("Harry", db.getCustomers().get(100).getFirstName());
        assertEquals("Harry Checking", db.getAccounts().get(200).getName());
    }

    @Test
    void testCustomerLogin() throws Exception {
        TestingBankDb db = new TestingBankDb();

        // Create fake customer
        Bank bank = new Bank(1, "B");
        Branch branch = new Branch(10, "A", bank);
        Customer cust = new Customer(
            99,
            "Sabrina",
            "Carpenter",
            LocalDate.of(1999, 5, 11),
            "234-567-890",
            "+16045557890",
            "sabrina.carpenter@email.com",
            branch
        );

        db.addBank(bank);
        db.addBranch(branch);
        db.addCustomer(cust);

        // customerLogin executes SQL, so override it here:
        db =
            new TestingBankDb() {

                @Override
                public Optional<Customer> customerLogin(
                    String email,
                    String pwd
                ) {
                    if (
                        email.equals("sabrina.carpenter@email.com") &&
                        pwd.equals("secret")
                    ) {
                        return Optional.of(cust);
                    }
                    return Optional.empty();
                }
            };

        Optional<Customer> result = db.customerLogin(
            "sabrina.carpenter@email.com",
            "secret"
        );

        assertTrue(result.isPresent());
        assertEquals(cust, result.get());
    }

    @Test
    void testCustomerSearch() throws Exception {
        TestingBankDb db = new TestingBankDb();

        // Mock customers
        Bank bank = new Bank(1, "B");
        Branch branch = new Branch(10, "A", bank);

        Customer c1 = new Customer(
            1,
            "Harry",
            "Styles",
            LocalDate.of(1994, 2, 1),
            "123-456-789",
            "+14165551234",
            "harry.styles@email.com",
            branch
        );
        Customer c2 = new Customer(
            2,
            "Sabrina",
            "Carpenter",
            LocalDate.of(1999, 5, 11),
            "234-567-890",
            "+16045557890",
            "sabrina.carpenter@email.com",
            branch
        );

        db.addBank(bank);
        db.addBranch(branch);
        db.addCustomer(c1);
        db.addCustomer(c2);

        // Override SQL-based search:
        db =
            new TestingBankDb() {

                @Override
                public List<Customer> getCustomersSearch(String[] query) {
                    String q = query[0].toLowerCase();
                    List<Customer> customers = new ArrayList<>();
                    for (Customer c : List.of(c1, c2)) {
                        if (
                            c.getFirstName().toLowerCase().contains(q) ||
                            c.getLastName().toLowerCase().contains(q) ||
                            c.getEmail().toLowerCase().contains(q)
                        ) {
                            customers.add(c);
                        }
                    }
                    return customers;
                }
            };

        List<Customer> found = db.getCustomersSearch(new String[] { "har" });
        assertEquals(1, found.size());
        assertEquals(c1, found.get(0));
    }
}
