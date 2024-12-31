package model;

import controller.MainController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class App extends Application {

    public static Stage primaryStage;
    private MainController controller = new MainController();

    @Override
    public void start(Stage primaryStage) throws Exception {
        App.primaryStage = primaryStage;

        primaryStage.setTitle("Gestión de Prácticas");
        primaryStage.setScene(new Scene(controller.getView()));

        primaryStage.show();
    }

}