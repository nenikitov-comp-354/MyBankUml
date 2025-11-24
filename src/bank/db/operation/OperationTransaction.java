package bank.db.operation;

import bank.db.*;
import bank.util.TypeValidator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

class OperationTransaction implements Operation {
    private final TransactionInfo info;

    public OperationTransaction(TransactionInfo info) {
        TypeValidator.validateNotNull("Transaction Info", info);
        this.info = info;
    }

    @Override
    public void process(Connection connection, BankDb BankDb) throws SQLException {
        String sql =
            "INSERT INTO transaction (account_id_source, account_id_destination, amount, time) VALUES (?, ?, ?, ?)";

        try (
            PreparedStatement stmt = connection.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
            )
        ) {
            stmt.setInt(1, this.info.getSource().getId());
            stmt.setInt(2, this.info.getDestination().getId());
            stmt.setBigDecimal(3, this.info.getAmount());
            stmt.setTimestamp(4, Timestamp.valueOf(this.info.getTime()));

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) throw new SQLException(
                    "Could not insert transaction"
                );

                int id = rs.getInt(1);

                Transaction transaction = new Transaction(id, this.info);

                this.info.getSource().addTransaction(transaction);
                BankDb.addCachedTransaction(transaction);
            }
        }
    }
}
