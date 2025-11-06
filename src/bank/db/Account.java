package bank.db;

import java.util.*;

import lombok.Getter;

public abstract class Account {
    @Getter
    private int id;

    @Getter
    private String name;

    @Getter
    private boolean isLocked;

    private List<Transaction> transactions;

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(this.transactions);
    }
}
