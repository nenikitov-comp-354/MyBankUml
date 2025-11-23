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

final class TestAccountChecking {

    @ParameterizedTest
    @CsvSource(
        {
            "1, 'John`s Checking', false, '10.17'",
            "2, 'John`s Wallet',   true,  '123.342'",
        }
    )
    void testConstructorValid(
        int id,
        String name,
        Boolean isLocked,
        String monthlyFee
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

        AccountChecking account = new AccountChecking(
            id,
            name,
            isLocked,
            customer,
            new BigDecimal(monthlyFee)
        );

        assertEquals(id, account.getId());
        assertEquals(name, account.getName());
        assertEquals(isLocked, account.isLocked());
        assertEquals(customer, account.getCustomer());
        assertEquals(new BigDecimal(monthlyFee), account.getMonthlyFee());
        assertEquals(new ArrayList<>(), account.getTransactions());
    }

    @ParameterizedTest
    @CsvSource(
        value = {
            "-3, 'John`s Checking', true,  '10.17',  'Id `-3` is not a valid SQL id (must be > 0)'",
            "1,  NULL,              true,  '10.17',  'Name is null'",
            "1,  '   ',             true,  '10.17',  'Name `   ` is blank or starts and ends with trailing spaces'",
            "1,  'John`s Checking', false, '10.17',  'Customer is null'",
            "1,  'John`s Checking', true,  'NULL',   'Monthly fee is null'",
            "1,  'John`s Checking', true,  '-10.17', 'Monthly fee `-10.17` is not positive or zero'",
        },
        nullValues = "NULL"
    )
    void testConstructorInvalid(
        int id,
        String name,
        boolean customerNotNull,
        String monthlyFee,
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
                new AccountChecking(
                    id,
                    name,
                    false,
                    customerNotNull ? customer : null,
                    monthlyFee != null ? new BigDecimal(monthlyFee) : null
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
        AccountChecking account1 = new AccountChecking(
            1,
            "My checking 1",
            false,
            customer,
            new BigDecimal("0.00")
        );
        AccountChecking account2 = new AccountChecking(
            2,
            "My checking 2",
            false,
            customer,
            new BigDecimal("10.00")
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
            "true,  'Transaction Transaction(id=1, info=TransactionInfo(source=AccountChecking(SUPER=Account(id=1, name=My checking 1, isLocked=false, customer=Customer(id=1, firstName=John, lastName=Big, dateOfBirth=1990-01-17, socialInsuranceNumber=123-456-789, phone=+15147892571, email=big-john@email.com, branch=Branch(id=1, address=Address, bank=Bank(id=1, name=First World Bank)))), monthlyFee=0.00), destination=AccountChecking(SUPER=Account(id=2, name=My checking 2, isLocked=false, customer=Customer(id=1, firstName=John, lastName=Big, dateOfBirth=1990-01-17, socialInsuranceNumber=123-456-789, phone=+15147892571, email=big-john@email.com, branch=Branch(id=1, address=Address, bank=Bank(id=1, name=First World Bank)))), monthlyFee=10.00), amount=17.79, time=2025-11-18T18:59)) does not belong to this account AccountChecking(SUPER=Account(id=2, name=My checking 2, isLocked=false, customer=Customer(id=1, firstName=John, lastName=Big, dateOfBirth=1990-01-17, socialInsuranceNumber=123-456-789, phone=+15147892571, email=big-john@email.com, branch=Branch(id=1, address=Address, bank=Bank(id=1, name=First World Bank)))), monthlyFee=10.00)'",
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
        Account account1 = new AccountChecking(
            1,
            "My checking 1",
            false,
            customer,
            new BigDecimal("0.00")
        );
        Account account2 = new AccountChecking(
            2,
            "My checking 2",
            false,
            customer,
            new BigDecimal("10.00")
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
