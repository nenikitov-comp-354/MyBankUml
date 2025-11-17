package bank;

import java.sql.SQLException;
import java.util.*;

import bank.db.*;

public class Main {
    public static void main(String[] args) throws SQLException {
        BankDb db = new BankDb("localhost", Optional.empty(), "bank", "admin", Optional.of("admin"));
        System.out.println(db);
    }
}
