package bank.db.operation;

import bank.db.*;
import bank.util.TypeValidator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OperationMakeAdmin implements Operation {
    private final Customer customer;
    private final boolean isAdmin;

    public OperationMakeAdmin(Customer customer, boolean isAdmin) {
        TypeValidator.validateNotNull("Customer", customer);
        this.customer = customer;

        this.isAdmin = isAdmin;
    }

    @Override
    public void process(Connection connection, BankDb bankDb)
        throws SQLException {
        String sql = "UPDATE customer SET is_admin = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, this.isAdmin);
            stmt.setInt(2, this.customer.getId());
            int updated = stmt.executeUpdate();
            if (updated != 1) {
                throw new SQLException(
                    "Customer " +
                    this.customer.getId() +
                    " status was not updated correctly, updated " +
                    updated +
                    " rows"
                );
            }
        }

        customer.setAdmin(this.isAdmin);
    }
}
