package bank.db;

import java.math.BigDecimal;

import bank.util.TypeValidator;
import lombok.Getter;

public class AccountSavings extends Account {
    @Getter
    private BigDecimal interestRate;

    public AccountSavings(int id, String name, boolean isLocked, Customer customer, BigDecimal interestRate) {
        super(id, name, isLocked, customer);

        TypeValidator.validatePositiveMoney("Interest rate", interestRate);
        this.interestRate = interestRate;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof AccountSavings))
            return false;

        AccountSavings other = (AccountSavings) obj;
        return this.getId() == other.getId();
    }

    @Override
    public String toString() {
        return "AccountSavings(SUPER=" + super.toString() + ", interestRate=" + interestRate + ")";
    }
}
