package bank.controllers;

import bank.db.Customer;
import bank.util.SceneManager;
import java.sql.SQLException;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class LoginController {
    @FXML
    private Button loginButton;

    @FXML
    private Button signUpButton;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Text errorText;

    private SceneManager sceneManager = SceneManager.getInstance();

    @FXML
    private void connectToDB(ActionEvent event) {
        try {
            String email = emailField.getText();
            String password = passwordField.getText();
            Optional<Customer> customerOpt = SceneManager
                .getInstance()
                .getDb()
                .customerLogin(email, password);

            if (customerOpt.isPresent()) {
                Customer customer = customerOpt.get();
                sceneManager.setCustomer(customer);
                sceneManager.switchScene("/fxml/UserPage.fxml", loginButton);
            } else {
                if (
                    emailField.getText().isEmpty() ||
                    passwordField.getText().isEmpty()
                ) {
                    errorText.setText("No username or password was provided");
                } else {
                    errorText.setText(
                        "Login failed. Username or password is incorrect"
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL exception occurred on login");
            System.exit(1);
        }
    }

    @FXML
    private void loadSignUp(ActionEvent event) {
        sceneManager.switchScene("/fxml/SignUpPage.fxml", signUpButton);
    }
}
