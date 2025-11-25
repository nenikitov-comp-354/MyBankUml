package bank;

import bank.db.*;
import bank.db.operation.OperationLock;
import bank.db.operation.OperationTransaction;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

// HACK: The only way to make Maven correctly package a JavaFX application
// is to have a separate class for application and call it from the main.
// [Solution](https://stackoverflow.com/a/70809214)
public class App extends Application {

    public static void main(String[] args) {
        App.launch(args);
    }

    @Override
    public void start(Stage stage) throws SQLException {
        // TODO: Use the DB connection
        BankDb db = new BankDb(
            "localhost",
            Optional.empty(),
            "bank",
            "admin",
            Optional.of("admin")
        );
        db.connect();
        db.addOperation(
            new OperationTransaction(
                new TransactionInfo(
                    db.getAccounts().get(1),
                    db.getAccounts().get(2),
                    new BigDecimal("250.79"),
                    LocalDateTime.now()
                )
            )
        );
        db.addOperation(new OperationLock(db.getAccounts().get(6), true));
        db.processOperations();

        System.out.println("LOCKED " + db.getAccounts().get(6).isLocked());
        System.out.println("TRANSACTIONS" + db.getTransactions().get(6));
        System.out.println("BALANCE " + db.getAccounts().get(1).getBalance());

        // for (Customer customer : db.getCustomersSearch(new String[] { "ar" })) {
        for (bank.db.Customer customer : db.getCustomersSearch(
            new String[] { "ar" }
        )) {
            System.out.println("CUSTOMER " + customer);
        }

        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label(
            "Hello, JavaFX " +
            javafxVersion +
            ", running on Java " +
            javaVersion +
            "."
        );
        Scene scene = new Scene(new StackPane(l), 640, 480);
        stage.setScene(scene);
        stage.show();
    }
}
