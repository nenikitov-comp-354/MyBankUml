package com.mftplus.Tamrin.BankUml;

public class Saving extends Account {
    public Saving(Customer customer) {
        super(customer);
    }
    public void title(){
        System.out.println("**Payments**");
    };

    @Override
    public void pay() {
        title();
        System.out.println("\tPayment From saving account For: " + customer.getName());
    }

    @Override
    public void receipt() {
        System.out.println("\tPayment receipt from saving account for: " + customer.getName());
    }
}
