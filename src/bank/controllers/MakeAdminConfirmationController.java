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

    private CustomerCardController parentController;;
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

      if (parentController != null) {
          parentController.updateFieldVisibility();
      }
      cancelMakeAdminButton.getScene().getWindow().hide();
      // sceneManager.switchScene(
      //       "/fxml/AdminSearch.fxml",
      //       cancelMakeAdminButton
      //   );
    }

    @FXML
    private void handleConfirmMakeAdmin(ActionEvent event) {
      if (selectedCustomer != null) {
        selectedCustomer.setAdmin(true);
        sceneManager.setSelectedCustomer(selectedCustomer); 
      
        if (parentController != null) {
          parentController.updateAdminButtons(true);
        }
      }
      
      confirmMakeAdminButton.getScene().getWindow().hide();
      // sceneManager.switchScene(
      //       "/fxml/AdminSearch.fxml",
      //       confirmMakeAdminButton
      //   );
    }
}
