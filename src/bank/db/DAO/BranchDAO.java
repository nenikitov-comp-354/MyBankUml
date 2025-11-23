package bank.db.DAO;

import bank.db.Bank;
import bank.db.Branch;
import java.sql.*;
import java.util.HashMap;
import java.util.Optional;

/**
 * Data Access Object for Branch Table.
 */
public class BranchDAO {
    private final Connection connection;
    private final BankDAO bankDao;
    private final HashMap<Integer, Branch> cache;

    /**
     * constructor
     * @param connection
     * @param bankDao
     */
    public BranchDAO(Connection connection, BankDAO bankDao) {
        this.connection = connection;
        this.bankDao = bankDao;
        this.cache = new HashMap<>();
    }

    /**
     * finds the Branch object by ID within the DB using SQL
     * @param id
     * @return branch object
     * @throws SQLException
     */
    public Optional<Branch> findById(int id) throws SQLException {
        Branch branch = cache.get(id);

        if (branch != null) return Optional.of(branch);

        String sql = "SELECT * FROM branch WHERE id = ?";

        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, id);

        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) return Optional.empty();

        String address = rs.getString("address");
        int bankId = rs.getInt("bank_id");

        Optional<Bank> bank = bankDao.findById(bankId);
        if (!bank.isPresent()) throw new SQLException(
            "Branch " + id + " refers to missing bank " + bankId
        );

        branch = new Branch(id, address, bank.get());
        bank.get().addBranch(branch);

        cache.put(id, branch);
        return Optional.of(branch);
    }
}
