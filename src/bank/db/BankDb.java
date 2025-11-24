package bank.db;

import bank.db.DAO.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

/**
 * In-memory bank repository backed by the DB.
 * Loads all banks, branches, customers, accounts, and transactions on startup.
 * Keeps the cache in sync on mutations.
 */
public class BankDB {
    protected Connection connection;
    protected BankDAO bankDao;
    protected BranchDAO branchDao;
    protected CustomerDAO customerDao;
    protected AccountDAO accountDao;
    protected AccountCheckingDAO accountCheckingDao;
    protected AccountSavingsDAO accountSavingsDao;
    protected AccountCreditDAO accountCreditDao;
    protected TransactionDAO transactionDao;

    protected final Map<Integer, Bank> banks = new HashMap<>();

    // Allow tests to instantiate a subclass without creating a DB connection.
    protected BankDB() {
        // Intentionally empty for tests — test code will set DAOs/connection directly.
    }

    public BankDB(
        String host,
        Optional<Integer> port,
        String database,
        String user,
        Optional<String> password
    )
        throws SQLException {
        String url =
            "jdbc:postgresql://" +
            host +
            port.map(p -> ":" + p).orElse("") +
            "/" +
            database;
        Properties props = new Properties();
        props.setProperty("user", user);
        password.ifPresent(p -> props.setProperty("password", p));

        this.connection = DriverManager.getConnection(url, props);

        // Initialize DAOs
        this.bankDao = new BankDAO(connection);
        this.branchDao = new BranchDAO(connection);
        this.customerDao = new CustomerDAO(connection);
        this.accountDao = new AccountDAO(connection, this.customerDao);
        this.accountCheckingDao =
            new AccountCheckingDAO(connection, this.customerDao);
        this.accountSavingsDao =
            new AccountSavingsDAO(connection, this.customerDao);
        this.accountCreditDao =
            new AccountCreditDAO(connection, this.customerDao);
        this.transactionDao = new TransactionDAO(connection, this.accountDao);

        // Preload full hierarchy
        preloadAll();
    }

    /**
     * Preload all data hierarchically into memory (All data into respecitive branch and bank).
     */
    protected void preloadAll() throws SQLException {
        List<Bank> bankList = bankDao.findAll();
        for (Bank bank : bankList) {
            banks.put(bank.getId(), bank);

            List<Branch> branches = branchDao.findByBankId(bank.getId());
            for (Branch branch : branches) {
                bank.addBranch(branch);

                List<Customer> customers = customerDao.findByBranchId(
                    branch.getId()
                );
                for (Customer customer : customers) {
                    branch.addCustomer(customer);

                    List<Account> accounts = accountDao.findByCustomerId(
                        customer.getId()
                    );
                    for (Account account : accounts) {
                        customer.addAccount(account);

                        // Load transactions for this account
                        List<Transaction> txs = transactionDao.findByAccountId(
                            account.getId()
                        );
                        for (Transaction t : txs) {
                            account.addTransaction(t);
                        }
                    }
                }
            }
        }
    }

    // ----- Access Methods -----
    public Collection<Bank> getBanks() {
        return Collections.unmodifiableCollection(banks.values());
    }

    public Optional<Bank> getById(int id) {
        return Optional.ofNullable(banks.get(id));
    }

    public Bank create(Bank bank) throws SQLException {
        Bank created = bankDao.insert(bank);
        banks.put(created.getId(), created);
        return created;
    }

    public boolean update(Bank bank) throws SQLException {
        boolean ok = bankDao.update(bank);
        if (ok) banks.put(bank.getId(), bank);
        return ok;
    }

    public boolean delete(int id) throws SQLException {
        boolean ok = bankDao.delete(id);
        if (ok) banks.remove(id);
        return ok;
    }

    public void reload() throws SQLException {
        banks.clear();
        preloadAll();
    }
}
