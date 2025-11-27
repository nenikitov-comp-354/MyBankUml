package bank.controllers;

import bank.db.Customer;
import bank.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class RevokeAdminConfirmationController {
    @FXML
    private Text confirmRevokeAdminTxt;

    @FXML
    private Button cancelRevokeAdminButton;

    @FXML
    private Button confirmRevokeAdminButton;

    private final SceneManager sceneManager = SceneManager.getInstance();
    private Customer customer;

    public void initialize() {
        customer = sceneManager.getPendingCustomer();
        setConfirmRevokeAdminTxt();
    }

    public void setConfirmRevokeAdminTxt() {
        confirmRevokeAdminTxt.setText(
            "Are you sure you want to revoke " +
            customer.getFirstName() +
            "'s admin priviledge?"
        );
    }

    @FXML
    private void handleCancelRevokeAdmin(ActionEvent event) {
      sceneManager.setPendingCustomer(null); // Clear pending  
      sceneManager.switchScene(
            "fxml/AdminSearch.fxml",
            cancelRevokeAdminButton
        );
    }

    @FXML
    private void handleConfirmRevokeAdmin(ActionEvent event) {
      customer.setAdmin(false);
      sceneManager.setPendingCustomer(null); // Clear pending  
      sceneManager.switchScene(
            "fxml/AdminSearch.fxml",
            confirmRevokeAdminButton
        );
    }
}
