package bank.db.operation;

import bank.db.*;
import bank.util.TypeValidator;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;

public class OperationNewCustomer implements Operation {
    private final String firstName;
    private final String lastName;
    private final LocalDate dateOfBirth;
    private final String socialInsuranceNumber;
    private final String phone;
    private final String email;
    private final Branch branch;
    private final String accountName;
    private final String password;

    public OperationNewCustomer(
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String socialInsuranceNumber,
        String phone,
        String email,
        Branch branch,
        String accountName,
        String password
    ) {
        TypeValidator.validateNonEmptyText("First name", firstName);
        this.firstName = firstName;

        TypeValidator.validateNonEmptyText("Last name", lastName);
        this.lastName = lastName;

        TypeValidator.validateNotNull("Date of birth", dateOfBirth);
        this.dateOfBirth = dateOfBirth;

        TypeValidator.validateSocialInsuranceNumber(
            "Social insurance number",
            socialInsuranceNumber
        );
        this.socialInsuranceNumber = socialInsuranceNumber;

        TypeValidator.validatePhone("Phone", phone);
        this.phone = phone;

        TypeValidator.validateEmail("Email", email);
        this.email = email;

        TypeValidator.validateNotNull("Branch", branch);
        this.branch = branch;

        TypeValidator.validateNonEmptyText("Account name", accountName);
        this.accountName = accountName;

        this.password = password;
    }

    @Override
    public void process(Connection connection, BankDb bankDb)
        throws SQLException {
        Customer customer = this.insertCustomer(connection, bankDb);
        Account account = this.insertAccount(connection, bankDb, customer);

        customer.addAccount(account);
        bankDb.addCustomer(customer);
    }

    private Customer insertCustomer(Connection connection, BankDb bankDb)
        throws SQLException {
        String sql =
            "WITH c as (INSERT INTO customer (first_name, last_name, date_of_birth, social_insurance_number, phone, email, branch_id) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id) INSERT INTO customer_login (customer_id, password) SELECT c.id, ? FROM c";

        try (
            PreparedStatement stmt = connection.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
            )
        ) {
            stmt.setString(1, this.firstName);
            stmt.setString(2, this.lastName);
            stmt.setDate(3, Date.valueOf(this.dateOfBirth));
            stmt.setString(4, this.socialInsuranceNumber);
            stmt.setString(5, this.phone);
            stmt.setString(6, this.email);
            stmt.setInt(7, this.branch.getId());
            stmt.setString(8, this.password);

            int updated = stmt.executeUpdate();
            if (updated != 1) {
                throw new SQLException(
                    "Could not create a customer " +
                    this.firstName +
                    " " +
                    this.lastName
                );
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (!rs.next()) throw new SQLException(
                    "Could not get the ID of the new customer"
                );

                int id = rs.getInt(1);

                return new Customer(
                    id,
                    this.firstName,
                    this.lastName,
                    this.dateOfBirth,
                    this.socialInsuranceNumber,
                    this.phone,
                    this.email,
                    this.branch,
                    false
                );
            }
        }
    }

    private Account insertAccount(
        Connection connection,
        BankDb bankDb,
        Customer customer
    )
        throws SQLException {
        String sql =
            "WITH a AS (INSERT INTO account (name, is_locked, customer_id) VALUES (?, ?, ?) RETURNING id) INSERT INTO account_chequing (id, monthly_fee) SELECT a.id, ? FROM a";

        try (
            PreparedStatement stmt = connection.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
            )
        ) {
            stmt.setString(1, this.accountName);
            stmt.setBoolean(2, false);
            stmt.setInt(3, customer.getId());
            stmt.setBigDecimal(4, new BigDecimal(0));

            int updated = stmt.executeUpdate();
            if (updated != 1) {
                throw new SQLException(
                    "Could not create an account " + this.accountName
                );
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (!rs.next()) throw new SQLException(
                    "Could not get the ID of the new account"
                );

                int id = rs.getInt(1);

                return new AccountChequing(
                    id,
                    this.accountName,
                    false,
                    customer,
                    new BigDecimal(0)
                );
            }
        }
    }
}
