package bank.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

final class _TestBank {

    @ParameterizedTest
    @CsvSource({ "1, 'First World Bank'", "2, 'The Big Bank'" })
    void testConstructorValid(int id, String name) {
        Bank bank = new Bank(id, name);
        assertEquals(id, bank.getId());
        assertEquals(name, bank.getName());
        assertEquals(new ArrayList<>(), bank.getBranches());
    }

    @ParameterizedTest
    @CsvSource(
        {
            "-3, 'First World Bank', 'Id `-3` is not a valid SQL id (must be > 0)'",
            "3,  '   ',              'Name `   ` is blank or starts and ends with trailing spaces'",
        }
    )
    void testConstructorInvalid(int id, String name, String error) {
        Exception e = assertThrows(
            IllegalArgumentException.class,
            () -> {
                new Bank(id, name);
            }
        );
        assertEquals(error, e.getMessage());
    }
}
