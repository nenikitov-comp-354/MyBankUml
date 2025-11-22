package bank.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

final class _TestBranch {

    @ParameterizedTest
    @CsvSource({ "1, 'First World Branch'", "2, 'The Big Branch'" })
    void testConstructorValid(int id, String address) {
        Bank bank = new Bank(1, "My bank");

        Branch branch = new Branch(id, address, bank);

        assertEquals(id, branch.getId());
        assertEquals(address, branch.getAddress());
        assertEquals(bank, branch.getBank());
        assertEquals(new ArrayList<>(), branch.getCustomers());
    }

    @ParameterizedTest
    @CsvSource(
        value = {
            "-3, 'First World Branch', true,  'Id `-3` is not a valid SQL id (must be > 0)'",
            "3,  NULL,                 true,  'Address is null'",
            "3,  '   ',                true,  'Address `   ` is blank or starts and ends with trailing spaces'",
            "3,  'First World Branch', false, 'Bank is null'",
        },
        nullValues = "NULL"
    )
    void testConstructorInvalid(
        int id,
        String address,
        boolean bankNotNull,
        String error
    ) {
        Bank bank = new Bank(1, "My bank");

        Exception e = assertThrows(
            IllegalArgumentException.class,
            () -> {
                new Branch(id, address, bankNotNull ? bank : null);
            }
        );
        assertEquals(error, e.getMessage());
    }

    @Test
    void testAddCustomerValid() {
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
        branch.addCustomer(customer);
    }

    @ParameterizedTest
    @CsvSource(
        {
            "true,  'Customer Customer(id=1, firstName=John, lastName=Big, dateOfBirth=1990-01-17, socialInsuranceNumber=123-456-789, phone=+15147892571, email=big-john@email.com, branch=Branch(id=2, address=Address, bank=Bank(id=1, name=First World Bank))) does not belong to this branch Branch(id=1, address=Address, bank=Bank(id=1, name=First World Bank))'",
            "false, 'Customer is null'",
        }
    )
    void testAddCustomerInvalid(boolean customerNotNull, String error) {
        Bank bank = new Bank(1, "First World Bank");
        Branch branch1 = new Branch(1, "Address", bank);
        Branch branch2 = new Branch(2, "Address", bank);

        Customer customer = new Customer(
            1,
            "John",
            "Big",
            LocalDate.of(1990, 1, 17),
            "123-456-789",
            "+15147892571",
            "big-john@email.com",
            branch2
        );
        Exception e = assertThrows(
            IllegalArgumentException.class,
            () -> {
                branch1.addCustomer(customerNotNull ? customer : null);
            }
        );
        assertEquals(error, e.getMessage());
    }
}
