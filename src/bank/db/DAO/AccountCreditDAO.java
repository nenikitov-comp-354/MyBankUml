package bank.db.DAO;

import bank.db.AccountCredit;
import bank.db.Customer;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class AccountCreditDAO {
    private final Connection connection;
    private final CustomerDAO customerDao;

    public AccountCreditDAO(Connection connection, CustomerDAO customerDao) {
        this.connection = connection;
        this.customerDao = customerDao;
    }

    public List<AccountCredit> findAll() throws SQLException {
        String sql =
            "SELECT a.id, a.number, a.active, a.balance, a.customer_id, c.credit_limit " +
            "FROM account a JOIN account_credit c ON a.id = c.id " +
            "ORDER BY a.id";
        List<AccountCredit> result = new ArrayList<>();

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
                int creditLimit = rs.getInt("credit_limit");
                Customer owner = customerDao
                    .findById(customerId)
                    .orElseThrow(
                        () ->
                            new SQLException(
                                "Customer not found: " + customerId
                            )
                    );
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
            }
        }

        return result;
    }

    public AccountCredit findById(int id) throws SQLException {
        String sql =
            "SELECT a.id, a.number, a.active, a.balance, a.customer_id, c.credit_limit " +
            "FROM account a JOIN account_credit c ON a.id = c.id WHERE a.id = ?";
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
                int creditLimit = rs.getInt("credit_limit");
                return new AccountCredit(
                    rs.getInt("id"),
                    rs.getString("number"),
                    rs.getBoolean("active"),
                    owner,
                    balance,
                    creditLimit
                );
            }
        }
    }
}
