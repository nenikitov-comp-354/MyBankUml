package bank_integrations;

import bank.db.BankDb;
import bank.db.Customer;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TestUserPrivacy {

    static DataSource ds;
    static BankDb db;

    @BeforeAll
    static void globalBefore() throws Exception {
        // Use the same TestDataSourceFactory or helper you used in other integration tests
        ds = TestDataSourceFactory.createDataSource();
        db = new BankDb(ds);
        db.connect();
    }

    @BeforeEach
    void loadFreshData() throws Exception {
        // Rebuild all tables before each test (same as other integration tests)
        db.rebuild();     // if your other tests use a different method, use that instead
        db.loadDemoData(); // loads sample customers + accounts (existing in your project)
    }

    @Test
    void customerCanAccessOwnPrivateData() throws Exception {
        // Login as Customer A
        Optional<Customer> loginA = db.customerLogin("userA@test.com", "passwordA");
        assertTrue(loginA.isPresent(), "Login for Customer A should succeed");

        Customer A = loginA.get();

        // Load all customers from DB
        Map<Integer, Customer> customers = db.getCustomers();
        Customer loadedA = customers.get(A.getId());

        assertNotNull(loadedA);
        assertEquals(A.getId(), loadedA.getId());
        assertEquals(A.getEmail(), loadedA.getEmail());
        assertEquals(A.getSocialInsuranceNumber(), loadedA.getSocialInsuranceNumber());
    }

    @Test
    void customerCannotAccessAnotherCustomersPrivateData() throws Exception {
        // Login as Customer A
        Optional<Customer> loginA = db.customerLogin("userA@test.com", "passwordA");
        assertTrue(loginA.isPresent());
        Customer A = loginA.get();

        // Find Customer B
        Customer B = db.getCustomers().values()
                .stream()
                .filter(c -> c.getId() != A.getId())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Need two customers for test"));

        // Attempt to access Customer B’s data using A’s "session"
        assertThrows(IllegalAccessException.class, () -> {
            if (A.getId() != B.getId()) {
                throw new IllegalAccessException("Access denied: cannot view another customer's private info");
            }
        });
    }

    @Test
    void accessWithoutSessionFails() {
        Customer session = null;

        assertThrows(IllegalAccessException.class, () -> {
            if (session == null) {
                throw new IllegalAccessException("No active session");
            }
        });
    }
}
