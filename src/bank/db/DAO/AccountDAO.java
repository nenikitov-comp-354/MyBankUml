package bank.db.DAO;

import bank.db.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Locale;

public class AccountDAO {
    private final Connection connection;
    private final CustomerDAO customerDao;

    public AccountDAO(Connection connection, CustomerDAO customerDao) {
        this.connection = connection;
        this.customerDao = customerDao;
    }

    public List<Account> findByCustomerId(int customerId) throws SQLException {
        String sql = "SELECT * FROM account WHERE customer_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Account> result = new ArrayList<>();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String number = rs.getString("number");
                    boolean active = rs.getBoolean("active");
                    BigDecimal balance = rs.getBigDecimal("balance");
                    int ownerId = rs.getInt("customer_id");
                    Customer owner = customerDao
                        .findById(ownerId)
                        .orElseThrow(
                            () ->
                                new SQLException(
                                    "Customer not found: " + ownerId
                                )
                        );
                    String type = rs.getString("type"); // CHECKING / SAVINGS / CREDIT
                    switch (type.toUpperCase(Locale.ROOT)) {
                        case "CHECKING":
                            result.add(
                                new AccountChecking(
                                    id,
                                    number,
                                    active,
                                    owner,
                                    balance
                                )
                            );
                            break;
                        case "SAVINGS":
                            result.add(
                                new AccountSavings(
                                    id,
                                    number,
                                    active,
                                    owner,
                                    balance
                                )
                            );
                            break;
                        case "CREDIT":
                            int creditLimit = rs.getInt("credit_limit");
                            result.add(
                                new AccountCredit(
                                    id,
                                    number,
                                    active,
                                    owner,
                                    balance,
                                    creditLimit
                                )
                            );
                            break;
                        default:
                            throw new SQLException(
                                "Unknown account type: " + type
                            );
                    }
                }
                return result;
            }
        }
    }

    public Optional<Account> findById(int id) throws SQLException {
        String sql =
            "SELECT id, number, active, balance, customer_id FROM account WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                int acctId = rs.getInt("id");
                String number = rs.getString("number");
                boolean active = rs.getBoolean("active");
                BigDecimal balance = rs.getBigDecimal("balance");
                int ownerId = rs.getInt("customer_id");
                Customer owner = customerDao
                    .findById(ownerId)
                    .orElseThrow(
                        () -> new SQLException("Customer not found: " + ownerId)
                    );
                // CHECKING
                try (
                    PreparedStatement ps = connection.prepareStatement(
                        "SELECT 1 FROM account_checking WHERE id = ?"
                    )
                ) {
                    ps.setInt(1, acctId);
                    try (ResultSet r2 = ps.executeQuery()) {
                        if (r2.next()) {
                            return Optional.of(
                                new AccountChecking(
                                    acctId,
                                    number,
                                    active,
                                    owner,
                                    balance
                                )
                            );
                        }
                    }
                }

                // SAVINGS
                try (
                    PreparedStatement ps = connection.prepareStatement(
                        "SELECT 1 FROM account_savings WHERE id = ?"
                    )
                ) {
                    ps.setInt(1, acctId);
                    try (ResultSet r2 = ps.executeQuery()) {
                        if (r2.next()) {
                            return Optional.of(
                                new AccountSavings(
                                    acctId,
                                    number,
                                    active,
                                    owner,
                                    balance
                                )
                            );
                        }
                    }
                }

                // CREDIT
                try (
                    PreparedStatement ps = connection.prepareStatement(
                        "SELECT credit_limit FROM account_credit WHERE id = ?"
                    )
                ) {
                    ps.setInt(1, acctId);
                    try (ResultSet r2 = ps.executeQuery()) {
                        if (r2.next()) {
                            int creditLimit = r2.getInt("credit_limit");
                            return Optional.of(
                                new AccountCredit(
                                    acctId,
                                    number,
                                    active,
                                    owner,
                                    balance,
                                    creditLimit
                                )
                            );
                        }
                    }
                }

                // If no subtype row, return generic Account if you have one; else empty
                // If Account is abstract return empty
                return Optional.empty();
            }
        }
    }
}
