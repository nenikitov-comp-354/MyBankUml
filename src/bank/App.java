package bank;

import bank.db.*;
import bank.util.SceneManager;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

// HACK: The only way to make Maven correctly package a JavaFX application
// is to have a separate class for application and call it from the main.
// [Solution](https://stackoverflow.com/a/70809214)

public class App extends Application {

    public static void main(String[] args) {
        App.launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        BankDb db = new BankDb(
            "localhost",
            Optional.empty(),
            "bank",
            "admin",
            Optional.of("admin")
        );
        db.connect();
        SceneManager.getInstance().setDb(db);

        Parent root = FXMLLoader.load(
            getClass().getResource("/fxml/LogInPage.fxml")
        );
        Scene scene = new Scene(root);
        stage.getIcons().add(new Image("file:icons/bank.png"));
        stage.setScene(scene);
        stage.show();
    }
}
