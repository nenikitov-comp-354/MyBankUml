package bank.db;

import java.math.BigDecimal;

import lombok.Getter;

public class AccountChecking extends Account {
    @Getter
    private BigDecimal monthlyFee;
}
