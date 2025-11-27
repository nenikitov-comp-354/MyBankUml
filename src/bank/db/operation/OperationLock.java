package bank.db.operation;

import bank.db.*;
import bank.util.TypeValidator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OperationLock implements Operation {
    private final Account account;
    private final boolean isLocked;

    public OperationLock(Account account, boolean isLocked) {
        TypeValidator.validateNotNull("Account", account);
        this.account = account;

        this.isLocked = isLocked;
    }

    @Override
    public void process(Connection connection, BankDb bankDb)
        throws SQLException {
        String sql = "UPDATE account SET is_locked = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, this.isLocked);
            stmt.setInt(2, this.account.getId());
            int updated = stmt.executeUpdate();
            if (updated != 1) {
                throw new SQLException(
                    "Account " +
                    this.account.getId() +
                    " status was not updated correctly, updated " +
                    updated +
                    " rows"
                );
            }
        }

        account.setLocked(this.isLocked);
    }
}
