package bank.db;

import java.util.*;

import lombok.Getter;

public abstract class Bank {
    @Getter
    private int id;

    @Getter
    private String name;

    private List<Branch> branches;

    public List<Branch> getBranches() {
        return Collections.unmodifiableList(this.branches);
    }
}
