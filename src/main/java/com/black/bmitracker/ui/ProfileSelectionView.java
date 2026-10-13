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
        setSpacing(25);
        setPadding(new Insets(40));
        setStyle("-fx-background-color: #0b1120;");

        Label titleLabel = new Label("BMI TRACKER PRO");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#818cf8"));

        Label subtitleLabel = new Label("Select an existing profile or register a new one");
        subtitleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 15));
        subtitleLabel.setTextFill(Color.web("#cbd5e1"));

        VBox card = new VBox(16);
        card.setMaxWidth(420);
        card.setPadding(new Insets(30));
        card.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 14; -fx-border-color: #475569; -fx-border-width: 1.5;");

        // Existing Profile section
        Label existingLabel = new Label("Existing User Profiles:");
        existingLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        existingLabel.setTextFill(Color.web("#ffffff"));

        ComboBox<UserProfile> profileCombo = new ComboBox<>();
        profileCombo.setMaxWidth(Double.MAX_VALUE);
        profileCombo.setStyle(
                "-fx-background-color: #334155; " +
                        "-fx-mark-color: #ffffff; " +
                        "-fx-border-color: #64748b; " +
                        "-fx-border-radius: 6; " +
                        "-fx-background-radius: 6; " +
                        "-fx-padding: 6;"
        );

        List<UserProfile> profiles = healthTrackerService.getAllProfiles();
        profileCombo.setItems(FXCollections.observableArrayList(profiles));

        profileCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(UserProfile item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getUsername() + " (" + item.getGender() + ")");
                    setTextFill(Color.WHITE);
                    setStyle("-fx-background-color: #1e293b; -fx-font-size: 13px; -fx-font-weight: bold;");
                }
            }
        });

        profileCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(UserProfile item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Select existing user...");
                } else {
                    setText(item.getUsername() + " (" + item.getGender() + ")");
                }
                setTextFill(Color.WHITE);
                setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
            }
        });

        if (!profiles.isEmpty()) {
            profileCombo.getSelectionModel().selectFirst();
        }

        Separator separator = new Separator();
        separator.setPadding(new Insets(8, 0, 8, 0));

        // Create New Profile Section
        Label newLabel = new Label("Or Create New Profile:");
        newLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        newLabel.setTextFill(Color.web("#ffffff"));

        Label userPromptLabel = new Label("Username:");
        userPromptLabel.setTextFill(Color.web("#e2e8f0"));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter new username...");
        usernameField.setStyle("-fx-background-color: #0f172a; -fx-text-fill: #ffffff; -fx-border-color: #64748b; -fx-border-radius: 6; -fx-padding: 8;");

        Label genderPromptLabel = new Label("Gender:");
        genderPromptLabel.setTextFill(Color.web("#e2e8f0"));

        ComboBox<Gender> genderCombo = new ComboBox<>(FXCollections.observableArrayList(Gender.values()));
        genderCombo.getSelectionModel().select(Gender.MALE);
        genderCombo.setMaxWidth(Double.MAX_VALUE);
        genderCombo.setStyle(
                "-fx-background-color: #334155; " +
                        "-fx-mark-color: #ffffff; " +
                        "-fx-border-color: #64748b; " +
                        "-fx-border-radius: 6; " +
                        "-fx-background-radius: 6; " +
                        "-fx-padding: 6;"
        );

        genderCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Gender item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.name());
                    setTextFill(Color.WHITE);
                    setStyle("-fx-background-color: #1e293b; -fx-font-size: 13px; -fx-font-weight: bold;");
                }
            }
        });

        genderCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Gender item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.name());
                setTextFill(Color.WHITE);
                setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
            }
        });

        Button continueBtn = new Button("Launch BMI Tracker");
        continueBtn.setMaxWidth(Double.MAX_VALUE);
        continueBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        continueBtn.setStyle("-fx-background-color: #6366f1; -fx-text-fill: #ffffff; -fx-background-radius: 8; -fx-padding: 12; -fx-cursor: hand;");

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

        card.getChildren().addAll(
                existingLabel, profileCombo,
                separator,
                newLabel,
                userPromptLabel, usernameField,
                genderPromptLabel, genderCombo,
                continueBtn
        );

        getChildren().addAll(titleLabel, subtitleLabel, card);
    }
}
