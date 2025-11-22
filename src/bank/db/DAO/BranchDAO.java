package bank.db.DAO;
import java.sql.*;

import bank.db.Bank;
import bank.db.Branch;

/**
 * Data Access Object for Branch Table.
 */
public class BranchDAO {
    
    private final Connection connection;
    private final BankDAO bankDao;

    /**
     * constructor
     * @param connection
     * @param bankDao
     */
    public BranchDAO(Connection connection, BankDAO bankDao) {
        this.connection = connection;
        this.bankDao = bankDao;
    }

    /**
     * finds the Branch object by ID within the DB using SQL
     * @param id
     * @return branch object
     * @throws SQLException
     */
    public Branch findById(int id) throws SQLException {
        String sql = "SELECT * FROM branch WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;

                int branchId = rs.getInt("id");
                String address = rs.getString("address");
                int bankId = rs.getInt("bank_id");

                Bank bank = bankDao.findById(bankId);
                if (bank == null)
                    throw new SQLException("Branch " + id + " refers to missing bank " + bankId);

                return new Branch(branchId, address, bank);
            }
        }
    }
}
