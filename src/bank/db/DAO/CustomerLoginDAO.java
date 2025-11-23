package bank.db.DAO;

import bank.db.Customer;
import java.sql.*;
import java.util.Optional;

/**
 * Data Access Object for customer login / authentication.
 */
public class CustomerLoginDAO {
    private final Connection connection;
    private final CustomerDAO customerDAO;

    /**
     * Constructor
     * @param connection
     * @param customerDao
     */
    public CustomerLoginDAO(Connection connection, CustomerDAO customerDao) {
        this.connection = connection;
        this.customerDAO = customerDao;
    }

    /**
     * Create login credentials for an existing customer storing into Customer_login table
     * @param customerId
     * @param password
     * @throws SQLException
     */
    public void createLogin(int customerId, String password)
        throws SQLException {
        String sql =
            "INSERT INTO customer_login (customer_id, password) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            stmt.setString(2, password);
            stmt.executeUpdate();
        }
    }

    /**
     * Change password for an existing customer.
     * @param customerId
     * @param newPassword
     * @throws SQLException
     */
    public void updatePassword(int customerId, String newPassword)
        throws SQLException {
        String sql =
            "UPDATE customer_login SET password = ? WHERE customer_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setInt(2, customerId);
            stmt.executeUpdate();
        }
    }

    /**
     * Authenticate by email + password.
     * @param email
     * @param password
     * @return Customer if credentials are valid, null otherwise
     * @throws SQLException
     */
    public Optional<Customer> authenticate(String email, String password)
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

                int customerId = rs.getInt("id");
                return customerDAO.findById(customerId);
            }
        }
    }

    /**
     * Check if a customer already has login credentials within Database
     * @param customerId
     * @return true if it has login credentials, false if none.
     * @throws SQLException
     */
    public boolean hasLogin(int customerId) throws SQLException {
        String sql = "SELECT 1 FROM customer_login WHERE customer_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
