package bank.db;

import java.math.BigDecimal;

import bank.util.TypeValidator;
import lombok.Getter;

public class AccountCredit extends Account {
    @Getter
    private BigDecimal creditLimit;

    @Getter
    private int paymentGraceDays;

    public AccountCredit(int id, String name, boolean isLocked, Customer customer, BigDecimal creditLimit,
            int paymentGraceDays) {
        super(id, name, isLocked, customer);

        TypeValidator.validatePositiveMoney("Credit limit", creditLimit);
        this.creditLimit = creditLimit;

        TypeValidator.validatePositiveInteger("Payment grace days", paymentGraceDays);
        this.paymentGraceDays = paymentGraceDays;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof AccountCredit))
            return false;

        AccountCredit other = (AccountCredit) obj;
        return this.getId() == other.getId();
    }

    @Override
    public String toString() {
        return "AccountCredit(SUPER=" + super.toString() + ", creditLimit=" + creditLimit + ", paymentGraceDays="
                + paymentGraceDays + ")";
    }
}
