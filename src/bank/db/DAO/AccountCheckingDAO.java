package bank.db.DAO;

import bank.db.AccountChecking;

import java.sql.*;

/**
 * DAO for AccountChecking Table.
 */
public class AccountCheckingDAO {
    private final Connection connection;
    private final AccountDAO accountDao;

    public AccountCheckingDAO(Connection connection, AccountDAO accountDao) {
        this.connection = connection;
        this.accountDao = accountDao;
    }

    public AccountChecking findById(int id) throws SQLException {
        String sql = "SELECT * FROM account_checking WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;

                double monthlyFee = rs.getDouble("monthly_fee");
                return new AccountChecking(accountDao.findById(id), monthlyFee);
            }
        }
    }
}