package com.black.bmitracker.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class BmiTrackerApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        MainDashboardView dashboardView = new MainDashboardView();
        Scene scene = new Scene(dashboardView.getView(), 1050, 720);

        URL cssResource = getClass().getResource("/styles/app.css");
        if (cssResource != null) {
            scene.getStylesheets().add(cssResource.toExternalForm());
        }

        primaryStage.setTitle("BMI Health Tracker Pro");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(650);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
