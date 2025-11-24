package bank_unit.db;

import static org.junit.jupiter.api.Assertions.*;

import bank.db.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.Test;

public class TestBankDB {

    /**
     * A test double replacing all DB access.
     * We override fetchBanks(), fetchBranches(), etc.
     * The real constructor still runs but connection is unused.
     */
    static class FakeBankDB extends BankDB {
        // In-memory fake data
        Map<Integer, Bank> fakeBanks = new HashMap<>();
        Map<Integer, Branch> fakeBranches = new HashMap<>();
        Map<Integer, Customer> fakeCustomers = new HashMap<>();
        Map<Integer, Account> fakeAccounts = new HashMap<>();
        Map<Integer, Transaction> fakeTransactions = new HashMap<>();

        public FakeBankDB() throws SQLException {
            super(
                "localhost",
                Optional.empty(),
                "test",
                "user",
                Optional.empty()
            );
        }

        /* ------------ Override fetch methods ------------ */

        @Override
        protected Map<Integer, Bank> fetchBanks() {
            return fakeBanks;
        }

        @Override
        protected Map<Integer, Branch> fetchBranches(Map<Integer, Bank> banks) {
            return fakeBranches;
        }

        @Override
        protected Map<Integer, Customer> fetchCustomers(
            Map<Integer, Branch> branches
        ) {
            return fakeCustomers;
        }

        @Override
        protected Map<Integer, Account> fetchAccounts(
            Map<Integer, Customer> customers
        ) {
            return fakeAccounts;
        }

        @Override
        protected Map<Integer, Transaction> fetchTransactions(
            Map<Integer, Account> accounts
        ) {
            return fakeTransactions;
        }

        /* ------------ Helper methods to populate fake data ------------ */
        void addBank(Bank b) {
            fakeBanks.put(b.getId(), b);
        }

        void addBranch(Branch br) {
            fakeBranches.put(br.getId(), br);
            br.getBank().addBranch(br);
        }

        void addCustomer(Customer c) {
            fakeCustomers.put(c.getId(), c);
            c.getBranch().addCustomer(c);
        }

        void addAccount(Account a) {
            fakeAccounts.put(a.getId(), a);
            a.getCustomer().addAccount(a);
        }

        void addTransaction(Transaction t) {
            fakeTransactions.put(t.getId(), t);
            t.getInfo().getSource().addTransaction(t);
        }
    }

    // ============================================================
    //                    TEST: connect()
    // ============================================================

    @Test
    void testHierarchyPreload() throws Exception {
        FakeBankDB db = new FakeBankDB();

        // --- Create fake data ---
        Bank bank = new Bank(1, "TestBank");
        db.addBank(bank);

        Branch branch = new Branch(10, "Addr", bank);
        db.addBranch(branch);

        Customer cust = new Customer(
            100,
            "Alice",
            "Smith",
            LocalDate.of(1990, 1, 1),
            "111",
            "555",
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

        Transaction tx = new Transaction(
            300,
            new TransactionInfo(
                acc,
                acc, // self-transfer to simplify test
                BigDecimal.valueOf(50),
                LocalDateTime.now()
            )
        );
        db.addTransaction(tx);

        // --- Run connect() which calls overridden fetch methods ---
        db.connect();

        // --- Assertions ---
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

    // ============================================================
    //                   TEST: customerLogin
    // ============================================================

    @Test
    void testCustomerLogin() throws Exception {
        FakeBankDB db = new FakeBankDB();

        // Create fake customer
        Bank b = new Bank(1, "B");
        Branch br = new Branch(10, "A", b);
        Customer c = new Customer(
            99,
            "John",
            "Doe",
            LocalDate.of(1995, 5, 5),
            "111",
            "555",
            "john@x.com",
            br
        );

        db.addBank(b);
        db.addBranch(br);
        db.addCustomer(c);

        // But IMPORTANT: customerLogin executes SQL, so override it here:
        db =
            new FakeBankDB() {

                @Override
                public Optional<Customer> customerLogin(
                    String email,
                    String pwd
                ) {
                    if (email.equals("john@x.com") && pwd.equals("secret")) {
                        return Optional.of(c);
                    }
                    return Optional.empty();
                }
            };

        Optional<Customer> result = db.customerLogin("john@x.com", "secret");

        assertTrue(result.isPresent());
        assertEquals(c, result.get());
    }

    // ============================================================
    //                   TEST: search function
    // ============================================================

    @Test
    void testCustomerSearch() throws Exception {
        FakeBankDB db = new FakeBankDB();

        // Fake customers
        Bank b = new Bank(1, "B");
        Branch br = new Branch(10, "A", b);

        Customer c1 = new Customer(
            1,
            "Alice",
            "A",
            LocalDate.of(1990, 1, 1),
            "111",
            "1",
            "alice@test.com",
            br
        );
        Customer c2 = new Customer(
            2,
            "Bob",
            "B",
            LocalDate.of(1980, 1, 1),
            "222",
            "2",
            "bob@test.com",
            br
        );

        db.addBank(b);
        db.addBranch(br);
        db.addCustomer(c1);
        db.addCustomer(c2);

        // Override SQL-based search:
        db =
            new FakeBankDB() {

                @Override
                public List<Customer> getCustomersSearch(String[] query) {
                    String q = query[0].toLowerCase();
                    List<Customer> res = new ArrayList<>();
                    for (Customer c : List.of(c1, c2)) {
                        if (
                            c.getFirstName().toLowerCase().contains(q) ||
                            c.getLastName().toLowerCase().contains(q) ||
                            c.getEmail().toLowerCase().contains(q)
                        ) {
                            res.add(c);
                        }
                    }
                    return res;
                }
            };

        List<Customer> found = db.getCustomersSearch(new String[] { "ali" });
        assertEquals(1, found.size());
        assertEquals(c1, found.get(0));
    }
}
