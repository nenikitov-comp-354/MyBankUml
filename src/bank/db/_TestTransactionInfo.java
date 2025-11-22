package bank.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

final class _TestTransactionInfo {

    @ParameterizedTest
    @CsvSource(
        {
            "'account1', 'account2', '10.15', 2025, 11, 18, 18, 59",
            "'account2', 'account1', '2371',  2025, 11, 21, 20, 46",
        }
    )
    void testConstructorValid(
        String source,
        String destination,
        String amount,
        int timeYear,
        int timeMonth,
        int timeDay,
        int timeHour,
        int timeMinute
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
            source.equals("account1") ? account1 : account2,
            destination.equals("account1") ? account1 : account2,
            new BigDecimal(amount),
            LocalDateTime.of(timeYear, timeMonth, timeDay, timeHour, timeMinute)
        );

        assertEquals(
            source.equals("account1") ? account1 : account2,
            info.getSource()
        );
        assertEquals(
            destination.equals("account1") ? account1 : account2,
            info.getDestination()
        );
        assertEquals(new BigDecimal(amount), info.getAmount());
        assertEquals(
            LocalDateTime.of(
                timeYear,
                timeMonth,
                timeDay,
                timeHour,
                timeMinute
            ),
            info.getTime()
        );
    }

    @ParameterizedTest
    @CsvSource(
        value = {
            "NULL,      'account2',  '10.15',  true,  'Source is null'",
            "'account1', NULL,       '10.15',  true,  'Destination is null'",
            "'account1', 'account1', '10.15',  true,  'Source and destination accounts cannot be the same'",
            "'account1', 'account1', NULL,     true,  'Amount is null'",
            "'account1', 'account1', '-17.65', false, 'Amount `-17.65` is not positive or zero'",
        },
        nullValues = "NULL"
    )
    void testConstructorInvalid(
        String source,
        String destination,
        String amount,
        boolean timeNotNull,
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

        Exception e = assertThrows(
            IllegalArgumentException.class,
            () -> {
                new TransactionInfo(
                    source != null
                        ? (source.equals("account1") ? account1 : account2)
                        : null,
                    destination != null
                        ? (destination.equals("account1") ? account1 : account2)
                        : null,
                    amount != null ? new BigDecimal(amount) : null,
                    timeNotNull ? LocalDateTime.of(2025, 11, 18, 18, 59) : null
                );
            }
        );
        assertEquals(error, e.getMessage());
    }
}
