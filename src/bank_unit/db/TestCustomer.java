package bank_unit.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import bank.db.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

final class TestCustomer {

    @ParameterizedTest
    @CsvSource(
        {
            "1, 'John', 'Big', 1990, 1, 17, '123-456-789', '+15147892571', 'big-john@email.com', false",
            "2, 'Jane', 'Doe', 1800, 3, 24, '789-456-123', '+48864632577', 'jane-doe@email.com', false",
        }
    )
    void testConstructorValid(
        int id,
        String firstName,
        String lastName,
        int dateOfBirthYear,
        int dateOfBirthMonth,
        int dateOfBirthDay,
        String socialInsuranceNumber,
        String phone,
        String email,
        Boolean adminStat
    ) {
        Bank bank = new Bank(1, "My bank");
        Branch branch = new Branch(1, "Address", bank);

        Customer customer = new Customer(
            id,
            firstName,
            lastName,
            LocalDate.of(dateOfBirthYear, dateOfBirthMonth, dateOfBirthDay),
            socialInsuranceNumber,
            phone,
            email,
            branch,
            adminStat
            
        );

        assertEquals(id, customer.getId());
        assertEquals(firstName, customer.getFirstName());
        assertEquals(lastName, customer.getLastName());
        assertEquals(
            LocalDate.of(dateOfBirthYear, dateOfBirthMonth, dateOfBirthDay),
            customer.getDateOfBirth()
        );
        assertEquals(phone, customer.getPhone());
        assertEquals(email, customer.getEmail());
        assertEquals(branch, customer.getBranch());
        assertEquals(new ArrayList<>(), customer.getAccounts());
        assertEquals(adminStat, customer.getAdminStatus());
    }

    @ParameterizedTest
    @CsvSource(
        value = {
            "-3, 'John', 'Big', true,  '123-456-789', '+15147892571', 'big-john@email.com', true, false,  'Id `-3` is not a valid SQL id (must be > 0)'",
            "1,  NULL,   'Big', true,  '123-456-789', '+15147892571', 'big-john@email.com', true, false,  'First name is null'",
            "1,  '   ',  'Big', true,  '123-456-789', '+15147892571', 'big-john@email.com', true, false,  'First name `   ` is blank or starts and ends with trailing spaces'",
            "1,  'John', NULL,  true,  '123-456-789', '+15147892571', 'big-john@email.com', true, false,  'Last name is null'",
            "1,  'John', '   ', true,  '123-456-789', '+15147892571', 'big-john@email.com', true, false,  'Last name `   ` is blank or starts and ends with trailing spaces'",
            "1,  'John', 'Big', false, '123-456-789', '+15147892571', 'big-john@email.com', true, false,  'Date of birth is null'",
            "1,  'John', 'Big', true,  NULL,          '+15147892571', 'big-john@email.com', true, false,  'Social insurance number is null'",
            "1,  'John', 'Big', true,  'abcdefghijk', '+15147892571', 'big-john@email.com', true, false,  'Social insurance number `abcdefghijk` does not match social insurance format of 3 groups of 3 digits separated by `-`'",
            "1,  'John', 'Big', true,  '123-456-789', NULL,           'big-john@email.com', true, false,  'Phone is null'",
            "1,  'John', 'Big', true,  '123-456-789', 'fhjdsljfdlsj', 'big-john@email.com', true, false,  'Phone `fhjdsljfdlsj` does not match phone number format of `+` followed by 2-15 digits'",
            "1,  'John', 'Big', true,  '123-456-789', '+15147892571', NULL,                 true, false,  'Email is null'",
            "1,  'John', 'Big', true,  '123-456-789', '+15147892571', 'invalidemail',       true, false,  'Email `invalidemail` does not match email format of some text followed by `@` followed by some text'",
            "1,  'John', 'Big', true,  '123-456-789', '+15147892571', 'big-john@email.com', false, false, 'Branch is null'",
        },
        nullValues = "NULL"
    )
    void testConstructorInvalid(
        int id,
        String firstName,
        String lastName,
        boolean dateOfBirthNotNull,
        String socialInsuranceNumber,
        String phone,
        String email,
        boolean branchNotNull,
        boolean adminStat,
        String error
    ) {
        Bank bank = new Bank(1, "My bank");
        Branch branch = new Branch(1, "Address", bank);

        Exception e = assertThrows(
            IllegalArgumentException.class,
            () -> {
                new Customer(
                    id,
                    firstName,
                    lastName,
                    dateOfBirthNotNull ? LocalDate.of(1990, 1, 17) : null,
                    socialInsuranceNumber,
                    phone,
                    email,
                    branchNotNull ? branch : null,
                    adminStat
                );
            }
        );
        assertEquals(error, e.getMessage());
    }

    @Test
    void testAddAccountValid() {
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
            branch,
            false
        );
        Account account = new AccountChequing(
            1,
            "My chequing",
            false,
            customer,
            new BigDecimal("100.00")
        );
        customer.addAccount(account);
    }

    @ParameterizedTest
    @CsvSource(
        {
            "true,  'Account AccountChequing(SUPER=Account(id=1, name=My chequing, isLocked=false, customer=Customer(id=2, firstName=Jane, lastName=Doe, dateOfBirth=1800-03-24, socialInsuranceNumber=789-456-123, phone=+48864632577, email=jane-doe@email.com, branch=Branch(id=1, address=Address, bank=Bank(id=1, name=First World Bank)))), monthlyFee=100.00) does not belong to this customer Customer(id=1, firstName=John, lastName=Big, dateOfBirth=1990-01-17, socialInsuranceNumber=123-456-789, phone=+15147892571, email=big-john@email.com, branch=Branch(id=1, address=Address, bank=Bank(id=1, name=First World Bank)))'",
            "false, 'Account is null'",
        }
    )
    void testAddAccountInvalid(boolean accountNotNull, String error) {
        Bank bank = new Bank(1, "First World Bank");
        Branch branch = new Branch(1, "Address", bank);
        Customer customer1 = new Customer(
            1,
            "John",
            "Big",
            LocalDate.of(1990, 1, 17),
            "123-456-789",
            "+15147892571",
            "big-john@email.com",
            branch,
            false
        );
        Customer customer2 = new Customer(
            2,
            "Jane",
            "Doe",
            LocalDate.of(1800, 3, 24),
            "789-456-123",
            "+48864632577",
            "jane-doe@email.com",
            branch,
            false
        );

        Account account = new AccountChequing(
            1,
            "My chequing",
            false,
            customer2,
            new BigDecimal("100.00")
        );
        Exception e = assertThrows(
            IllegalArgumentException.class,
            () -> {
                customer1.addAccount(accountNotNull ? account : null);
            }
        );
        assertEquals(error, e.getMessage());
    }
}
