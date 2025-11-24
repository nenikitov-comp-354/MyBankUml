package bank.controllers;

import bank.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class TransactionsController {

    @FXML
    private Button savingsButton;
    @FXML
    private Button contactsButton;
    @FXML
    private Button cancelButton;

    private SceneManager sceneManager = SceneManager.getInstance();

    @FXML
    private void handleSavings(ActionEvent event) {
        sceneManager.switchScene("/fxml/SendMoneySavingsPage.fxml", savingsButton);
    }
    @FXML
    private void handleContacts(ActionEvent event) {
        sceneManager.switchScene("/fxml/SendMoneyContacts.fxml", contactsButton);
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        sceneManager.switchScene("/fxml/UserPage.fxml", cancelButton);
    }
}