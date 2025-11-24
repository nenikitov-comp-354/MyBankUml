package bank.db.DAO;

import bank.db.Bank;
import java.sql.*;
import java.util.*;

public class BankDAO {
    private final Connection connection;

    public BankDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Bank> findAll() throws SQLException {
        String sql = "SELECT * FROM bank ORDER BY id";
        List<Bank> result = new ArrayList<>();

        try (
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)
        ) {
            while (rs.next()) {
                result.add(new Bank(rs.getInt("id"), rs.getString("name")));
            }
        }

        return result;
    }

    public Optional<Bank> findById(int id) throws SQLException {
        String sql = "SELECT * FROM bank WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(
                    new Bank(rs.getInt("id"), rs.getString("name"))
                );
            }
        }
    }

    public Bank insert(Bank bank) throws SQLException {
        String sql = "INSERT INTO bank(name) VALUES (?)";
        try (
            PreparedStatement stmt = connection.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
            )
        ) {
            stmt.setString(1, bank.getName());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    // construct a Bank with the generated id instead of calling setId()
                    return new Bank(id, bank.getName());
                }
            }
        }
        // return original (may be id==0)
        return bank;
    }

    public boolean update(Bank bank) throws SQLException {
        String sql = "UPDATE bank SET name=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, bank.getName());
            stmt.setInt(2, bank.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM bank WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}
