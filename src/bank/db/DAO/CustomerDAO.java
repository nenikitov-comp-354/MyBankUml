package bank.db.DAO;

import bank.db.Branch;
import bank.db.Customer;
import java.sql.*;
import java.time.LocalDate;

/**
 * Data Access Object for Customer table.
 */

public class CustomerDAO {
    private final Connection connection;
    private final BranchDAO branchDao;

    /**
     * constructor
     * @param connection
     * @param branchDao
     */
    public CustomerDAO(Connection connection, BranchDAO branchDao) {
        this.connection = connection;
        this.branchDao = branchDao;
    }

    /**
     * returns a customer object with corresponding attributes after finding
     * corresponding customer via the SQL query used in the find functions
     * @param rs
     * @return Customer object
     * @throws SQLException
     */
    private Customer mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        LocalDate dob = rs.getDate("date_of_birth").toLocalDate();
        String sin = rs.getString("social_insurance_number");
        String phone = rs.getString("phone");
        String email = rs.getString("email");
        int branchId = rs.getInt("branch_id");

        Branch branch = branchDao.findById(branchId);
        if (branch == null) {
            throw new SQLException(
                "Customer points to non-existent branch: " + branchId
            );
        }

        return new Customer(
            id,
            firstName,
            lastName,
            dob,
            sin,
            phone,
            email,
            branch
        );
    }

    /**
     * Finds the customer by email using the DB via SQL
     * @param email
     * @return
     * @throws SQLException
     */
    public Customer findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM customer WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;
                return mapRow(rs);
            }
        }
    }

    /**
     * Finds the customer by iD using the DB via SQL
     * @param id
     * @return
     * @throws SQLException
     */
    public Customer findById(int id) throws SQLException {
        String sql = "SELECT * FROM customer WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;
                return mapRow(rs);
            }
        }
    }
}
