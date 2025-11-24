package bank.db;

import bank.util.TypeValidator;
import java.math.BigDecimal;
import java.util.*;
import lombok.Getter;

public abstract class Account {
    @Getter
    private int id;

    @Getter
    private String name;

    @Getter
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

    public BigDecimal getBalance() {
        BigDecimal balance = new BigDecimal(0);

        for (Transaction t : this.transactions) {
            if (this.equals(t.getInfo().getSource())) {
                balance = balance.subtract(t.getInfo().getAmount());
            } else {
                balance = balance.add(t.getInfo().getAmount());
            }
        }

        return balance;
    }

    public void addTransaction(Transaction transaction) {
        TypeValidator.validateNotNull("Transaction", transaction);
        if (
            !this.equals(transaction.getInfo().getSource()) &&
            !this.equals(transaction.getInfo().getDestination())
        ) {
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
