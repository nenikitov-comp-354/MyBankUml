package bank;

import bank.db.*;
import java.sql.SQLException;
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

        // for (Customer customer : db.getCustomersSearch(new String[] { "ar" })) {
        for (bank.db.Customer customer : db.getCustomersSearch(new String[] { "ar" })) {
            System.out.println(customer);
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
