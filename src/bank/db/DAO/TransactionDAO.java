package bank.db.DAO;

import bank.db.Account;
import bank.db.Transaction;
import bank.db.TransactionInfo;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class TransactionDAO {
    private final Connection connection;
    private final AccountDAO accountDao;

    public TransactionDAO(Connection connection, AccountDAO accountDao) {
        this.connection = connection;
        this.accountDao = accountDao;
    }

    public List<Transaction> findByAccountId(int accountId)
        throws SQLException {
        String sql =
            "SELECT * FROM transaction WHERE source_account_id = ? OR destination_account_id = ? ORDER BY id";
        List<Transaction> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            stmt.setInt(2, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int srcId = rs.getInt("source_account_id");
                    int dstId = rs.getInt("destination_account_id");
                    BigDecimal amount = rs.getBigDecimal("amount");
                    Timestamp ts = rs.getTimestamp("time"); // adjust column name if different
                    LocalDateTime time = ts == null
                        ? null
                        : ts.toLocalDateTime();

                    // resolve accounts via AccountDAO (requires AccountDAO.findById(int) -> Optional<Account>)
                    Account src = accountDao
                        .findById(srcId)
                        .orElseThrow(
                            () ->
                                new SQLException(
                                    "Source account not found: " + srcId
                                )
                        );
                    Account dst = accountDao
                        .findById(dstId)
                        .orElseThrow(
                            () ->
                                new SQLException(
                                    "Destination account not found: " + dstId
                                )
                        );

                    TransactionInfo info = new TransactionInfo(
                        src,
                        dst,
                        amount,
                        time
                    );
                    result.add(new Transaction(id, info));
                }
            }
        }
        return result;
    }
}
