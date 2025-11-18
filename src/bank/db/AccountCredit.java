package bank.db;

import java.math.BigDecimal;

import lombok.Getter;

public class AccountCredit extends Account {
    @Getter
    private BigDecimal creditLimit;

    @Getter
    private int paymentGraceDays;
}
