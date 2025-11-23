package bank.db.DAO;

import bank.db.AccountCredit;

import java.sql.*;

/**
 * DAO for AccountCredit Table.
 */
public class AccountCreditDAO {
    private final Connection connection;
    private final AccountDAO accountDao;

    public AccountCreditDAO(Connection connection, AccountDAO accountDao) {
        this.connection = connection;
        this.accountDao = accountDao;
    }

    public AccountCredit findById(int id) throws SQLException {
        String sql = "SELECT * FROM account_credit WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;

                double creditLimit = rs.getDouble("credit_limit");
                int graceDays = rs.getInt("payment_grace_days");
                return new AccountCredit(accountDao.findById(id), creditLimit, graceDays);
            }
        }
    }
}