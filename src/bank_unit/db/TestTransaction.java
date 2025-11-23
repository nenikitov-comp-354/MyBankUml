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

final class TestTransaction {

    @ParameterizedTest
    @CsvSource({ "1", "2" })
    void testConstructorValid(int id) {
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
        Account account1 = new AccountChecking(
            1,
            "My checking",
            false,
            customer,
            new BigDecimal("10.99")
        );
        Account account2 = new AccountSavings(
            2,
            "My savings",
            false,
            customer,
            new BigDecimal("10.99")
        );
        TransactionInfo info = new TransactionInfo(
            account1,
            account2,
            new BigDecimal("89.00"),
            LocalDateTime.of(2025, 11, 18, 18, 59)
        );

        Transaction transaction = new Transaction(id, info);

        assertEquals(id, transaction.getId());
        assertEquals(info, transaction.getInfo());
    }

    @ParameterizedTest
    @CsvSource(
        value = {
            "-3, true,  'Id `-3` is not a valid SQL id (must be > 0)'",
            "1,  false,  'Info is null'",
        },
        nullValues = "NULL"
    )
    void testConstructorInvalid(int id, boolean infoNotNull, String error) {
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
        Account account1 = new AccountChecking(
            1,
            "My checking",
            false,
            customer,
            new BigDecimal("10.99")
        );
        Account account2 = new AccountSavings(
            2,
            "My savings",
            false,
            customer,
            new BigDecimal("10.99")
        );
        TransactionInfo info = new TransactionInfo(
            account1,
            account2,
            new BigDecimal("89.00"),
            LocalDateTime.of(2025, 11, 18, 18, 59)
        );

        Exception e = assertThrows(
            IllegalArgumentException.class,
            () -> {
                new Transaction(id, infoNotNull ? info : null);
            }
        );
        assertEquals(error, e.getMessage());
    }
}
