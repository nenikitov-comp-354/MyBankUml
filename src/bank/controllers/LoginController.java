package bank.controllers;

import java.sql.SQLException;
import java.util.Optional;

import bank.db.BankDb;
import bank.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class LoginController {

    @FXML private Button loginButton;
    @FXML private Button signUpButton;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Text errorText;

    private SceneManager sceneManager = SceneManager.getInstance();

    @FXML
    private void connectToDB(ActionEvent event) {
        try {
            // TODO: Use dao's when they're done. This isn't what we need to do
            BankDb db = new BankDb("localhost", Optional.empty(), "bank", usernameField.getText(), Optional.of(passwordField.getText()));

            sceneManager.switchScene("/fxml/UserPage.fxml", loginButton);
        } catch (SQLException e) {
            if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                errorText.setText("No username or password was provided");
            } else {
                errorText.setText("Login failed. Username or password is incorrect");
            }
        }
    }

    @FXML
    private void loadSignUp(ActionEvent event) {
        sceneManager.switchScene("/fxml/SignUpPage.fxml", signUpButton);
    }
}
