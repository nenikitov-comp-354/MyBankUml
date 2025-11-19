package bank.db;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import bank.util.TypeValidator;
import lombok.Getter;

public class TransactionInfo {
    @Getter
    private Account source;

    @Getter
    private Account destination;

    @Getter
    private BigDecimal amount;

    @Getter
    private LocalDateTime time;

    public TransactionInfo(Account source, Account destination, BigDecimal amount, LocalDateTime time) {
        TypeValidator.validateNotNull("Source", source);
        this.source = source;

        TypeValidator.validateNotNull("Destination", destination);
        this.destination = destination;

        TypeValidator.validatePositiveMoney("Amount", amount);
        if (amount.signum() == 0) {
            throw new IllegalArgumentException("Amount `" + amount + "` cannot be 0");
        }
        this.amount = amount;

        TypeValidator.validateNotNull("time", time);
        this.time = time;

        if (this.source.equals(destination)) {
            throw new IllegalArgumentException("Source and destination accounts cannot be the same");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof TransactionInfo))
            return false;

        TransactionInfo other = (TransactionInfo) obj;
        return (this.source.equals(other.source) && this.destination.equals(other.destination)
                && this.amount.equals(other.amount) && this.time.equals(other.time));
    }

    @Override
    public String toString() {
        return "TransactionInfo(source=" + source + ", destination=" + destination + ", amount=" + amount + ", time="
                + time + ")";
    }
}
