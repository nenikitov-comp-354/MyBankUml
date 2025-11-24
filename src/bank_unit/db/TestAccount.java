package bank_unit.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import bank.db.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

final class TestAccount {

    class AccountSimple extends Account {

        public AccountSimple(
            int id,
            String name,
            boolean isLocked,
            Customer customer
        ) {
            super(id, name, isLocked, customer);
        }
    }

    @ParameterizedTest
    @CsvSource({ "1, 'John`s Simple', false", "2, 'John`s Wallet', true" })
    void testConstructorValid(int id, String name, Boolean isLocked) {
        Bank bank = new Bank(1, "My bank");
        Branch branch = new Branch(1, "Address", bank);
        Customer customer = new Customer(
            1,
            "John",
            "Big",
            LocalDate.of(1990, 1, 17),
            "123-456-789",
            "+15147892571",
            "big-john@email.com",
            branch
        );

        Account account = new AccountSimple(id, name, isLocked, customer);

        assertEquals(id, account.getId());
        assertEquals(name, account.getName());
        assertEquals(isLocked, account.isLocked());
        assertEquals(customer, account.getCustomer());
        assertEquals(new ArrayList<>(), account.getTransactions());
    }

    @ParameterizedTest
    @CsvSource(
        value = {
            "-3, 'John`s Simple', true,  'Id `-3` is not a valid SQL id (must be > 0)'",
            "1,  NULL,            true,  'Name is null'",
            "1,  '   ',           true,  'Name `   ` is blank or starts and ends with trailing spaces'",
            "1,  'John`s Simple', false, 'Customer is null'",
        },
        nullValues = "NULL"
    )
    void testConstructorInvalid(
        int id,
        String name,
        boolean customerNotNull,
        String error
    ) {
        Bank bank = new Bank(1, "My bank");
        Branch branch = new Branch(1, "Address", bank);
        Customer customer = new Customer(
            1,
            "John",
            "Big",
            LocalDate.of(1990, 1, 17),
            "123-456-789",
            "+15147892571",
            "big-john@email.com",
            branch
        );

        Exception e = assertThrows(
            IllegalArgumentException.class,
            () -> {
                new AccountSimple(
                    id,
                    name,
                    false,
                    customerNotNull ? customer : null
                );
            }
        );
        assertEquals(error, e.getMessage());
    }

    @Test
    void testAddTransactionValid() {
        Bank bank = new Bank(1, "First World Bank");
        Branch branch = new Branch(1, "Address", bank);
        Customer customer = new Customer(
            1,
            "John",
            "Big",
            LocalDate.of(1990, 1, 17),
            "123-456-789",
            "+15147892571",
            "big-john@email.com",
            branch
        );
        Account account1 = new AccountSimple(1, "My simple 1", false, customer);
        Account account2 = new AccountSimple(2, "My simple 2", false, customer);
        Transaction transaction = new Transaction(
            1,
            new TransactionInfo(
                account1,
                account2,
                new BigDecimal("17.79"),
                LocalDateTime.of(2025, 11, 18, 18, 59)
            )
        );
        account1.addTransaction(transaction);
        account2.addTransaction(transaction);
    }

    @ParameterizedTest
    @CsvSource(
        {
            "true,  'Transaction Transaction(id=1, info=TransactionInfo(source=Account(id=1, name=My simple 1, isLocked=false, customer=Customer(id=1, firstName=John, lastName=Big, dateOfBirth=1990-01-17, socialInsuranceNumber=123-456-789, phone=+15147892571, email=big-john@email.com, branch=Branch(id=1, address=Address, bank=Bank(id=1, name=First World Bank)))), destination=Account(id=2, name=My simple 2, isLocked=false, customer=Customer(id=1, firstName=John, lastName=Big, dateOfBirth=1990-01-17, socialInsuranceNumber=123-456-789, phone=+15147892571, email=big-john@email.com, branch=Branch(id=1, address=Address, bank=Bank(id=1, name=First World Bank)))), amount=17.79, time=2025-11-18T18:59)) does not belong to this account Account(id=3, name=My simple 3, isLocked=false, customer=Customer(id=1, firstName=John, lastName=Big, dateOfBirth=1990-01-17, socialInsuranceNumber=123-456-789, phone=+15147892571, email=big-john@email.com, branch=Branch(id=1, address=Address, bank=Bank(id=1, name=First World Bank))))'",
            "false, 'Transaction is null'",
        }
    )
    void testAddTransactionInvalid(boolean transactionNotNull, String error) {
        Bank bank = new Bank(1, "First World Bank");
        Branch branch = new Branch(1, "Address", bank);
        Customer customer = new Customer(
            1,
            "John",
            "Big",
            LocalDate.of(1990, 1, 17),
            "123-456-789",
            "+15147892571",
            "big-john@email.com",
            branch
        );
        Account account1 = new AccountSimple(1, "My simple 1", false, customer);
        Account account2 = new AccountSimple(2, "My simple 2", false, customer);
        Account account3 = new AccountSimple(3, "My simple 3", false, customer);
        Transaction transaction = new Transaction(
            1,
            new TransactionInfo(
                account1,
                account2,
                new BigDecimal("17.79"),
                LocalDateTime.of(2025, 11, 18, 18, 59)
            )
        );

        Exception e = assertThrows(
            IllegalArgumentException.class,
            () -> {
                account3.addTransaction(
                    transactionNotNull ? transaction : null
                );
            }
        );
        assertEquals(error, e.getMessage());
    }
}
