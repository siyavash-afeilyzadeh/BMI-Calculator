package com.black.bmitracker.ui;

import com.black.bmitracker.domain.model.UserProfile;
import com.black.bmitracker.domain.model.enums.Gender;
import com.black.bmitracker.domain.service.HealthTrackerService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public class ProfileSelectionView extends VBox {

    private final HealthTrackerService healthTrackerService;
    private final Consumer<UserProfile> onProfileSelected;

    public ProfileSelectionView(HealthTrackerService healthTrackerService, Consumer<UserProfile> onProfileSelected) {
        this.healthTrackerService = healthTrackerService;
        this.onProfileSelected = onProfileSelected;

        initView();
    }

    private void initView() {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(40));
        setStyle("-fx-background-color: #0f172a;");

        Label titleLabel = new Label("BMI TRACKER PRO");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#818cf8"));

        Label subtitleLabel = new Label("Select or Create a User Profile");
        subtitleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        subtitleLabel.setTextFill(Color.web("#94a3b8"));

        VBox card = new VBox(15);
        card.setMaxWidth(380);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 12; -fx-border-color: #334155; -fx-border-radius: 12;");

        Label existingLabel = new Label("Existing Profile:");
        existingLabel.setTextFill(Color.web("#cbd5e1"));

        ComboBox<UserProfile> profileCombo = new ComboBox<>();
        profileCombo.setMaxWidth(Double.MAX_VALUE);
        profileCombo.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6;");

        List<UserProfile> profiles = healthTrackerService.getAllProfiles();
        profileCombo.setItems(FXCollections.observableArrayList(profiles));
        profileCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(UserProfile item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getUsername());
            }
        });
        profileCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(UserProfile item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Select existing user..." : item.getUsername());
            }
        });

        if (!profiles.isEmpty()) {
            profileCombo.getSelectionModel().selectFirst();
        }

        Separator separator = new Separator();

        Label newLabel = new Label("Or Create New Profile:");
        newLabel.setTextFill(Color.web("#cbd5e1"));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter new username...");
        usernameField.setStyle("-fx-background-color: #0f172a; -fx-text-fill: white; -fx-border-color: #475569; -fx-border-radius: 6; -fx-padding: 8;");

        ComboBox<Gender> genderCombo = new ComboBox<>(FXCollections.observableArrayList(Gender.values()));
        genderCombo.getSelectionModel().select(Gender.MALE);
        genderCombo.setMaxWidth(Double.MAX_VALUE);
        genderCombo.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-padding: 6;");

        Button continueBtn = new Button("Continue to Dashboard");
        continueBtn.setMaxWidth(Double.MAX_VALUE);
        continueBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        continueBtn.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10; -fx-cursor: hand;");

        continueBtn.setOnAction(e -> {
            String newUsername = usernameField.getText().trim();
            if (!newUsername.isEmpty()) {
                UserProfile created = healthTrackerService.getOrCreateProfileByUsername(newUsername, genderCombo.getValue(), LocalDate.now().minusYears(25));
                onProfileSelected.accept(created);
            } else if (profileCombo.getValue() != null) {
                onProfileSelected.accept(profileCombo.getValue());
            } else {
                UserProfile fallback = healthTrackerService.getOrCreateProfileByUsername("mr_black", Gender.MALE, LocalDate.now().minusYears(25));
                onProfileSelected.accept(fallback);
            }
        });

        card.getChildren().addAll(existingLabel, profileCombo, separator, newLabel, usernameField, genderCombo, continueBtn);
        getChildren().addAll(titleLabel, subtitleLabel, card);
    }
}
