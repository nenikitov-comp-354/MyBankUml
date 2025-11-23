package bank.db.DAO;

import bank.db.Bank;
import java.sql.*;
import java.util.HashMap;
import java.util.Optional;

/**
 * Data Access Object for Bank Table.
 */
public class BankDAO {
    private final Connection connection;
    private final HashMap<Integer, Bank> cache;

    /**
     * constructor
     * @param connection
     */
    public BankDAO(Connection connection) {
        this.connection = connection;
        this.cache = new HashMap<>();
    }

    /**
     * finds the bank object by ID within the DB using SQL
     * @param id
     * @return bank object
     * @throws SQLException
     */
    public Optional<Bank> findById(int id) throws SQLException {
        Bank bank = cache.get(id);

        if (bank != null) return Optional.of(bank);

        String sql = "SELECT * FROM bank WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return Optional.empty();

                bank = new Bank(id, rs.getString("name"));

                cache.put(id, bank);
                return Optional.of(bank);
            }
        }
    }
}
