package bank.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;
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
        value = {
            "-3, 'First World Bank', 'Id `-3` is not a valid SQL id (must be > 0)'",
            "3,  NULL,               'Name is null'",
            "3,  '   ',              'Name `   ` is blank or starts and ends with trailing spaces'",
        },
        nullValues = "NULL"
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

    @Test
    void testAddBranchValid() {
        Bank bank = new Bank(1, "First World Bank");
        Branch branch = new Branch(1, "Address", bank);
        bank.addBranch(branch);
    }

    @ParameterizedTest
    @CsvSource(
        {
            "true,  'Branch Branch(id=1, address=Address, bank=Bank(id=2, name=The Big Bank)) does not belong to this bank Bank(id=1, name=First World Bank)'",
            "false, 'Branch is null'",
        }
    )
    void testAddBranchInvalid(boolean branchNotNull, String error) {
        Bank bank1 = new Bank(1, "First World Bank");
        Bank bank2 = new Bank(2, "The Big Bank");

        Branch branch = new Branch(1, "Address", bank2);
        Exception e = assertThrows(
            IllegalArgumentException.class,
            () -> {
                bank1.addBranch(branchNotNull ? branch : null);
            }
        );
        assertEquals(error, e.getMessage());
    }
}
