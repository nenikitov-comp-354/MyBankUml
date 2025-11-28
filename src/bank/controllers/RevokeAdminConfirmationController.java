package bank.controllers;

import bank.db.BankDb;
import bank.db.Customer;
import bank.db.operation.OperationMakeAdmin;
import bank.util.SceneManager;
import java.sql.Connection;
import java.sql.SQLException;
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

    private CustomerCardController parentController;
    private final SceneManager sceneManager = SceneManager.getInstance();
    private Customer selectedCustomer;

    public void initialize() {
        selectedCustomer = sceneManager.getSelectedCustomer();
        setConfirmRevokeAdminTxt();
    }

    public void setConfirmRevokeAdminTxt() {
        confirmRevokeAdminTxt.setText(
            "Are you sure you want to revoke " +
            selectedCustomer.getFirstName() +
            "'s admin priviledge?"
        );
    }

    public void setParentController(CustomerCardController controller) {
        this.parentController = controller;
    }

    @FXML
    private void handleCancelRevokeAdmin(ActionEvent event) {
        cancelRevokeAdminButton.getScene().getWindow().hide();
    }

    @FXML
    private void handleConfirmRevokeAdmin(ActionEvent event) {
        if (selectedCustomer != null) {
            selectedCustomer.setAdmin(false);
            sceneManager.setSelectedCustomer(selectedCustomer);
            OperationMakeAdmin oma;

            if (selectedCustomer.isAdmin()) {
                oma = new OperationMakeAdmin(selectedCustomer, true);
            } else {
                oma = new OperationMakeAdmin(selectedCustomer, false);
            }

            if (parentController != null) {
                parentController.updateAdminButtons(false);
            }

            try {
                sceneManager.getDb().addOperation(oma);
                sceneManager.getDb().processOperations();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        confirmRevokeAdminButton.getScene().getWindow().hide();
    }
}
