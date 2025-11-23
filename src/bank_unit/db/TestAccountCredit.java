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

final class TestAccountCredit {

    @ParameterizedTest
    @CsvSource(
        {
            "1, 'John`s Credit', false, '10.17', 18",
            "2, 'John`s Wallet', true,  '123.342', 21",
        }
    )
    void testConstructorValid(
        int id,
        String name,
        Boolean isLocked,
        String creditLimit,
        int paymentGraceDays
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

        AccountCredit account = new AccountCredit(
            id,
            name,
            isLocked,
            customer,
            new BigDecimal(creditLimit),
            paymentGraceDays
        );

        assertEquals(id, account.getId());
        assertEquals(name, account.getName());
        assertEquals(isLocked, account.isLocked());
        assertEquals(customer, account.getCustomer());
        assertEquals(new BigDecimal(creditLimit), account.getCreditLimit());
        assertEquals(paymentGraceDays, account.getPaymentGraceDays());
        assertEquals(new ArrayList<>(), account.getTransactions());
    }

    @ParameterizedTest
    @CsvSource(
        value = {
            "-3, 'John`s Credit', true,  '10.17',  17, 'Id `-3` is not a valid SQL id (must be > 0)'",
            "1,  NULL,            true,  '10.17',  17, 'Name is null'",
            "1,  '   ',           true,  '10.17',  17, 'Name `   ` is blank or starts and ends with trailing spaces'",
            "1,  'John`s Credit', false, '10.17',  17, 'Customer is null'",
            "1,  'John`s Credit', true,  'NULL',   17, 'Credit limit is null'",
            "1,  'John`s Credit', true,  '-10.17', 17, 'Credit limit `-10.17` is not positive or zero'",
            "1,  'John`s Credit', true,  '10.17',  -17,'Payment grace days `-17` is not positive'",
        },
        nullValues = "NULL"
    )
    void testConstructorInvalid(
        int id,
        String name,
        boolean customerNotNull,
        String creditLimit,
        int paymentGraceDays,
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
                new AccountCredit(
                    id,
                    name,
                    false,
                    customerNotNull ? customer : null,
                    creditLimit != null ? new BigDecimal(creditLimit) : null,
                    paymentGraceDays
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
        AccountCredit account1 = new AccountCredit(
            1,
            "My credit 1",
            false,
            customer,
            new BigDecimal("0.00"),
            17
        );
        AccountCredit account2 = new AccountCredit(
            2,
            "My credit 2",
            false,
            customer,
            new BigDecimal("10.00"),
            21
        );
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
    }

    @ParameterizedTest
    @CsvSource(
        {
            "true,  'Transaction Transaction(id=1, info=TransactionInfo(source=AccountCredit(SUPER=Account(id=1, name=My credit 1, isLocked=false, customer=Customer(id=1, firstName=John, lastName=Big, dateOfBirth=1990-01-17, socialInsuranceNumber=123-456-789, phone=+15147892571, email=big-john@email.com, branch=Branch(id=1, address=Address, bank=Bank(id=1, name=First World Bank)))), creditLimit=0.00, paymentGraceDays=17), destination=AccountCredit(SUPER=Account(id=2, name=My credit 2, isLocked=false, customer=Customer(id=1, firstName=John, lastName=Big, dateOfBirth=1990-01-17, socialInsuranceNumber=123-456-789, phone=+15147892571, email=big-john@email.com, branch=Branch(id=1, address=Address, bank=Bank(id=1, name=First World Bank)))), creditLimit=10.00, paymentGraceDays=21), amount=17.79, time=2025-11-18T18:59)) does not belong to this account AccountCredit(SUPER=Account(id=2, name=My credit 2, isLocked=false, customer=Customer(id=1, firstName=John, lastName=Big, dateOfBirth=1990-01-17, socialInsuranceNumber=123-456-789, phone=+15147892571, email=big-john@email.com, branch=Branch(id=1, address=Address, bank=Bank(id=1, name=First World Bank)))), creditLimit=10.00, paymentGraceDays=21)'",
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
        Account account1 = new AccountCredit(
            1,
            "My credit 1",
            false,
            customer,
            new BigDecimal("0.00"),
            17
        );
        Account account2 = new AccountCredit(
            2,
            "My credit 2",
            false,
            customer,
            new BigDecimal("10.00"),
            21
        );
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
                account2.addTransaction(
                    transactionNotNull ? transaction : null
                );
            }
        );
        assertEquals(error, e.getMessage());
    }
}
