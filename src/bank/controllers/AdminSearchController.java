package bank.controllers;

import bank.db.Customer;
import bank.util.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AdminSearchController {

    @FXML
    private VBox customersHolderVBox;

    @FXML
    private javafx.scene.control.TextField searchInput;
    
    private SceneManager sceneManager = SceneManager.getInstance();

    public void initialize() {
        try {
            Map<Integer, Customer> customersMap = sceneManager.getDb().getCustomers();
            List<Customer> customers = customersMap.values().stream().toList();

            for (Customer c : customers) {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/CustomerCard.fxml")
                );
                Parent card = loader.load();
                CustomerCardController cc = loader.getController();
                cc.setCustomerName(c.getFirstName() + " " + c.getLastName());
                cc.setCustomerId(c.getId());
                customersHolderVBox.getChildren().add(card);
                VBox.setMargin(card, new javafx.geometry.Insets(15));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
