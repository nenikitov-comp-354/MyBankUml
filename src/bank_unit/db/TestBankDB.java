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

    /**
     * A test double replacing all DB access.
     * We override fetchBanks(), fetchBranches(), etc.
     * The real constructor still runs but connection is unused.
     */
    static class TestingBankDb extends BankDb {
        // In-memory fake data
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

        // Helper methods to populate fake data
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

        Branch branch = new Branch(10, "Addr", bank);
        db.addBranch(branch);

        Customer cust = new Customer(
            100,
            "Alice",
            "Smith",
            LocalDate.of(1990, 1, 1),
            "111-111-111",
            "+15550000001",
            "alice@test.com",
            branch
        );
        db.addCustomer(cust);

        AccountChecking acc = new AccountChecking(
            200,
            "Alice Checking",
            false,
            cust,
            BigDecimal.valueOf(5.00)
        );
        db.addAccount(acc);

        // create a second account to use as transaction destination
        AccountSavings accDestination = new AccountSavings(
            201,
            "Alice Savings",
            false,
            cust,
            BigDecimal.valueOf(0.01)
        );

        Transaction transaction = new Transaction(
            300,
            new TransactionInfo(
                acc,
                accDestination, // use separate destination account
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
        assertEquals("Addr", db.getBranches().get(10).getAddress());
        assertEquals("Alice", db.getCustomers().get(100).getFirstName());
        assertEquals("Alice Checking", db.getAccounts().get(200).getName());
    }

    @Test
    void testCustomerLogin() throws Exception {
        TestingBankDb db = new TestingBankDb();

        // Create fake customer
        Bank bank = new Bank(1, "B");
        Branch branch = new Branch(10, "A", bank);
        Customer cust = new Customer(
            99,
            "John",
            "Doe",
            LocalDate.of(1995, 5, 5),
            "111-111-111",
            "+15550000002",
            "john@x.com",
            branch
        );

        db.addBank(bank);
        db.addBranch(branch);
        db.addCustomer(cust);

        // customerLogin executes SQL, so override it here:
        db = new TestingBankDb() {

                @Override
                public Optional<Customer> customerLogin(
                    String email,
                    String pwd
                ) {
                    if (email.equals("john@x.com") && pwd.equals("secret")) {
                        return Optional.of(cust);
                    }
                    return Optional.empty();
                }
            };

        Optional<Customer> result = db.customerLogin("john@x.com", "secret");

        assertTrue(result.isPresent());
        assertEquals(cust, result.get());
    }

    @Test
    void testCustomerSearch() throws Exception {
        TestingBankDb db = new TestingBankDb();

        // Fake customers
        Bank bank = new Bank(1, "B");
        Branch branch = new Branch(10, "A", bank);

        Customer c1 = new Customer(
            1,
            "Alice",
            "A",
            LocalDate.of(1990, 1, 1),
            "111-111-111",
            "+15550000003",
            "alice@test.com",
            branch
        );
        Customer c2 = new Customer(
            2,
            "Bob",
            "B",
            LocalDate.of(1980, 1, 1),
            "222-222-222",
            "+15550000004",
            "bob@test.com",
            branch
        );

        db.addBank(bank);
        db.addBranch(branch);
        db.addCustomer(c1);
        db.addCustomer(c2);

        // Override SQL-based search:
        db = new TestingBankDb() {

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

        List<Customer> found = db.getCustomersSearch(new String[] { "ali" });
        assertEquals(1, found.size());
        assertEquals(c1, found.get(0));
    }
}
