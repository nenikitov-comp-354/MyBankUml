package bank.db.DAO;

import bank.db.Account;
import bank.db.Customer;

import java.sql.*;

/**
 * Data Access Object for Account Table.
 */
public class AccountDAO {
    private final Connection connection;
    private final CustomerDAO customerDao;

    public AccountDAO(Connection connection, CustomerDAO customerDao) {
        this.connection = connection;
        this.customerDao = customerDao;
    }

    public Account findById(int id) throws SQLException {
        String sql = "SELECT * FROM account WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;

                int accountId = rs.getInt("id");
                String name = rs.getString("name");
                boolean isLocked = rs.getBoolean("is_locked");
                int customerId = rs.getInt("customer_id");

                Customer customer = customerDao.findById(customerId);
                if (customer == null) throw new SQLException("Account " + id + " refers to missing customer " + customerId);

                return new Account(accountId, name, isLocked, customer);
            }
        }
    }
}