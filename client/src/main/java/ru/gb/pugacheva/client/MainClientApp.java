package ru.gb.pugacheva.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.gb.pugacheva.client.controller.Controller;

public class MainClientApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/mainWindow.fxml"));
        Parent parent = loader.load();
        primaryStage.setScene(new Scene(parent));
        primaryStage.setTitle("Облачное хранилище");
        primaryStage.setResizable(false);

        Controller controller = loader.getController();
        primaryStage.setOnCloseRequest((event) -> controller.shutdown());
        primaryStage.show();
    }

}
