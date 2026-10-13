package com.black.bmitracker.ui;

import com.black.bmitracker.application.service.HealthTrackerService;
import com.black.bmitracker.domain.model.entity.UserProfile;
import com.black.bmitracker.ui.view.MainDashboardView;
import com.black.bmitracker.ui.view.ProfileSelectionView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;

public class BmiTrackerApp extends Application {

    private static final String APP_TITLE = "BMI Tracker";
    private static final String ICON_RESOURCE_PATH = "/icons/icon.png";
    private static final double DEFAULT_WIDTH = 1920.0;
    private static final double DEFAULT_HEIGHT = 1080.0;

    private Stage primaryStage;
    private HealthTrackerService healthTrackerService;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.healthTrackerService = new HealthTrackerService();

        loadApplicationIcon(stage);

        stage.setTitle(APP_TITLE);
        stage.setMinWidth(1080);
        stage.setMinHeight(700);
        stage.setWidth(DEFAULT_WIDTH);
        stage.setHeight(DEFAULT_HEIGHT);

        showProfileSelection();
        stage.show();
    }

    private void loadApplicationIcon(Stage stage) {
        try (InputStream iconStream = getClass().getResourceAsStream(ICON_RESOURCE_PATH)) {
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            }
        } catch (Exception ignored) {
            // Gracefully ignore missing icon resource
        }
    }

    public void showProfileSelection() {
        ProfileSelectionView view = new ProfileSelectionView(
                healthTrackerService,
                this::showDashboard
        );
        Scene scene = new Scene(view, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(scene);
    }

    public void showDashboard(UserProfile profile) {
        MainDashboardView dashboardView = new MainDashboardView(
                healthTrackerService,
                profile,
                this::showProfileSelection
        );
        Scene scene = new Scene(dashboardView.getView(), primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
