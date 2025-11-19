package bank.db;

import java.math.BigDecimal;

import bank.util.TypeValidator;
import lombok.Getter;

public class AccountChecking extends Account {
    @Getter
    private BigDecimal monthlyFee;

    public AccountChecking(int id, String name, boolean isLocked, Customer customer, BigDecimal monthlyFee) {
        super(id, name, isLocked, customer);

        TypeValidator.validatePositiveMoney("Monthly fee", monthlyFee);
        this.monthlyFee = monthlyFee;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof AccountChecking))
            return false;

        AccountChecking other = (AccountChecking) obj;
        return this.getId() == other.getId();
    }

    @Override
    public String toString() {
        return "AccountChecking(SUPER=" + super.toString() + ", monthlyFee=" + monthlyFee + ")";
    }
}
