package bank.db;

import bank.util.TypeValidator;
import java.util.*;
import lombok.Getter;
import lombok.Setter;

public abstract class Account {
    @Getter
    private int id;

    @Getter
    private String name;

    @Getter
    @Setter
    private boolean isLocked;

    @Getter
    private Customer customer;

    private List<Transaction> transactions;

    public Account(int id, String name, boolean isLocked, Customer customer) {
        TypeValidator.validateId("Id", id);
        this.id = id;

        TypeValidator.validateNonEmptyText("Name", name);
        this.name = name;

        this.isLocked = isLocked;

        TypeValidator.validateNotNull("Customer", customer);
        this.customer = customer;

        this.transactions = new ArrayList<>();
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(this.transactions);
    }

    public void addTransaction(Transaction transaction) {
        TypeValidator.validateNotNull("Transaction", transaction);
        if (!this.equals(transaction.getInfo().getSource())) {
            throw new IllegalArgumentException(
                "Transaction " +
                transaction +
                " does not belong to this account " +
                this
            );
        }

        this.transactions.add(transaction);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Account)) return false;

        Account other = (Account) obj;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        return (
            "Account(id=" +
            id +
            ", name=" +
            name +
            ", isLocked=" +
            isLocked +
            ", customer=" +
            customer +
            ")"
        );
    }
}
