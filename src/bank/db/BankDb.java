package bank.db;

import java.sql.*;
import java.util.*;

public class BankDb {
    private Connection connection;

    // store params so connection can be created lazily
    private final String host;
    private final Optional<Integer> port;
    private final String database;
    private final String user;
    private final Optional<String> password;

    private Map<Integer, Bank> banks;
    private Map<Integer, Branch> branches;
    private Map<Integer, Customer> customers;
    private Map<Integer, Account> accounts;
    private Map<Integer, Transaction> transactions;

    public BankDb(
        String host,
        Optional<Integer> port,
        String database,
        String user,
        Optional<String> password
    )
        throws SQLException {
        // store connection parameters but DO NOT connect here
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;

        this.connection = null; // created on demand

        this.banks = new HashMap<>();
        this.branches = new HashMap<>();
        this.customers = new HashMap<>();
        this.accounts = new HashMap<>();
        this.transactions = new HashMap<>();
    }

    // ensureConnection() creates the JDBC Connection only when needed
    private synchronized void ensureConnection() throws SQLException {
        if (this.connection != null) return;

        String url =
            "jdbc:postgresql://" +
            host +
            port.map(p -> ":" + p).orElse("") +
            "/" +
            database;

        Properties props = new Properties();
        props.setProperty("user", user);
        password.ifPresent(pw -> props.setProperty("password", pw));

        this.connection = DriverManager.getConnection(url, props);
    }

    public void connect() throws SQLException {
        this.banks = this.fetchBanks();
        this.branches = this.fetchBranches(this.banks);
        this.customers = this.fetchCustomers(this.branches);
        this.accounts = this.fetchAccounts(this.customers);
        this.transactions = this.fetchTransactions(this.accounts);
    }

    public Optional<Customer> customerLogin(String email, String password)
        throws SQLException {
        ensureConnection();

        String sql =
            "SELECT c.id " +
            "FROM customer c " +
            "JOIN customer_login cl ON c.id = cl.customer_id " +
            "WHERE c.email = ? AND cl.password = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                // invalid credentials
                if (!rs.next()) return Optional.empty();

                return Optional.ofNullable(this.customers.get(rs.getInt("id")));
            }
        }
    }

    public List<Customer> getCustomersSearch(String[] query)
        throws SQLException {
        ensureConnection();

        List<Customer> customers = new ArrayList<>();

        String sql = "SELECT * FROM search_for_customer_ids(?)";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            Array queryArray = connection.createArrayOf("TEXT", query);
            stmt.setArray(1, queryArray);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                customers.add(this.customers.get(rs.getInt("id")));
            }

            queryArray.free();
        }

        return customers;
    }

    public Map<Integer, Bank> getBanks() {
        return Collections.unmodifiableMap(this.banks);
    }

    public Map<Integer, Branch> getBranches() {
        return Collections.unmodifiableMap(this.branches);
    }

    public Map<Integer, Customer> getCustomers() {
        return Collections.unmodifiableMap(this.customers);
    }

    public Map<Integer, Account> getAccounts() {
        return Collections.unmodifiableMap(this.accounts);
    }

    public Map<Integer, Transaction> getTransactions() {
        return Collections.unmodifiableMap(this.transactions);
    }

    // changed visibility from private -> protected so tests can override
    protected Map<Integer, Bank> fetchBanks() throws SQLException {
        ensureConnection();

        Map<Integer, Bank> banks = new HashMap<>();

        String sql = "SELECT * FROM bank";
        try (
            PreparedStatement stmt = this.connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                int id = rs.getInt("id");
                banks.put(id, new Bank(id, rs.getString("name")));
            }
        }

        return banks;
    }

    protected Map<Integer, Branch> fetchBranches(Map<Integer, Bank> banks)
        throws SQLException {
        ensureConnection();

        Map<Integer, Branch> branches = new HashMap<>();

        String sql = "SELECT * FROM branch";
        try (
            PreparedStatement stmt = this.connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                int id = rs.getInt("id");
                Bank bank = banks.get(rs.getInt("bank_id"));
                Branch branch = new Branch(id, rs.getString("address"), bank);
                bank.addBranch(branch);
                branches.put(id, branch);
            }
        }

        return branches;
    }

    protected Map<Integer, Customer> fetchCustomers(
        Map<Integer, Branch> branches
    )
        throws SQLException {
        ensureConnection();

        Map<Integer, Customer> customers = new HashMap<>();

        String sql = "SELECT * FROM customer";
        try (
            PreparedStatement stmt = this.connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                int id = rs.getInt("id");
                Branch branch = branches.get(rs.getInt("branch_id"));
                Customer customer = new Customer(
                    id,
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getDate("date_of_birth").toLocalDate(),
                    rs.getString("social_insurance_number"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    branch
                );
                branch.addCustomer(customer);
                customers.put(id, customer);
            }
        }

        return customers;
    }

    protected Map<Integer, Account> fetchAccounts(
        Map<Integer, Customer> customers
    )
        throws SQLException {
        Map<Integer, Account> accounts = new HashMap<>();

        accounts.putAll(this.fetchAccountsChecking(customers));
        accounts.putAll(this.fetchAccountsCredit(customers));
        accounts.putAll(this.fetchAccountsSavings(customers));

        return accounts;
    }

    // make these protected as well (optional but consistent)
    protected Map<Integer, AccountChecking> fetchAccountsChecking(
        Map<Integer, Customer> customers
    )
        throws SQLException {
        ensureConnection();

        Map<Integer, AccountChecking> accounts = new HashMap<>();

        String sql = "SELECT * FROM account_checking";
        try (
            PreparedStatement stmt = this.connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                int id = rs.getInt("id");
                Customer customer = customers.get(rs.getInt("customer_id"));
                AccountChecking account = new AccountChecking(
                    id,
                    rs.getString("name"),
                    rs.getBoolean("is_locked"),
                    customer,
                    rs.getBigDecimal("monthly_fee")
                );
                customer.addAccount(account);
                accounts.put(id, account);
            }
        }

        return accounts;
    }

    protected Map<Integer, AccountCredit> fetchAccountsCredit(
        Map<Integer, Customer> customers
    )
        throws SQLException {
        ensureConnection();

        Map<Integer, AccountCredit> accounts = new HashMap<>();

        String sql = "SELECT * FROM account_credit";
        try (
            PreparedStatement stmt = this.connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                int id = rs.getInt("id");
                Customer customer = customers.get(rs.getInt("customer_id"));
                AccountCredit account = new AccountCredit(
                    id,
                    rs.getString("name"),
                    rs.getBoolean("is_locked"),
                    customer,
                    rs.getBigDecimal("credit_limit"),
                    rs.getInt("payment_grace_days")
                );
                customer.addAccount(account);
                accounts.put(id, account);
            }
        }

        return accounts;
    }

    protected Map<Integer, AccountSavings> fetchAccountsSavings(
        Map<Integer, Customer> customers
    )
        throws SQLException {
        ensureConnection();

        Map<Integer, AccountSavings> accounts = new HashMap<>();

        String sql = "SELECT * FROM account_savings";
        try (
            PreparedStatement stmt = this.connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                int id = rs.getInt("id");
                Customer customer = customers.get(rs.getInt("customer_id"));
                AccountSavings account = new AccountSavings(
                    id,
                    rs.getString("name"),
                    rs.getBoolean("is_locked"),
                    customer,
                    rs.getBigDecimal("interest_rate")
                );
                customer.addAccount(account);
                accounts.put(id, account);
            }
        }

        return accounts;
    }

    protected Map<Integer, Transaction> fetchTransactions(
        Map<Integer, Account> accounts
    )
        throws SQLException {
        ensureConnection();

        Map<Integer, Transaction> transactions = new HashMap<>();

        String sql = "SELECT * FROM transaction";
        try (
            PreparedStatement stmt = this.connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                int id = rs.getInt("id");
                Account source = accounts.get(rs.getInt("account_id_source"));
                Transaction transaction = new Transaction(
                    id,
                    new TransactionInfo(
                        source,
                        accounts.get(rs.getInt("account_id_destination")),
                        rs.getBigDecimal("amount"),
                        rs.getTimestamp("time").toLocalDateTime()
                    )
                );
                source.addTransaction(transaction);
                transactions.put(id, transaction);
            }
        }

        return transactions;
    }
}
