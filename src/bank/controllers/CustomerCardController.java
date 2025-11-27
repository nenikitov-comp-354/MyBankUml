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
    private Button revokeAdminButton;

    @FXML
    private Text isAdminText;

    private Customer customer;

    private final SceneManager sceneManager = SceneManager.getInstance();

    // initalizing
    @FXML
    public void initialize() {
        updateAdminButtons(false);
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        populateCustomerInfo();
        updateAdminButtons(customer.isAdmin());
        updateFieldVisibility();
    }

    private void populateCustomerInfo() {
        customerNameText.setText(
            customer.getFirstName() + " " + customer.getLastName()
        );
        customerIdText.setText("ID: " + customer.getId());
        customerDOBText.setText("DoB: " + customer.getDateOfBirth());
        customerEmailText.setText("Email: " + customer.getEmail());
        customerPhoneText.setText("Phone: " + customer.getPhone());
        setIsAdmin(customer.isAdmin());
    }

    public void updateFieldVisibility() {
        // Only show detailed fields if the logged-in user is an admin
        boolean currentUserIsAdmin = sceneManager.getCustomer().isAdmin();

        customerDOBText.setVisible(currentUserIsAdmin);
        customerDOBText.setManaged(currentUserIsAdmin);

        customerEmailText.setVisible(currentUserIsAdmin);
        customerEmailText.setManaged(currentUserIsAdmin);

        customerPhoneText.setVisible(currentUserIsAdmin);
        customerPhoneText.setManaged(currentUserIsAdmin);

        customerIdText.setVisible(currentUserIsAdmin);
        customerIdText.setManaged(currentUserIsAdmin);

        // hide the admin buttons if not an admin
        makeAdminButton.setVisible(currentUserIsAdmin);
        makeAdminButton.setManaged(currentUserIsAdmin);

        revokeAdminButton.setVisible(currentUserIsAdmin && customer.isAdmin());
        revokeAdminButton.setManaged(currentUserIsAdmin && customer.isAdmin());
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

    public void setIsAdmin(boolean isAdmin) {
        isAdminText.setText(isAdmin ? "Admin" : "");
    }

    public void updateAdminButtons(boolean isAdmin) {
        revokeAdminButton.setVisible(isAdmin);
        revokeAdminButton.setManaged(isAdmin);

        makeAdminButton.setVisible(!isAdmin);
        makeAdminButton.setManaged(!isAdmin);

        setIsAdmin(isAdmin);

        // only show certain fields for admins
        customerEmailText.setVisible(isAdmin);
        customerEmailText.setManaged(isAdmin);

        customerPhoneText.setVisible(isAdmin);
        customerPhoneText.setManaged(isAdmin);

        customerDOBText.setVisible(isAdmin);
        customerDOBText.setManaged(isAdmin);

        customerIdText.setVisible(isAdmin);
        customerIdText.setManaged(isAdmin);
    }

    @FXML
    private void handleMakeAdmin() {
        if (customer == null) return;

        customer.setAdmin(true);
        sceneManager.setCustomer(customer);
        updateAdminButtons(true);

        System.out.println(customer.getFirstName() + " is now an admin.");

        handleMakeAdminPopup(new ActionEvent());
    }

    @FXML
    public void handleRevokeAdmin() {
        if (customer == null) return;

        customer.setAdmin(false);
        sceneManager.setCustomer(customer); // Write to DB
        updateAdminButtons(false);

        System.out.println(customer.getFirstName() + " is no longer an admin.");

        handleRevokePopup(new ActionEvent());
    }

    @FXML
    public void handleMakeAdminPopup(ActionEvent event) {
        sceneManager.switchScene(
            "/fxml/MakeAdminConfirmation.fxml",
            makeAdminButton
        );
    }

    @FXML
    public void handleRevokePopup(ActionEvent event) {
        sceneManager.switchScene(
            "/fxml/RevokeAdminConfirmation.fxml",
            revokeAdminButton
        );
    }
}
