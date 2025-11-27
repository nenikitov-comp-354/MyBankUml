package bank.controllers;

import bank.db.Customer;
import bank.util.SceneManager;
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

    private final SceneManager sceneManager = SceneManager.getInstance();
    private Customer customer;

    public void initialize() {
        customer = sceneManager.getPendingCustomer();
        setConfirmMakeAdminTxt();
    }

    public void setConfirmMakeAdminTxt() {
        confirmMakeAdminTxt.setText(
            "Are you sure you want to make " +
            customer.getFirstName() +
            " an admin?"
        );
    }

    @FXML
    private void handleCancelMakeAdmin(ActionEvent event) {
      sceneManager.setPendingCustomer(null); // Clear pending 
      sceneManager.switchScene(
            "/fxml/AdminSearch.fxml",
            cancelMakeAdminButton
        );
    }

    @FXML
    private void handleConfirmMakeAdmin(ActionEvent event) {
      customer.setAdmin(true);
      sceneManager.setPendingCustomer(null); // Clear pending customer after action
      sceneManager.switchScene(
            "/fxml/AdminSearch.fxml",
            confirmMakeAdminButton
        );
    }
}
