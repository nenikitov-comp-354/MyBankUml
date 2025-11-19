package bank.db;

import java.util.*;

import bank.util.TypeValidator;
import lombok.Getter;

public class Branch {
    @Getter
    private int id;

    @Getter
    private String address;

    @Getter
    private Bank bank;

    private List<Customer> customers;

    public Branch(int id, String address, Bank bank) {
        TypeValidator.validateId("Id", id);
        this.id = id;

        TypeValidator.validateNonEmptyText("Address", address);
        this.address = address;

        TypeValidator.validateNotNull("Bank", bank);
        this.bank = bank;

        this.customers = new ArrayList<>();
    }

    public List<Customer> getCustomers() {
        return Collections.unmodifiableList(this.customers);
    }

    protected void addCustomer(Customer customer) {
        TypeValidator.validateNotNull("Customer", customer);
        if (!this.equals(customer.getBranch())) {
            throw new IllegalArgumentException("Customer " + customer + " does not belong to this branch " + this);
        }
        this.customers.add(customer);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Branch))
            return false;

        Branch other = (Branch) obj;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        return "Branch(id=" + id + ", address=" + address + ", bank=" + bank + ")";
    }
}
