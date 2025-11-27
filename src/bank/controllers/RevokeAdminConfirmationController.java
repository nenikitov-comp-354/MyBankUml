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

    private final SceneManager sceneManager = SceneManager.getInstance();
    private Customer customer;

    public void initialize() {
        customer = sceneManager.getCustomer();
        setConfirmRevokeAdminTxt();
    }

    public void setConfirmRevokeAdminTxt() {
        confirmRevokeAdminTxt.setText(
            "Are you sure you want to revoke " +
            customer.getFirstName() +
            "'s' admin priviledge?"
        );
    }

    @FXML
    private void handleCancelRevokeAdmin(ActionEvent event) {
        sceneManager.switchScene(
            "/fxml/AdminSearch.fxml",
            cancelRevokeAdminButton
        );
    }
}
