package bank.controllers;

import javafx.fxml.FXML;
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
}
