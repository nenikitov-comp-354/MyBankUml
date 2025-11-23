package bank.controllers;

import bank.util.SceneManager;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Text;

public class SignUpController {
    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private DatePicker dobField;

    @FXML
    private PasswordField sinField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private TextField branchNameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Text errorText;

    @FXML
    private Button signUpConfirmButton;

    private SceneManager sceneManager = SceneManager.getInstance();

    @FXML
    private void signUpUser(ActionEvent event) {
        List<TextInputControl> fields = List.of(
            firstNameField,
            lastNameField,
            sinField,
            phoneNumberField,
            branchNameField,
            emailField,
            passwordField,
            confirmPasswordField
        );
        boolean anyEmpty = fields.stream().anyMatch(f -> f.getText().isEmpty());

        if (anyEmpty || dobField.getValue() == null) {
            errorText.setText("One or more fields are empty");
        } else if (
            !passwordField.getText().equals(confirmPasswordField.getText())
        ) {
            errorText.setText("Password is not the same");
        } else {
            // TODO: Add logic that will add the user to the database
            sceneManager.switchScene(
                "/fxml/LogInPage.fxml",
                signUpConfirmButton
            );
        }
    }
}
