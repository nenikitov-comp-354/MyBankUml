package bank;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import bank.db.BankDb;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

// HACK: The only way to make Maven correctly package a JavaFX application
// is to have a separate class for application and call it from the main.
// [Solution](https://stackoverflow.com/a/70809214)

public class App extends Application {

    private BankDb db;

    @FXML
    private Button loginButton;
    @FXML
    private Button signUpButton;
    @FXML
    private Button signUpConfirmButton;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Text errorText;


    public static void main(String[] args) {
        App.launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        // TODO: Use the DB connection
        // BankDb db = new BankDb("localhost", Optional.empty(), "bank", "admin", Optional.of("admin"));

        // TODO: Remove this example when not needed
        // This is how BankDb will initialize all classes. The order is important to avoid interdependence.
        // This code must be done inside `bank.db` package since all `add...` functions are protected.
        // This is done to prevent any data inconsistencies introduced outside the namespace that is responsible for both class and DB modifications.
        //
        // Bank bank = new Bank(1, "My bank");
        //
        // Branch branch = new Branch(1, "My branch", bank);
        // bank.addBranch(branch);
        //
        // Customer customer = new Customer(1, "Big", "John", LocalDate.of(1990, 1, 17), "123-456-789", "+15147892571", "big-john@email.com", branch);
        // branch.addCustomer(customer);
        //
        // Account account1 = new AccountChecking(1, "John's checking", false, customer, new BigDecimal("0.0")); customer.addAccount(account1);
        // Account account2 = new AccountSavings(2, "John's savings", false, customer, new BigDecimal("1.12"));
        // customer.addAccount(account2);
        //
        // TransactionInfo transactionInfo = new TransactionInfo(account1, account2, new BigDecimal("12.77"), LocalDateTime.of(2025, 11, 18, 18, 59));
        // Transaction transaction = new Transaction(1, transactionInfo);
        // account1.addTransaction(transaction);
        //
        // System.out.println(transaction);;

        Parent root = FXMLLoader.load(
            getClass().getResource("/fxml/LogInPage.fxml")
        );
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void connectToDB(ActionEvent event) {
        try {
            String username = usernameField.getText();
            String password = passwordField.getText();
            // TODO: Use dao's when they're done. This isn't what we need to do
            db = new BankDb("localhost", Optional.empty(), "bank", username, Optional.of(password));

            Parent root = FXMLLoader.load(getClass().getResource("/fxml/UserPage.fxml"));
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (SQLException e) {
            if (usernameField.getText() == "" || passwordField.getText() == "") {
                errorText.setText("No username or password was provided");
            }
            else {
                errorText.setText("Login failed. Username or password is incorrect");
            }
        } catch (IOException e) {
            System.err.println("UserPage.fxml was not found");
            System.exit(1);
        }
    }

    @FXML
    private void loadSignUp(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/SignUpPage.fxml"));
            Stage stage = (Stage) signUpButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            System.err.println("SignUpPage.fxml was not found");
            System.exit(1);
        }
    }

    @FXML
    private void signUpUser(ActionEvent event) {
        try {
            // TODO: Add logic that will add the user to the database

            Parent root = FXMLLoader.load(getClass().getResource("/fxml/LogInPage.fxml"));
            Stage stage = (Stage) signUpConfirmButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            System.err.println("LogInPage.fxml was not found");
            System.exit(1);
        }
    }
}
