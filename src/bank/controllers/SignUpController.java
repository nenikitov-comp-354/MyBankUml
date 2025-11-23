package bank.controllers;

import bank.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SignUpController {

    @FXML private Button signUpConfirmButton;

    private SceneManager sceneManager = SceneManager.getInstance();

    @FXML
    private void signUpUser(ActionEvent event) {
        // TODO: Add logic that will add the user to the database

        sceneManager.switchScene("/fxml/LogInPage.fxml", signUpConfirmButton);
    }
}
