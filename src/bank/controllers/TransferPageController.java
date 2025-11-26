package bank.controllers;

import bank.db.Account;
import bank.db.Customer;
import bank.db.TransactionInfo;
import bank.util.SceneManager;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TransferPageController {
    @FXML
    private ComboBox<String> fromAccountComboBox;

    @FXML
    private ComboBox<String> toAccountComboBox;

    @FXML
    private Text amountAvailableText;

    @FXML
    private Text errorText;

    @FXML
    private TextField amountTextField;

    @FXML
    private Button proceedButton;

    private List<Account> accounts;
    private SceneManager sceneManager = SceneManager.getInstance();

    public void initialize() {
        Customer c = sceneManager.getCustomer();
        accounts =
            c
                .getAccounts()
                .stream()
                .filter(a -> !a.isLocked())
                .collect(Collectors.toList());

        fromAccountComboBox.setOnAction(
            e -> {
                int i = fromAccountComboBox
                    .getSelectionModel()
                    .getSelectedIndex();
                amountAvailableText.setText(
                    "Amount Available: " + accounts.get(i).getBalance()
                );
            }
        );

        for (Account a : accounts) {
            fromAccountComboBox.getItems().add(a.getName());
            toAccountComboBox.getItems().add(a.getName());
        }
    }

    @FXML
    private void proceedToTransaction(ActionEvent event) {
        BigDecimal amount;

        int fromIndex = fromAccountComboBox
            .getSelectionModel()
            .getSelectedIndex();
        int toIndex = toAccountComboBox.getSelectionModel().getSelectedIndex();
        try {
            amount = new BigDecimal(amountTextField.getText());
        } catch (NumberFormatException e) {
            errorText.setText("Error: " + e.getMessage());
            return;
        }
        BigDecimal customerBalance = accounts.get(fromIndex).getBalance();

        if (
            fromAccountComboBox.getSelectionModel().isEmpty() ||
            toAccountComboBox.getSelectionModel().isEmpty() ||
            amountTextField.getText() == null
        ) {
            errorText.setText("Error: One or more fields are empty");
            return;
        }

        try {
            TransactionInfo t = new TransactionInfo(
                accounts.get(fromIndex),
                accounts.get(toIndex),
                amount,
                LocalDateTime.now()
            );
            if (amount.compareTo(customerBalance) > 0) {
                errorText.setText("Error: Amount is greater than balance");
                return;
            } else {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/TransferDetailsPage.fxml")
                );
                Parent root = loader.load();
                TransferDetailsPageController tdpc = loader.getController();

                tdpc.initializeData(t);
                Stage stage = (Stage) proceedButton.getScene().getWindow();
                stage.setScene(new Scene(root));
            }
        } catch (IllegalArgumentException e) {
            errorText.setText("Error: " + e.getMessage());
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
