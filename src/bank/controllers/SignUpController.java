package bank.controllers;

import bank.db.Branch;
import bank.db.operation.OperationNewCustomer;
import bank.util.SceneManager;
import bank.util.TypeValidator;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
    private ComboBox<String> branchComboBox;

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
    List<Map.Entry<Integer, Branch>> branchList;

    public void initialize() {
        Map<Integer, Branch> branches = sceneManager.getDb().getBranches();
        branchList = new ArrayList<>(branches.entrySet());

        for (Map.Entry<Integer, Branch> entry : branchList) {
            Branch branch = entry.getValue();
            branchComboBox.getItems().add(branch.getBank().getName());
        }
    }

    @FXML
    private void signUpUser(ActionEvent event) {
        List<TextInputControl> fields = List.of(
            firstNameField,
            lastNameField,
            sinField,
            phoneNumberField,
            emailField,
            passwordField,
            confirmPasswordField
        );
        boolean anyEmpty = fields.stream().anyMatch(f -> f.getText().isEmpty());

        if (
            anyEmpty ||
            branchComboBox.getSelectionModel().isEmpty() ||
            dobField.getValue() == null
        ) {
            errorText.setText("One or more fields are empty");
        } else if (
            !passwordField.getText().equals(confirmPasswordField.getText())
        ) {
            errorText.setText("Password is not the same");
        } else {
            try {
                TypeValidator.validateSocialInsuranceNumber(
                    "SIN",
                    sinField.getText()
                );
                TypeValidator.validatePhone(
                    "Phone Number",
                    phoneNumberField.getText()
                );
                TypeValidator.validateEmail("Email", emailField.getText());

                OperationNewCustomer onc = new OperationNewCustomer(
                    firstNameField.getText(),
                    lastNameField.getText(),
                    dobField.getValue(),
                    sinField.getText(),
                    phoneNumberField.getText(),
                    emailField.getText(),
                    branchList
                        .get(
                            branchComboBox
                                .getSelectionModel()
                                .getSelectedIndex()
                        )
                        .getValue(),
                    firstNameField.getText() + " Chequing",
                    passwordField.getText()
                );
                sceneManager.getDb().addOperation(onc);
                sceneManager.getDb().processOperations();

                Alert a = new Alert(AlertType.INFORMATION);
                a.setContentText("Account created successfully");
                a.showAndWait();
                sceneManager.switchScene(
                    "/fxml/LogInPage.fxml",
                    signUpConfirmButton
                );
            } catch (IllegalArgumentException e) {
                errorText.setText(e.getMessage());
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
