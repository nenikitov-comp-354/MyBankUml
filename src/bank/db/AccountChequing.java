package bank.db;

import bank.util.TypeValidator;
import java.math.BigDecimal;
import lombok.Getter;

public class AccountChequing extends Account {
    @Getter
    private BigDecimal monthlyFee;

    public AccountChequing(
        int id,
        String name,
        boolean isLocked,
        Customer customer,
        BigDecimal monthlyFee
    ) {
        super(id, name, isLocked, customer);
        TypeValidator.validatePositiveMoney("Monthly fee", monthlyFee);
        this.monthlyFee = monthlyFee;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AccountChequing)) return false;

        AccountChequing other = (AccountChequing) obj;
        return this.getId() == other.getId();
    }

    @Override
    public String toString() {
        return (
            "AccountChequing(SUPER=" +
            super.toString() +
            ", monthlyFee=" +
            monthlyFee +
            ")"
        );
    }
}
