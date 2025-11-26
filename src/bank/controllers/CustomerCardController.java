package bank.controllers;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class CustomerCardController {

    @FXML
    private Text customerNameText;

    @FXML
    private Text customerIdText;

    public void setCustomerName(String name) {
        customerNameText.setText(name);
    }
    public void setCustomerId(int id) {
        customerIdText.setText("ID: " + id);
    }
}
