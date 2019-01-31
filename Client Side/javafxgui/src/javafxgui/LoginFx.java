package javafxgui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginFx extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root1 = FXMLLoader.load(getClass().getResource("FXMLLogin.fxml"));
        Scene scene1 = new Scene(root1, 490, 400);
        stage.setScene(scene1);
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.setTitle("Log in");
        stage.show();
        stage.setOnCloseRequest(event -> {
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

}
