package bank.db;

import java.math.BigDecimal;

import lombok.Getter;

public class AccountSavings extends Account {
    @Getter
    private BigDecimal interestRate;
}
