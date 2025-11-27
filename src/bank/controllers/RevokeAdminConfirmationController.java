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
      if (parentController != null) {
        parentController.updateFieldVisibility();
      }
      // sceneManager.switchScene(
      //       "fxml/AdminSearch.fxml",
      //       cancelRevokeAdminButton
      //   );
      cancelRevokeAdminButton.getScene().getWindow().hide();
    }

    @FXML
    private void handleConfirmRevokeAdmin(ActionEvent event) {
      if (selectedCustomer != null) {
        selectedCustomer.setAdmin(false);
        sceneManager.setSelectedCustomer(selectedCustomer); 
      
        if (parentController != null) {
          parentController.updateAdminButtons(false);
        }
      }
      
      // sceneManager.switchScene(
      //       "/fxml/AdminSearch.fxml",
      //       confirmMakeAdminButton
      //   );
      confirmRevokeAdminButton.getScene().getWindow().hide();
    }
}
