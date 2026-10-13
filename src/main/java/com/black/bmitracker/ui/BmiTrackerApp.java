package com.black.bmitracker.ui;

import com.black.bmitracker.domain.model.UserProfile;
import com.black.bmitracker.domain.service.HealthTrackerService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BmiTrackerApp extends Application {

    private Stage primaryStage;
    private HealthTrackerService healthTrackerService;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.healthTrackerService = new HealthTrackerService();

        stage.setTitle("BMI Health Tracker Pro");
        stage.setMinWidth(1080);
        stage.setMinHeight(700);
        stage.setWidth(1920);
        stage.setHeight(800);

        showProfileSelection();
        stage.show();
    }

    private void showProfileSelection() {
        ProfileSelectionView profileView = new ProfileSelectionView(healthTrackerService, this::showDashboard);
        Scene scene = new Scene(profileView, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(scene);
    }

    private void showDashboard(UserProfile profile) {
        MainDashboardView dashboardView = new MainDashboardView(healthTrackerService, profile, this::showProfileSelection);
        Scene scene = new Scene(dashboardView.getView(), primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(scene);
    }

    static void main(String[] args) {
        launch(args);
    }
}
