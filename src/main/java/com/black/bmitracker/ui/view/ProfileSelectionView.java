package com.black.bmitracker.ui.view;

import com.black.bmitracker.application.service.HealthTrackerService;
import com.black.bmitracker.domain.model.entity.UserProfile;
import com.black.bmitracker.domain.model.enums.Gender;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public class ProfileSelectionView extends BorderPane {

    private final HealthTrackerService healthTrackerService;
    private final Consumer<UserProfile> onProfileSelected;

    private ComboBox<UserProfile> profileComboBox;
    private TextField usernameField;
    private ComboBox<Gender> genderBox;
    private DatePicker birthDatePicker;

    public ProfileSelectionView(HealthTrackerService healthTrackerService,
                                Consumer<UserProfile> onProfileSelected) {
        this.healthTrackerService = healthTrackerService;
        this.onProfileSelected = onProfileSelected;

        initUi();
        loadProfiles();
    }

    private void initUi() {
        setStyle("-fx-background-color: #0b1120;");
        setTop(buildHeader());
        setCenter(buildCenterContent());
    }

    private HBox buildHeader() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 28, 18, 28));
        header.setStyle("-fx-background-color: #0f172a; -fx-border-color: #1e293b; -fx-border-width: 0 0 1 0;");

        Label brand = new Label("BMI TRACKER");
        brand.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        brand.setTextFill(Color.web("#818cf8"));

        Label subtitle = new Label("• Profile Management");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        subtitle.setTextFill(Color.web("#94a3b8"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button exitBtn = new Button("✕");
        exitBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        exitBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 6 12;");
        exitBtn.setTooltip(new Tooltip("Exit Application"));
        exitBtn.setOnAction(e -> {
            Platform.exit();
            System.exit(0);
        });

        header.getChildren().addAll(brand, subtitle, spacer, exitBtn);
        return header;
    }

    private StackPane buildCenterContent() {
        StackPane centerWrapper = new StackPane();
        centerWrapper.setAlignment(Pos.CENTER);
        centerWrapper.setPadding(new Insets(20));

        // کادر مرکزی دقیقاً هم‌اندازه محتوا و بدون کشیدگی عمودی
        VBox mainCard = new VBox(20);
        mainCard.setMaxWidth(460);
        mainCard.setMaxHeight(Region.USE_PREF_SIZE); // مانع کشیده شدن از سقف تا کف
        mainCard.setPadding(new Insets(26));
        mainCard.setStyle("-fx-background-color: #0f172a; -fx-background-radius: 12; -fx-border-color: #1e293b; -fx-border-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 20, 0, 0, 6);");

        // بخش ۱: کاربران قبلی
        VBox existingSection = buildExistingSection();

        // خط جداکننده
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #334155; -fx-opacity: 0.5;");

        // بخش ۲: ایجاد کاربر جدید
        VBox createSection = buildCreateSection();

        mainCard.getChildren().addAll(existingSection, separator, createSection);
        centerWrapper.getChildren().add(mainCard);
        return centerWrapper;
    }

    private VBox buildExistingSection() {
        VBox box = new VBox(12);

        Label title = new Label("Select Existing Profile");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        title.setTextFill(Color.web("#f8fafc"));

        profileComboBox = new ComboBox<>();
        profileComboBox.setMaxWidth(Double.MAX_VALUE);
        profileComboBox.setPrefHeight(38);
        profileComboBox.setStyle("-fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-radius: 6; -fx-font-size: 13px;");

        // نمایش آیتم‌ها داخل منوی بازشونده (Dropdown popup)
        profileComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(UserProfile user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(user.getUsername() + " (" + user.getGender() + ")");
                    setTextFill(Color.web("#f8fafc"));
                    setStyle("-fx-background-color: #1e293b; -fx-padding: 8;");
                }
            }
        });

        // نمایش متن انتخاب‌شده روی دکمه کومبوباکس در حالت بسته (کاملاً سفید و شفاف)
        profileComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(UserProfile user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText("Choose your profile...");
                    setTextFill(Color.web("#64748b")); // خاکستری ملایم فقط برای placeholder
                } else {
                    setText(user.getUsername() + " (" + user.getGender() + ")");
                    setTextFill(Color.web("#f8fafc")); // سفید درخشان و خوانا
                    setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                }
            }
        });

        HBox actionsRow = new HBox(10);
        actionsRow.setAlignment(Pos.CENTER_LEFT);

        Button selectBtn = new Button("Open Profile");
        selectBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(selectBtn, Priority.ALWAYS);
        selectBtn.setPrefHeight(38);
        selectBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        selectBtn.setStyle("-fx-background-color: #4338ca; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
        selectBtn.setOnAction(e -> handleSelectProfile());

        Button deleteBtn = new Button("Delete");
        deleteBtn.setPrefHeight(38);
        deleteBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 0 16;");
        deleteBtn.setOnAction(e -> handleDeleteProfile());

        actionsRow.getChildren().addAll(selectBtn, deleteBtn);
        box.getChildren().addAll(title, profileComboBox, actionsRow);
        return box;
    }

    private VBox buildCreateSection() {
        VBox box = new VBox(12);

        Label title = new Label("Or Create New Profile");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        title.setTextFill(Color.web("#f8fafc"));

        Label usernameLabel = new Label("Username:");
        usernameLabel.setTextFill(Color.web("#94a3b8"));
        usernameField = new TextField();
        usernameField.setPromptText("Enter profile name");
        styleInput(usernameField);

        HBox rowFields = new HBox(12);

        VBox genderBoxCol = new VBox(6);
        genderBoxCol.setPrefWidth(180);
        Label genderLabel = new Label("Gender:");
        genderLabel.setTextFill(Color.web("#94a3b8"));
        genderBox = new ComboBox<>(FXCollections.observableArrayList(Gender.values()));
        genderBox.setValue(Gender.MALE);
        genderBox.setMaxWidth(Double.MAX_VALUE);
        genderBox.setPrefHeight(36);
        genderBox.setStyle("-fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-radius: 6;");

        // خوانا و سفید کردن متن انتخاب‌شده جنسیت
        genderBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Gender item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.name());
                    setTextFill(Color.web("#f8fafc"));
                    setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                }
            }
        });

        genderBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Gender item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.name());
                    setTextFill(Color.web("#f8fafc"));
                    setStyle("-fx-background-color: #1e293b; -fx-padding: 6;");
                }
            }
        });

        genderBoxCol.getChildren().addAll(genderLabel, genderBox);

        VBox birthBoxCol = new VBox(6);
        HBox.setHgrow(birthBoxCol, Priority.ALWAYS);
        Label birthLabel = new Label("Birth Date:");
        birthLabel.setTextFill(Color.web("#94a3b8"));
        birthDatePicker = new DatePicker(LocalDate.of(1995, 1, 1));
        birthDatePicker.setMaxWidth(Double.MAX_VALUE);
        birthDatePicker.setPrefHeight(36);
        birthDatePicker.setStyle("-fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-radius: 6;");
        birthBoxCol.getChildren().addAll(birthLabel, birthDatePicker);

        rowFields.getChildren().addAll(genderBoxCol, birthBoxCol);

        Button createBtn = new Button("Create & Continue");
        createBtn.setMaxWidth(Double.MAX_VALUE);
        createBtn.setPrefHeight(38);
        createBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        createBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
        createBtn.setOnAction(e -> handleCreateProfile());

        box.getChildren().addAll(title, usernameLabel, usernameField, rowFields, createBtn);
        return box;
    }

    private void styleInput(TextField field) {
        field.setPrefHeight(36);
        field.setStyle("-fx-background-color: #1e293b; -fx-text-fill: #f8fafc; -fx-prompt-text-fill: #64748b; -fx-border-color: #334155; -fx-border-radius: 6; -fx-padding: 8;");
    }

    private void handleSelectProfile() {
        UserProfile selected = profileComboBox.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a profile first.", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        onProfileSelected.accept(selected);
    }

    private void handleDeleteProfile() {
        UserProfile selected = profileComboBox.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a profile to delete.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete profile '" + selected.getUsername() + "'?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                healthTrackerService.deleteProfile(selected.getId());
                loadProfiles();
            }
        });
    }

    private void handleCreateProfile() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Username cannot be empty.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        Gender gender = genderBox.getValue();
        LocalDate birthDate = birthDatePicker.getValue();

        try {
            UserProfile profile = healthTrackerService.getOrCreateProfileByUsername(username, gender, birthDate);
            onProfileSelected.accept(profile);
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to create profile: " + ex.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void loadProfiles() {
        List<UserProfile> profiles = healthTrackerService.getAllProfiles();
        profileComboBox.setItems(FXCollections.observableArrayList(profiles));
        if (!profiles.isEmpty()) {
            profileComboBox.getSelectionModel().selectFirst();
        }
    }
}
