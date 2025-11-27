package bank.controllers;

import bank.db.Customer;
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

    private Customer customer;

    private SceneManager sceneManager = SceneManager.getInstance();

    public void setCustomer(Customer customer) {
        this.customer = customer;
        setCustomerName(customer.getFirstName() + " " + customer.getLastName());
        setCustomerId(customer.getId());
        setCustomerDOB(customer.getDateOfBirth().toString());
        setCustomerEmail(customer.getEmail());
        setCustomerPhone(customer.getPhone());
        updateAdminStatus();
    }

    public void updateAdminStatus() {
        if (customer.isAdmin()) {
            isAdminText.setText("Admin");
            isAdminText.setVisible(true);
            makeAdminButton.setDisable(true); // already admin
        } else {
            isAdminText.setText("");
            isAdminText.setVisible(false);
            makeAdminButton.setDisable(false);
        }
    }

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
    public void handleAdmin(ActionEvent event) {
        if (customer != null && !customer.isAdmin()) {
            customer.setAdmin(true);
            updateAdminStatus();

            System.out.println(
                "Customer " +
                customer.getFirstName() +
                " " +
                customer.getLastName() +
                " is now an admin."
            );
        }

        // load confirmation scene
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
