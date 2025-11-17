package bank.db;

import java.math.BigDecimal;
import java.time.LocalTime;

import lombok.Getter;

public class TransactionInfo {
    @Getter
    private Account source;

    @Getter
    private Account destination;

    @Getter
    private BigDecimal amount;

    @Getter
    private LocalTime time;
}
