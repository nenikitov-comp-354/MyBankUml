package bank.db.DAO;

import bank.db.Bank;
import bank.db.Branch;
import java.sql.*;
import java.util.*;

public class BranchDAO {
    private final Connection connection;

    public BranchDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Branch> findByBankId(int bankId) throws SQLException {
        String sql = "SELECT * FROM branch WHERE bank_id = ? ORDER BY id";
        List<Branch> branches = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bankId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    branches.add(
                        new Branch(
                            rs.getInt("id"),
                            rs.getString("address"),
                            new Bank(bankId, "")
                        )
                    );
                }
            }
        }

        return branches;
    }
}
