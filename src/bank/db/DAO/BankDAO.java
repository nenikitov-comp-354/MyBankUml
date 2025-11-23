package bank.db.DAO;

import bank.db.Bank;
import java.sql.*;

/**
 * Data Access Object for Bank Table.
 */
public class BankDAO {
    private final Connection connection;

    /**
     * constructor
     * @param connection
     */
    public BankDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * finds the bank object by ID within the DB using SQL
     * @param id
     * @return bank object
     * @throws SQLException
     */
    public Bank findById(int id) throws SQLException {
        String sql = "SELECT * FROM bank WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;

                return new Bank(rs.getInt("id"), rs.getString("name"));
            }
        }
    }
}
