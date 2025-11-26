package bank.controllers;

import bank.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class CustomerCardController {
    @FXML
    private Text customerNameText;

    @FXML
    private Text customerIdText;

    @FXML
    private Text customerDOBText;

    @FXML
    private Text customerEmailText;

    @FXML
    private Text customerPhoneText;

    @FXML
    private Button makeAdminButton;

    @FXML
    private Button cancelMakeAdminButton;

    @FXML
    private Text isAdminText;

    private SceneManager sceneManager = SceneManager.getInstance();

    public void setCustomerName(String name) {
        customerNameText.setText(name);
    }

    public void setCustomerId(int id) {
        customerIdText.setText("ID: " + id);
    }

    public void setCustomerDOB(String dob) {
        customerDOBText.setText("DoB: " + dob);
    }

    public void setCustomerEmail(String email) {
        customerEmailText.setText("Email: " + email);
    }

    public void setCustomerPhone(String phone) {
        customerPhoneText.setText("Phone: " + phone);
    }
    @FXML
    private void loadAdminConfirmation(ActionEvent event) {
        sceneManager.switchScene(
            "/fxml/MakeAdminConfirmation.fxml",
            makeAdminButton
        );
    }
    @FXML
    private void handleCancelMakeAdmin(ActionEvent event) {
        sceneManager.switchScene(
            "/fxml/AdminSearch.fxml",
            cancelMakeAdminButton
        );
    }
    public void setIsAdmin(boolean isAdmin) {
    if (isAdmin) {
        isAdminText.setText("Admin");
    } else {
        isAdminText.setText("Not Admin");
    }
}
}
