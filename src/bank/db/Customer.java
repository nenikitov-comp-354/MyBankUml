package bank.db;

import bank.util.TypeValidator;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

public class Customer {
    @Getter
    private int id;

    @Getter
    private String firstName;

    @Getter
    private String lastName;

    @Getter
    private LocalDate dateOfBirth;

    @Getter
    private String socialInsuranceNumber;

    @Getter
    private String phone;

    @Getter
    private String email;

    @Getter
    private Branch branch;

    private List<Account> accounts;

    public Customer(
        int id,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String socialInsuranceNumber,
        String phone,
        String email,
        Branch branch
    ) {
        TypeValidator.validateId("Id", id);
        this.id = id;

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

        this.accounts = new ArrayList<>();
    }

    public List<Account> getAccounts() {
        return Collections.unmodifiableList(this.accounts);
    }

    public void addAccount(Account account) {
        TypeValidator.validateNotNull("Account", account);
        if (!this.equals(account.getCustomer())) {
            throw new IllegalArgumentException(
                "Account " +
                account +
                " does not belong to this customer " +
                this
            );
        }
        this.accounts.add(account);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Customer)) return false;

        Customer other = (Customer) obj;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        return (
            "Customer(id=" +
            id +
            ", firstName=" +
            firstName +
            ", lastName=" +
            lastName +
            ", dateOfBirth=" +
            dateOfBirth +
            ", socialInsuranceNumber=" +
            socialInsuranceNumber +
            ", phone=" +
            phone +
            ", email=" +
            email +
            ", branch=" +
            branch +
            ")"
        );
    }
}
