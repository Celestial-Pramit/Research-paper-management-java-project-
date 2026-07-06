package com.researchpapers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static Stage stage;
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
        stage.setTitle("Research Paper Management System");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(620);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void setActiveNav(Button active, Button... navButtons) {
        for (Button btn : navButtons) {
            btn.getStyleClass().remove("nav-active");
        }
        active.getStyleClass().add("nav-active");
    }

    public static void changeScene(String fxml) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/" + fxml + ".fxml"));
            Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("failed to change scene");
            e.printStackTrace();
        }
    }
}
