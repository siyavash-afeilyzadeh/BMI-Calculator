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
    private Button loadBtn;
    private Button deleteBtn;
    private TextField usernameField;
    private ComboBox<Gender> genderBox;
    private DatePicker birthDatePicker;

    public ProfileSelectionView(HealthTrackerService healthTrackerService, Consumer<UserProfile> onProfileSelected) {
        this.healthTrackerService = healthTrackerService;
        this.onProfileSelected = onProfileSelected;
        initUi();
        loadProfiles();
    }

    private void initUi() {
        setStyle("-fx-background-color: #0b1120;");
        setTop(buildHeader());

        StackPane centerWrapper = new StackPane();
        centerWrapper.setPadding(new Insets(25));

        VBox mainCard = new VBox(20);
        mainCard.setMaxWidth(460);
        mainCard.setMaxHeight(Region.USE_PREF_SIZE);
        mainCard.setAlignment(Pos.TOP_LEFT);
        mainCard.setPadding(new Insets(28));
        mainCard.setStyle(
                "-fx-background-color: #1e293b;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: #334155;" +
                        "-fx-border-radius: 14;" +
                        "-fx-border-width: 1;"
        );

        VBox existingSection = buildExistingSection();

        Separator separator = new Separator();
        separator.setStyle("-fx-opacity: 0.25;");

        VBox createSection = buildCreateSection();

        mainCard.getChildren().addAll(existingSection, separator, createSection);
        centerWrapper.getChildren().add(mainCard);
        setCenter(centerWrapper);
    }

    private HBox buildHeader() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 28, 16, 28));
        header.setStyle("-fx-background-color: #0f172a; -fx-border-color: #1e293b; -fx-border-width: 0 0 1 0;");

        Label brand = new Label("BMI TRACKER");
        brand.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        brand.setTextFill(Color.web("#818cf8"));

        Label subtitle = new Label("Profile Management");
        subtitle.setFont(Font.font("Segoe UI", 13));
        subtitle.setTextFill(Color.web("#64748b"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button exitBtn = new Button("✕");
        exitBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        exitBtn.setStyle(
                "-fx-background-color: #ef4444;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 6 12;"
        );
        exitBtn.setTooltip(new Tooltip("Exit Application"));
        exitBtn.setOnAction(e -> {
            Platform.exit();
            System.exit(0);
        });

        header.getChildren().addAll(brand, subtitle, spacer, exitBtn);
        return header;
    }

    private VBox buildExistingSection() {
        VBox box = new VBox(12);

        Label sectionTitle = new Label("Select Existing User");
        sectionTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        sectionTitle.setTextFill(Color.web("#f8fafc"));

        profileComboBox = new ComboBox<>();
        profileComboBox.setMaxWidth(Double.MAX_VALUE);
        profileComboBox.setPromptText("Choose your profile...");
        profileComboBox.setStyle(
                "-fx-background-color: #0f172a;" +
                        "-fx-border-color: #334155;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 4;"
        );

        // High-contrast rendering for dropdown items
        profileComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(UserProfile item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: #1e293b;");
                } else {
                    setText(item.getUsername() + "  (" + item.getGender() + ")");
                    setTextFill(Color.web("#f8fafc"));
                    setStyle("-fx-background-color: #1e293b; -fx-padding: 6 10;");
                }
            }
        });

        // High-contrast rendering when ComboBox is closed
        profileComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(UserProfile item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    boolean noUsers = profileComboBox.getItems() == null || profileComboBox.getItems().isEmpty();
                    setText(noUsers ? "No profiles yet — create one below" : "Choose your profile...");
                    setTextFill(Color.web("#64748b"));
                } else {
                    setText(item.getUsername() + "  (" + item.getGender() + ")");
                    setTextFill(Color.web("#f8fafc"));
                }
            }
        });

        HBox actionRow = new HBox(10);
        loadBtn = new Button("Load Selected User");
        loadBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(loadBtn, Priority.ALWAYS);
        loadBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        loadBtn.setStyle(
                "-fx-background-color: #4338ca;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 16;" +
                        "-fx-cursor: hand;"
        );
        loadBtn.setOnAction(e -> handleLoadSelected());

        deleteBtn = new Button("Delete");
        deleteBtn.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        deleteBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #ef4444;" +
                        "-fx-border-color: #ef4444;" +
                        "-fx-border-radius: 8;" +
                        "-fx-padding: 9 14;" +
                        "-fx-cursor: hand;"
        );
        deleteBtn.setOnAction(e -> handleDeleteSelected());

        actionRow.getChildren().addAll(loadBtn, deleteBtn);
        box.getChildren().addAll(sectionTitle, profileComboBox, actionRow);
        return box;
    }

    private VBox buildCreateSection() {
        VBox box = new VBox(12);

        Label sectionTitle = new Label("Create New Profile");
        sectionTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        sectionTitle.setTextFill(Color.web("#f8fafc"));

        Label userLbl = new Label("Username:");
        userLbl.setTextFill(Color.web("#94a3b8"));

        usernameField = new TextField();
        usernameField.setPromptText("Enter username...");
        usernameField.setStyle(
                "-fx-background-color: #0f172a;" +
                        "-fx-text-fill: #f8fafc;" +
                        "-fx-prompt-text-fill: #64748b;" +
                        "-fx-border-color: #334155;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 9;"
        );

        HBox row = new HBox(12);

        VBox genderCol = new VBox(6);
        HBox.setHgrow(genderCol, Priority.ALWAYS);
        Label genderLbl = new Label("Gender:");
        genderLbl.setTextFill(Color.web("#94a3b8"));

        genderBox = new ComboBox<>(FXCollections.observableArrayList(Gender.values()));
        genderBox.setValue(Gender.MALE);
        genderBox.setMaxWidth(Double.MAX_VALUE);
        genderBox.setStyle(
                "-fx-background-color: #0f172a;" +
                        "-fx-border-color: #334155;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 3;"
        );
        genderBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Gender item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: #1e293b;");
                } else {
                    setText(item.name());
                    setTextFill(Color.web("#f8fafc"));
                    setStyle("-fx-background-color: #1e293b;");
                }
            }
        });
        genderBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Gender item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.name());
                    setTextFill(Color.web("#f8fafc"));
                }
            }
        });
        genderCol.getChildren().addAll(genderLbl, genderBox);

        VBox birthCol = new VBox(6);
        HBox.setHgrow(birthCol, Priority.ALWAYS);
        Label birthLbl = new Label("Birth Date:");
        birthLbl.setTextFill(Color.web("#94a3b8"));

        birthDatePicker = new DatePicker(LocalDate.of(1995, 1, 1));
        birthDatePicker.setMaxWidth(Double.MAX_VALUE);
        birthDatePicker.setStyle(
                "-fx-background-color: #0f172a;" +
                        "-fx-control-inner-background: #0f172a;" +
                        "-fx-border-color: #334155;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );
        birthCol.getChildren().addAll(birthLbl, birthDatePicker);

        row.getChildren().addAll(genderCol, birthCol);

        Button createBtn = new Button("Create & Launch");
        createBtn.setMaxWidth(Double.MAX_VALUE);
        createBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        createBtn.setStyle(
                "-fx-background-color: #10b981;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 11 16;" +
                        "-fx-cursor: hand;"
        );
        createBtn.setOnAction(e -> handleCreateNew());

        box.getChildren().addAll(sectionTitle, userLbl, usernameField, row, createBtn);
        return box;
    }

    private void loadProfiles() {
        List<UserProfile> profiles = healthTrackerService.getAllProfiles();
        profileComboBox.setItems(FXCollections.observableArrayList(profiles));

        boolean isEmpty = profiles.isEmpty();
        profileComboBox.setDisable(isEmpty);
        loadBtn.setDisable(isEmpty);
        deleteBtn.setDisable(isEmpty);

        if (isEmpty) {
            profileComboBox.getSelectionModel().clearSelection();
            profileComboBox.setPromptText("No profiles yet — create one below");
        } else {
            profileComboBox.setPromptText("Choose your profile...");
            profileComboBox.getSelectionModel().selectFirst();
        }
    }

    private void handleLoadSelected() {
        UserProfile selected = profileComboBox.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Profile Selected", "Please select an existing profile from the list.");
            return;
        }
        onProfileSelected.accept(selected);
    }

    private void handleDeleteSelected() {
        UserProfile selected = profileComboBox.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Profile Selected", "Please select a profile to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Profile");
        confirm.setHeaderText("Delete '" + selected.getUsername() + "'?");
        confirm.setContentText("All measurements for this profile will be permanently deleted.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                healthTrackerService.deleteProfile(selected.getId());
                loadProfiles();
            }
        });
    }

    private void handleCreateNew() {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        if (username.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Username Required", "Please enter a username to create a new profile.");
            return;
        }

        Gender gender = genderBox.getValue() != null ? genderBox.getValue() : Gender.MALE;
        LocalDate birthDate = birthDatePicker.getValue() != null ? birthDatePicker.getValue() : LocalDate.of(1995, 1, 1);

        UserProfile created = healthTrackerService.getOrCreateProfileByUsername(username, gender, birthDate);
        onProfileSelected.accept(created);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type, content, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
