package bank.db;

import lombok.Getter;

public abstract class Branch {
    @Getter
    private int id;

    @Getter
    private boolean address;

    @Getter
    private Bank bank;
}
