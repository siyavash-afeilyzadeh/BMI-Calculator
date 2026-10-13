package com.black.bmitracker.ui;

import com.black.bmitracker.application.service.HealthTrackerService;
import com.black.bmitracker.domain.model.entity.UserProfile;
import com.black.bmitracker.infrastructure.persistence.config.HibernateUtil;
import com.black.bmitracker.ui.view.MainDashboardView;
import com.black.bmitracker.ui.view.ProfileSelectionView;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;

public class BmiTrackerApp extends Application {

    private static final String APP_TITLE = "BMI Tracker Pro";
    private static final double MIN_WIDTH = 1080;
    private static final double MIN_HEIGHT = 700;
    private static final double DEFAULT_WIDTH = 1920;
    private static final double DEFAULT_HEIGHT = 1080;

    private Stage primaryStage;
    private HealthTrackerService healthTrackerService;

    @Override
    public void init() {
        this.healthTrackerService = new HealthTrackerService();
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.primaryStage.setTitle(APP_TITLE);
        this.primaryStage.setMinWidth(MIN_WIDTH);
        this.primaryStage.setMinHeight(MIN_HEIGHT);

        loadApplicationIcon();
        showProfileSelection();
        this.primaryStage.show();
    }

    private void loadApplicationIcon() {
        try (InputStream iconStream = getClass().getResourceAsStream("/icons/icon.png")) {
            if (iconStream != null) {
                primaryStage.getIcons().add(new Image(iconStream));
            }
        } catch (Exception ignored) {
        }
    }

    public void showProfileSelection() {
        ProfileSelectionView view = new ProfileSelectionView(
                healthTrackerService,
                this::showDashboard
        );
        switchScene(view);
    }

    public void showDashboard(UserProfile profile) {
        MainDashboardView dashboardView = new MainDashboardView(
                healthTrackerService,
                profile,
                this::showProfileSelection
        );
        switchScene(dashboardView);
    }

    private void switchScene(Parent root) {
        double width = primaryStage.getWidth() > 0 ? primaryStage.getWidth() : DEFAULT_WIDTH;
        double height = primaryStage.getHeight() > 0 ? primaryStage.getHeight() : DEFAULT_HEIGHT;
        primaryStage.setScene(new Scene(root, width, height));
    }

    @Override
    public void stop() {
        HibernateUtil.shutdown();
    }

    static void main(String[] args) {
        launch(args);
    }
}
