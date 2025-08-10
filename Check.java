package com.mftplus.Tamrin.BankUml;

public class Check extends Account {
    public Check(Customer customer) {
        super(customer);

    }
    public void title(){
        System.out.println("**Check Title**");
    }

    @Override
    public void pay() {
        //check title
        title();
        System.out.println("\tCheck payment for customer: " + customer.getName());
    }

    @Override
    public void receipt() {
        System.out.println("\tCheck payment for customer: " + customer.getName());
    }
}

