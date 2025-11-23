package bank.db.DAO;

import bank.db.AccountSavings;

import java.sql.*;

/**
 * DAO for AccountSavings Table.
 */
public class AccountSavingsDAO {
    private final Connection connection;
    private final AccountDAO accountDao;

    public AccountSavingsDAO(Connection connection, AccountDAO accountDao) {
        this.connection = connection;
        this.accountDao = accountDao;
    }

    public AccountSavings findById(int id) throws SQLException {
        String sql = "SELECT * FROM account_savings WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;

                double interestRate = rs.getDouble("interest_rate");
                return new AccountSavings(accountDao.findById(id), interestRate);
            }
        }
    }
}