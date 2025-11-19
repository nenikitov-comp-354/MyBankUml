package bank.db;

import bank.util.TypeValidator;
import lombok.Getter;

public class Transaction {
    @Getter
    private int id;

    @Getter
    private TransactionInfo info;

    public Transaction(int id, TransactionInfo info) {
        TypeValidator.validateId("Id", id);
        this.id = id;

        TypeValidator.validateNotNull("Info", info);
        this.info = info;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Transaction))
            return false;

        Transaction other = (Transaction) obj;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        return "Transaction(id=" + id + ", info=" + info + ")";
    }
}
