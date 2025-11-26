package bank.controllers;

import bank.db.Customer;
import bank.util.SceneManager;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class AdminSearchController {
    @FXML
    private VBox customersHolderVBox;

    @FXML
    private Button cancelSearchButton;

    @FXML
    private javafx.scene.control.TextField searchInput;

    private SceneManager sceneManager = SceneManager.getInstance();

    public void initialize() {
        Map<Integer, Customer> customersMap = sceneManager
            .getDb()
            .getCustomers();
        loadCustomers(customersMap.values().stream().toList());
    }

    private void loadCustomers(List<Customer> customers) {
        customersHolderVBox.getChildren().clear();
        for (Customer c : customers) {
            try {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/CustomerCard.fxml")
                );
                Parent card = loader.load();
                CustomerCardController cc = loader.getController();
                cc.setCustomerName(c.getFirstName() + " " + c.getLastName());
                cc.setCustomerId(c.getId());
                cc.setCustomerDOB(c.getDateOfBirth().toString());
                cc.setCustomerEmail(c.getEmail());
                cc.setCustomerPhone(c.getPhone());
                customersHolderVBox.getChildren().add(card);
                VBox.setMargin(card, new javafx.geometry.Insets(15));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchInput.getText().trim().toLowerCase();
        try {
            List<Customer> results = sceneManager
                .getDb()
                .getCustomersSearch(new String[] { query });
            loadCustomers(results);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadUserPage(ActionEvent event) {
        sceneManager.switchScene("/fxml/UserPage.fxml", cancelSearchButton);
    }
}
