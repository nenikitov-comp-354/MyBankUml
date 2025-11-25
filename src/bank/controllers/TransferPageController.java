package bank.controllers;

import bank.db.Account;
import bank.db.Customer;
import bank.util.SceneManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

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
        try {
            amount = new BigDecimal(amountTextField.getText());
        } catch (NumberFormatException e) {
            errorText.setText("Error: " + e.getMessage());
            return;
        }
        BigDecimal customerBalance = accounts
            .get(fromAccountComboBox.getSelectionModel().getSelectedIndex())
            .getBalance();

        if (amount.compareTo(customerBalance) > 0) {
            errorText.setText(
                "Error: Amount is greater than what is available in the account"
            );
            return;
        } else {
            sceneManager.switchScene(
                "/fxml/TransferDetailsPage.fxml",
                proceedButton
            );
        }
    }
}
