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

public class MakeAdminConfirmationController {
    @FXML
    private Text confirmMakeAdminTxt;

    @FXML
    private Button cancelMakeAdminButton;

    @FXML
    private Button confirmMakeAdminButton;

    private CustomerCardController parentController;
    private final SceneManager sceneManager = SceneManager.getInstance();
    private Customer selectedCustomer;

    public void initialize() {
        selectedCustomer = sceneManager.getSelectedCustomer();
        setConfirmMakeAdminTxt();
    }

    public void setConfirmMakeAdminTxt() {
        confirmMakeAdminTxt.setText(
            "Are you sure you want to make " +
            selectedCustomer.getFirstName() +
            " an admin?"
        );
    }

    public void setParentController(CustomerCardController controller) {
        this.parentController = controller;
    }

    @FXML
    private void handleCancelMakeAdmin(ActionEvent event) {
        cancelMakeAdminButton.getScene().getWindow().hide();
    }

    @FXML
    private void handleConfirmMakeAdmin(ActionEvent event) {
        if (selectedCustomer != null) {
            selectedCustomer.setAdmin(true);
            sceneManager.setSelectedCustomer(selectedCustomer);
            OperationMakeAdmin oma;

            if (selectedCustomer.isAdmin()) {
                oma = new OperationMakeAdmin(selectedCustomer, true);
            } else {
                oma = new OperationMakeAdmin(selectedCustomer, false);
            }

            if (parentController != null) {
                parentController.updateAdminButtons(true);
            }

            try {
                sceneManager.getDb().addOperation(oma);
                sceneManager.getDb().processOperations();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        confirmMakeAdminButton.getScene().getWindow().hide();
    }
}
