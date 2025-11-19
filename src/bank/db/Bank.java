package bank.db;

import java.util.*;

import bank.util.TypeValidator;
import lombok.Getter;

public class Bank {
    @Getter
    private int id;

    @Getter
    private String name;

    private List<Branch> branches;

    public Bank(int id, String name) {
        TypeValidator.validateId("Id", id);
        this.id = id;

        TypeValidator.validateNonEmptyText("Name", name);
        this.name = name;

        this.branches = new ArrayList<>();
    }

    public List<Branch> getBranches() {
        return Collections.unmodifiableList(this.branches);
    }

    protected void addBranch(Branch branch) {
        TypeValidator.validateNotNull("Branch", branch);
        if (!this.equals(branch.getBank())) {
            throw new IllegalArgumentException("Branch " + branch + " does not belong to this bank " + this);
        }

        this.branches.add(branch);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Bank))
            return false;

        Bank other = (Bank) obj;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        return "Bank(id=" + id + ", name=" + name + ")";
    }
}
