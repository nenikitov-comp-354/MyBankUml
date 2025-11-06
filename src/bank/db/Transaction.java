package bank.db;

import lombok.Getter;

public class Transaction {
    @Getter
    private int id;

    @Getter
    private TransactionInfo info;
}
