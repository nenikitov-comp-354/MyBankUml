package bank.db;

import bank.db.operation.*;
import bank.util.TypeValidator;
import java.sql.*;
import java.util.*;
import javax.sql.DataSource;

public class BankDb {
    private Optional<Connection> connection;

    private Optional<DataSource> dataSource;

    private String host;
    private Optional<Integer> port;
    private String database;
    private String user;
    private Optional<String> password;

    private Map<Integer, Bank> banks;
    private Map<Integer, Branch> branches;
    private Map<Integer, Customer> customers;
    private Map<Integer, Account> accounts;
    private Map<Integer, Transaction> transactions;

    private Queue<Operation> operations;

    public BankDb(
        String host,
        Optional<Integer> port,
        String database,
        String user,
        Optional<String> password
    ) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;

        this.connection = Optional.empty();
        this.dataSource = Optional.empty();

        this.banks = new HashMap<>();
        this.branches = new HashMap<>();
        this.customers = new HashMap<>();
        this.accounts = new HashMap<>();
        this.transactions = new HashMap<>();
    }

    // other constructor, to inject a DataSource, which is useful for testing
    public BankDb(DataSource dataSource) {
        this.host = null;
        this.port = Optional.empty();
        this.database = null;
        this.user = null;
        this.password = Optional.empty();

        this.connection = Optional.empty();
        this.dataSource = Optional.of(dataSource);

        this.banks = new HashMap<>();
        this.branches = new HashMap<>();
        this.customers = new HashMap<>();
        this.accounts = new HashMap<>();
        this.transactions = new HashMap<>();
    }

    protected synchronized void ensureConnection() throws SQLException {
        if (this.connection.isPresent()) return;

        if (this.dataSource.isPresent()) {
            this.connection =
                Optional.of(this.dataSource.get().getConnection());
            return;
        }

        String url =
            "jdbc:postgresql://" +
            host +
            port.map(p -> ":" + p).orElse("") +
            "/" +
            database;

        Properties props = new Properties();
        props.setProperty("user", user);
        password.ifPresent(pw -> props.setProperty("password", pw));

        this.connection = Optional.of(DriverManager.getConnection(url, props));
        this.operations = new LinkedList<>();
    }

    public void connect() throws SQLException {
        ensureConnection();

        // clears caches to avoid duplicates on multiple connect() calls
        this.banks.clear();
        this.branches.clear();
        this.customers.clear();
        this.accounts.clear();
        this.transactions.clear();

        this.banks = this.fetchBanks();
        this.branches = this.fetchBranches(this.banks);
        this.customers = this.fetchCustomers(this.branches);
        this.accounts = this.fetchAccounts(this.customers);
        this.transactions = this.fetchTransactions(this.accounts);
    }

    public void addOperation(Operation operation) {
        TypeValidator.validateNotNull("Operation", operation);
        this.operations.add(operation);
    }

    public void processOperations() throws SQLException {
        ensureConnection();
        Connection conn = this.connection.get();

        boolean autoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);

        while (!this.operations.isEmpty()) {
            Operation operation = this.operations.remove();
            try {
                operation.process(conn, this);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }

        conn.setAutoCommit(autoCommit);
    }

    public Optional<Customer> customerLogin(String email, String password)
        throws SQLException {
        ensureConnection();
        Connection conn = this.connection.get();

        String sql =
            "SELECT c.id " +
            "FROM customer c " +
            "JOIN customer_login cl ON c.id = cl.customer_id " +
            "WHERE LOWER(c.email) = LOWER(?) AND cl.password = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                // invalid credentials
                if (!rs.next()) return Optional.empty();

                int id = rs.getInt("id");

                if (this.customers.containsKey(id)) {
                    return Optional.of(this.customers.get(id));
                }
                return Optional.ofNullable(loadSingleCustomer(conn, id));
            }
        }
    }

    private Customer loadSingleCustomer(Connection conn, int id)
        throws SQLException {
        String sql = "SELECT * FROM customer WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;

                Branch branch = this.branches.get(rs.getInt("branch_id"));
                // avoid duplicates in case of multiple calls
                Customer c = new Customer(
                    id,
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getDate("date_of_birth").toLocalDate(),
                    rs.getString("social_insurance_number"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    branch,
                    rs.getBoolean("admin_stat")
                );
                if (!branch.getCustomers().contains(c)) {
                    branch.addCustomer(c);
                }

                this.customers.put(id, c);
                return c;
            }
        }
    }

    public List<Customer> getCustomersSearch(String[] query)
        throws SQLException {
        ensureConnection();
        Connection conn = this.connection.get();

        List<Customer> customers = new ArrayList<>();

        String sql = "SELECT * FROM search_for_customer_ids(?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            Array queryArray = conn.createArrayOf("TEXT", query);
            stmt.setArray(1, queryArray);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    Customer cust = this.customers.get(id);
                    if (cust == null) {
                        cust = loadSingleCustomer(conn, id);
                    }
                    customers.add(cust);
                }
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

    public void addTransaction(Transaction transaction) {
        TypeValidator.validateNotNull("Transaction", transaction);
        this.transactions.put(transaction.getId(), transaction);
    }

    // changed visibility from private to protected so tests can override
    protected Map<Integer, Bank> fetchBanks() throws SQLException {
        ensureConnection();
        Connection conn = this.connection.get();

        Map<Integer, Bank> banks = new HashMap<>();

        String sql = "SELECT * FROM bank";
        try (
            PreparedStatement stmt = conn.prepareStatement(sql);
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
        Connection conn = this.connection.get();

        Map<Integer, Branch> branches = new HashMap<>();

        String sql = "SELECT * FROM branch";
        try (
            PreparedStatement stmt = conn.prepareStatement(sql);
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
        Connection conn = this.connection.get();

        Map<Integer, Customer> customers = new HashMap<>();

        String sql = "SELECT * FROM customer";
        try (
            PreparedStatement stmt = conn.prepareStatement(sql);
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
                    branch,
                    rs.getBoolean("admin_stat")
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

        accounts.putAll(this.fetchAccountsChequing(customers));
        accounts.putAll(this.fetchAccountsCredit(customers));
        accounts.putAll(this.fetchAccountsSavings(customers));

        return accounts;
    }

    // made these protected as well so tests can override
    protected Map<Integer, AccountChequing> fetchAccountsChequing(
        Map<Integer, Customer> customers
    )
        throws SQLException {
        ensureConnection();
        Connection conn = this.connection.get();

        Map<Integer, AccountChequing> accounts = new HashMap<>();

        String sql =
            "SELECT * FROM account NATURAL INNER JOIN account_chequing";
        try (
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                int id = rs.getInt("id");
                Customer customer = customers.get(rs.getInt("customer_id"));
                AccountChequing account = new AccountChequing(
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
        Connection conn = this.connection.get();

        Map<Integer, AccountCredit> accounts = new HashMap<>();

        String sql = "SELECT * FROM account NATURAL INNER JOIN account_credit";
        try (
            PreparedStatement stmt = conn.prepareStatement(sql);
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
        Connection conn = this.connection.get();

        Map<Integer, AccountSavings> accounts = new HashMap<>();

        String sql = "SELECT * FROM account NATURAL INNER JOIN account_savings";
        try (
            PreparedStatement stmt = conn.prepareStatement(sql);
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
        Connection conn = this.connection.get();

        Map<Integer, Transaction> transactions = new HashMap<>();

        String sql = "SELECT * FROM transaction";
        try (
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                int id = rs.getInt("id");
                Account source = accounts.get(rs.getInt("account_id_source"));
                Account destination = accounts.get(
                    rs.getInt("account_id_destination")
                );
                Transaction transaction = new Transaction(
                    id,
                    new TransactionInfo(
                        source,
                        destination,
                        rs.getBigDecimal("amount"),
                        rs.getTimestamp("time").toLocalDateTime()
                    )
                );
                source.addTransaction(transaction);
                destination.addTransaction(transaction);
                transactions.put(id, transaction);
            }
        }

        return transactions;
    }
}
