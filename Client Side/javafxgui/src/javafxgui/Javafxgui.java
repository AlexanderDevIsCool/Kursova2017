package javafxgui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Javafxgui {

    public Javafxgui(FXMLLoader loader) throws Exception {
        Parent root = loader.load();
        Stage stage = new Stage();
        Scene scene = new Scene(root, 1055, 443);
        stage.setScene(scene);
        stage.setTitle("CharChat");
        stage.show();
    }
}
