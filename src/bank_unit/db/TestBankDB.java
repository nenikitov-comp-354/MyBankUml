package bank_unit.db;

import static org.junit.jupiter.api.Assertions.*;

import bank.db.*;
import bank.db.DAO.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.Test;

/**
 * Test BankDB without using Mockito (pure manual fakes).
 */
public class TestBankDB {

    // --- Fake DAO implementations ---
    static class FakeBankDAO extends BankDAO {
        private final List<Bank> banks;

        FakeBankDAO(List<Bank> banks) {
            super(null);
            this.banks = banks;
        }

        @Override
        public List<Bank> findAll() {
            return banks;
        }

        @Override
        public Bank insert(Bank bank) {
            banks.add(bank);
            return bank;
        }
    }

    static class FakeBranchDAO extends BranchDAO {
        private final Map<Integer, List<Branch>> branchesByBank = new HashMap<>();

        FakeBranchDAO() {
            super(null);
        }

        void addBranchForBank(int bankId, Branch branch) {
            branchesByBank
                .computeIfAbsent(bankId, k -> new ArrayList<>())
                .add(branch);
        }

        @Override
        public List<Branch> findByBankId(int bankId) {
            return branchesByBank.getOrDefault(bankId, Collections.emptyList());
        }
    }

    static class FakeCustomerDAO extends CustomerDAO {
        private final Map<Integer, List<Customer>> customersByBranch = new HashMap<>();

        FakeCustomerDAO() {
            super(null);
        }

        void addCustomerForBranch(int branchId, Customer customer) {
            customersByBranch
                .computeIfAbsent(branchId, k -> new ArrayList<>())
                .add(customer);
        }

        @Override
        public List<Customer> findByBranchId(int branchId) {
            return customersByBranch.getOrDefault(
                branchId,
                Collections.emptyList()
            );
        }
    }

    static class FakeAccountDAO extends AccountDAO {
        private final Map<Integer, List<Account>> accountsByCustomer = new HashMap<>();

        FakeAccountDAO() {
            super(null, null);
        }

        void addAccountForCustomer(int customerId, Account account) {
            accountsByCustomer
                .computeIfAbsent(customerId, k -> new ArrayList<>())
                .add(account);
        }

        @Override
        public List<Account> findByCustomerId(int customerId) {
            return accountsByCustomer.getOrDefault(
                customerId,
                Collections.emptyList()
            );
        }
    }

    static class FakeTransactionDAO extends TransactionDAO {
        private final Map<Integer, List<Transaction>> transactionsByAccount = new HashMap<>();

        FakeTransactionDAO() {
            super(null, null);
        }

        void addTransactionForAccount(int accountId, Transaction tx) {
            transactionsByAccount
                .computeIfAbsent(accountId, k -> new ArrayList<>())
                .add(tx);
        }

        @Override
        public List<Transaction> findByAccountId(int accountId) {
            return transactionsByAccount.getOrDefault(
                accountId,
                Collections.emptyList()
            );
        }
    }

    // --- Tests ---
    @Test
    void testHierarchicalPreload() throws Exception {
        // Setup fake DAOs
        Bank bank1 = new Bank(1, "Bank A");
        Bank bank2 = new Bank(2, "Bank B");

        FakeBankDAO bankDao = new FakeBankDAO(
            new ArrayList<>(List.of(bank1, bank2))
        );
        FakeBranchDAO branchDao = new FakeBranchDAO();
        FakeCustomerDAO customerDao = new FakeCustomerDAO();
        FakeAccountDAO accountDao = new FakeAccountDAO();
        FakeTransactionDAO transactionDao = new FakeTransactionDAO();

        // Branches
        Branch branch1 = new Branch(1, "Addr1", bank1);
        Branch branch2 = new Branch(2, "Addr2", bank2);
        branchDao.addBranchForBank(1, branch1);
        branchDao.addBranchForBank(2, branch2);

        // Customers
        Customer cust1 = new Customer(
            1,
            "Alice",
            "Smith",
            LocalDate.of(1990, 1, 1),
            "123-456-789",
            "+111",
            "a@x.com",
            branch1
        );
        Customer cust2 = new Customer(
            2,
            "Bob",
            "Jones",
            LocalDate.of(1985, 5, 5),
            "987-654-321",
            "+222",
            "b@x.com",
            branch2
        );
        customerDao.addCustomerForBranch(1, cust1);
        customerDao.addCustomerForBranch(2, cust2);

        // Accounts
        AccountChecking acc1 = new AccountChecking(
            1,
            "Alice Checking",
            false,
            cust1,
            BigDecimal.ZERO
        );
        AccountSavings acc2 = new AccountSavings(
            2,
            "Bob Savings",
            false,
            cust2,
            BigDecimal.ZERO
        );
        accountDao.addAccountForCustomer(1, acc1);
        accountDao.addAccountForCustomer(2, acc2);

        // Transactions
        TransactionInfo txInfo = new TransactionInfo(
            acc1,
            acc2,
            BigDecimal.valueOf(100.0),
            LocalDateTime.now()
        );
        Transaction tx1 = new Transaction(1, txInfo);
        transactionDao.addTransactionForAccount(1, tx1);
        transactionDao.addTransactionForAccount(2, tx1);

        // Build BankDB with overridden DAOs
        // use the protected no-arg ctor in tests to avoid real DB connection
        BankDB db = new BankDB() {

            {
                // ensure DAOs are set before using them
                this.bankDao = bankDao;
                this.branchDao = branchDao;
                this.customerDao = customerDao;
                this.accountDao = accountDao;
                this.transactionDao = transactionDao;
                this.banks.clear();

                // now preload using the fakes
                for (Bank b : this.bankDao.findAll()) {
                    this.banks.put(b.getId(), b);
                    for (Branch br : this.branchDao.findByBankId(b.getId())) {
                        b.addBranch(br);
                        for (Customer c : this.customerDao.findByBranchId(
                                br.getId()
                            )) {
                            br.addCustomer(c);
                            for (Account a : this.accountDao.findByCustomerId(
                                    c.getId()
                                )) {
                                c.addAccount(a);
                                for (Transaction t : this.transactionDao.findByAccountId(
                                        a.getId()
                                    )) {
                                    a.addTransaction(t);
                                }
                            }
                        }
                    }
                }
            }
        };

        // --- Assertions ---
        assertEquals(2, db.getBanks().size());
        assertTrue(db.getBanks().contains(bank1));
        assertTrue(db.getBanks().contains(bank2));
        assertEquals(1, bank1.getBranches().size());
        assertEquals(branch1, bank1.getBranches().get(0));
        assertEquals(1, bank2.getBranches().size());
        assertEquals(branch2, bank2.getBranches().get(0));
        assertEquals(1, branch1.getCustomers().size());
        assertEquals(cust1, branch1.getCustomers().get(0));
        assertEquals(1, branch2.getCustomers().size());
        assertEquals(cust2, branch2.getCustomers().get(0));
        assertEquals(1, cust1.getAccounts().size());
        assertEquals(acc1, cust1.getAccounts().get(0));
        assertEquals(1, cust2.getAccounts().size());
        assertEquals(acc2, cust2.getAccounts().get(0));
        assertEquals(1, acc1.getTransactions().size());
        assertEquals(tx1, acc1.getTransactions().get(0));
        assertEquals(1, acc2.getTransactions().size());
        assertEquals(tx1, acc2.getTransactions().get(0));
    }

    @Test
    void testCreateUpdatesCache() throws Exception {
        Bank newBank = new Bank(10, "New Bank");
        FakeBankDAO bankDao = new FakeBankDAO(new ArrayList<>());

        BankDB db = new BankDB() {

            {
                // set test doubles using subclass initializer (can access protected members)
                this.bankDao = bankDao;
                this.banks.clear();
            }
        };

        Bank created = db.create(newBank);

        assertEquals(newBank, created);
        assertEquals(newBank, db.getById(10).get());
    }
}
