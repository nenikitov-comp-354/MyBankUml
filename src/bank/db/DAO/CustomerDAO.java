package bank.db.DAO;

import bank.db.Branch;
import bank.db.Customer;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class CustomerDAO {
    private final Connection connection;

    public CustomerDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Customer> findByBranchId(int branchId) throws SQLException {
        String sql = "SELECT * FROM customer WHERE branch_id=?";
        List<Customer> customers = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(
                        new Customer(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getDate("date_of_birth").toLocalDate(),
                            rs.getString("social_insurance_number"),
                            rs.getString("phone"),
                            rs.getString("email"),
                            new Branch(branchId, "", null)
                        )
                    );
                }
            }
        }

        return customers;
    }

    public Optional<Customer> findById(int id) throws SQLException {
        String sql = "SELECT * FROM customer WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(
                    new Customer(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getDate("date_of_birth").toLocalDate(),
                        rs.getString("social_insurance_number"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        new Branch(rs.getInt("branch_id"), "", null)
                    )
                );
            }
        }
    }
}
