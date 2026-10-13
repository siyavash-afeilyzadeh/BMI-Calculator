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
        showProfileSelection();

        stage.setTitle("BMI Health Tracker Pro");
        stage.setWidth(1080);
        stage.setHeight(720);
        stage.show();
    }

    private void showProfileSelection() {
        ProfileSelectionView profileView = new ProfileSelectionView(healthTrackerService, this::showDashboard);
        Scene scene = new Scene(profileView, 1080, 720);
        primaryStage.setScene(scene);
    }

    private void showDashboard(UserProfile profile) {
        MainDashboardView dashboardView = new MainDashboardView(healthTrackerService, profile, this::showProfileSelection);
        Scene scene = new Scene(dashboardView.getView(), 1080, 720);
        primaryStage.setScene(scene);
    }

    static void main(String[] args) {
        launch(args);
    }
}
