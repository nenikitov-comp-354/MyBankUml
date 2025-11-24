package bank.db.DAO;

import bank.db.AccountSavings;
import bank.db.Customer;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class AccountSavingsDAO {
    private final Connection connection;
    private final CustomerDAO customerDao;

    public AccountSavingsDAO(Connection connection, CustomerDAO customerDao) {
        this.connection = connection;
        this.customerDao = customerDao;
    }

    public List<AccountSavings> findAll() throws SQLException {
        String sql =
            "SELECT a.id, a.number, a.active, a.balance, a.customer_id, s.interest_rate " +
            "FROM account a JOIN account_savings s ON a.id = s.id " +
            "ORDER BY a.id";
        List<AccountSavings> result = new ArrayList<>();

        try (
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)
        ) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String number = rs.getString("number");
                boolean active = rs.getBoolean("active");
                BigDecimal balance = rs.getBigDecimal("balance");
                int customerId = rs.getInt("customer_id");
                Customer owner = customerDao
                    .findById(customerId)
                    .orElseThrow(
                        () ->
                            new SQLException(
                                "Customer not found: " + customerId
                            )
                    );
                result.add(
                    new AccountSavings(id, number, active, owner, balance)
                );
            }
        }

        return result;
    }

    public AccountSavings findById(int id) throws SQLException {
        String sql =
            "SELECT a.id, a.number, a.active, a.balance, a.customer_id, s.interest_rate " +
            "FROM account a JOIN account_savings s ON a.id = s.id WHERE a.id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;
                int customerId = rs.getInt("customer_id");
                Customer owner = customerDao
                    .findById(customerId)
                    .orElseThrow(
                        () ->
                            new SQLException(
                                "Customer not found: " + customerId
                            )
                    );
                BigDecimal balance = rs.getBigDecimal("balance");
                return new AccountSavings(
                    rs.getInt("id"),
                    rs.getString("number"),
                    rs.getBoolean("active"),
                    owner,
                    balance
                );
            }
        }
    }
}
