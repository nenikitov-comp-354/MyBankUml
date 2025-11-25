package bank.db;

import bank.db.operation.*;
import bank.util.TypeValidator;
import java.sql.*;
import java.util.*;

public class BankDb {
    private Connection connection;

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
        if (password.isPresent()) {
            props.setProperty("password", password.get());
        }

        this.connection = DriverManager.getConnection(url, props);

        this.banks = new HashMap<>();
        this.branches = new HashMap<>();
        this.customers = new HashMap<>();
        this.accounts = new HashMap<>();
        this.transactions = new HashMap<>();

        this.operations = new LinkedList<>();
    }

    public void addOperation(Operation operation) {
        TypeValidator.validateNotNull("Operation", operation);
        this.operations.add(operation);
    }

    public void processOperations() throws SQLException {
        boolean autoCommit = this.connection.getAutoCommit();
        this.connection.setAutoCommit(false);

        while (!this.operations.isEmpty()) {
            Operation operation = this.operations.remove();
            try {
                operation.process(this.connection, this);
                this.connection.commit();
            } catch (SQLException e) {
                this.connection.rollback();
                throw e;
            }
        }

        this.connection.setAutoCommit(autoCommit);
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

    public void addTransaction(Transaction transaction) {
        TypeValidator.validateNotNull("Transaction", transaction);
        this.transactions.put(transaction.getId(), transaction);
    }

    private Map<Integer, Bank> fetchBanks() throws SQLException {
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

    private Map<Integer, Branch> fetchBranches(Map<Integer, Bank> banks)
        throws SQLException {
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

    private Map<Integer, Customer> fetchCustomers(
        Map<Integer, Branch> branches
    )
        throws SQLException {
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

    private Map<Integer, Account> fetchAccounts(
        Map<Integer, Customer> customers
    )
        throws SQLException {
        Map<Integer, Account> accounts = new HashMap<>();

        accounts.putAll(this.fetchAccountsChecking(customers));
        accounts.putAll(this.fetchAccountsCredit(customers));
        accounts.putAll(this.fetchAccountsSavings(customers));

        return accounts;
    }

    private Map<Integer, AccountChecking> fetchAccountsChecking(
        Map<Integer, Customer> customers
    )
        throws SQLException {
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

    private Map<Integer, AccountCredit> fetchAccountsCredit(
        Map<Integer, Customer> customers
    )
        throws SQLException {
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

    private Map<Integer, AccountSavings> fetchAccountsSavings(
        Map<Integer, Customer> customers
    )
        throws SQLException {
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

    private Map<Integer, Transaction> fetchTransactions(
        Map<Integer, Account> accounts
    )
        throws SQLException {
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
