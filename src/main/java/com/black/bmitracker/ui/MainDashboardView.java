package com.black.bmitracker.ui;

import com.black.bmitracker.domain.dto.BmiResultDto;
import com.black.bmitracker.domain.model.MeasurementEntry;
import com.black.bmitracker.domain.model.UserProfile;
import com.black.bmitracker.domain.model.enums.Gender;
import com.black.bmitracker.domain.model.enums.UnitSystem;
import com.black.bmitracker.domain.service.HealthTrackerService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainDashboardView {

    private final HealthTrackerService healthTrackerService;
    private UserProfile currentProfile;

    private final BorderPane rootLayout;
    private TextField weightField;
    private TextField heightField;
    private ComboBox<UnitSystem> unitSystemComboBox;
    private Label bmiValueLabel;
    private Label bmiCategoryLabel;
    private TableView<MeasurementEntry> historyTable;
    private XYChart.Series<String, Number> bmiSeries;

    public MainDashboardView() {
        this.healthTrackerService = new HealthTrackerService();
        this.rootLayout = new BorderPane();
        initializeDefaultProfile();
        buildUI();
        refreshDashboardData();
    }

    public Parent getView() {
        return rootLayout;
    }

    private void initializeDefaultProfile() {
        this.currentProfile = healthTrackerService.getOrCreateDefaultProfile(
                "Mr. Black",
                Gender.MALE,
                LocalDate.of(1992, 1, 1)
        );
    }

    private void buildUI() {
        rootLayout.setStyle("-fx-background-color: #12141d;");
        rootLayout.setTop(createHeader());
        rootLayout.setLeft(createInputSidebar());
        rootLayout.setCenter(createCenterContent());
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 24, 16, 24));
        header.setStyle("-fx-background-color: #1a1d28; -fx-border-color: #272b3c; -fx-border-width: 0 0 1 0;");

        Label title = new Label("BMI TRACKER PRO");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#6366f1"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label profileInfoLabel = new Label("User: " + currentProfile.getUsername());
        profileInfoLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        profileInfoLabel.setTextFill(Color.web("#94a3b8"));

        header.getChildren().addAll(title, spacer, profileInfoLabel);
        return header;
    }

    private VBox createInputSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(24));
        sidebar.setPrefWidth(300);
        sidebar.setStyle("-fx-background-color: #1a1d28; -fx-border-color: #272b3c; -fx-border-width: 0 1 0 0;");

        Label formTitle = new Label("New Measurement");
        formTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        formTitle.setTextFill(Color.web("#f8fafc"));

        Label unitLabel = new Label("Unit System");
        unitLabel.setTextFill(Color.web("#94a3b8"));
        unitSystemComboBox = new ComboBox<>(FXCollections.observableArrayList(UnitSystem.METRIC, UnitSystem.IMPERIAL));
        unitSystemComboBox.setValue(UnitSystem.METRIC);
        unitSystemComboBox.setMaxWidth(Double.MAX_VALUE);
        unitSystemComboBox.setStyle("-fx-background-color: #242938; -fx-text-fill: white; -fx-mark-color: white;");

        Label weightLabel = new Label("Weight (kg / lbs)");
        weightLabel.setTextFill(Color.web("#94a3b8"));
        weightField = new TextField();
        weightField.setPromptText("e.g. 75.5");
        applyInputStyle(weightField);

        Label heightLabel = new Label("Height (cm / in)");
        heightLabel.setTextFill(Color.web("#94a3b8"));
        heightField = new TextField();
        heightField.setPromptText("e.g. 178.0");
        applyInputStyle(heightField);

        Button calculateBtn = new Button("Record Measurement");
        calculateBtn.setMaxWidth(Double.MAX_VALUE);
        calculateBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        calculateBtn.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white; -fx-padding: 10 16; -fx-background-radius: 6; -fx-cursor: hand;");
        calculateBtn.setOnAction(_ -> handleRecordMeasurement());

        sidebar.getChildren().addAll(
                formTitle,
                unitLabel, unitSystemComboBox,
                weightLabel, weightField,
                heightLabel, heightField,
                calculateBtn
        );

        return sidebar;
    }

    private VBox createCenterContent() {
        VBox center = new VBox(20);
        center.setPadding(new Insets(24));

        HBox statsCard = createStatsCard();

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Date");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("BMI");

        LineChart<String, Number> trendChart = new LineChart<>(xAxis, yAxis);
        trendChart.setTitle("BMI Trend Over Time");
        trendChart.setPrefHeight(250);
        trendChart.setLegendVisible(false);
        trendChart.setStyle("-fx-background: transparent; -fx-text-base-color: #94a3b8;");

        bmiSeries = new XYChart.Series<>();
        trendChart.getData().add(bmiSeries);

        createHistoryTable();

        VBox.setVgrow(historyTable, Priority.ALWAYS);
        center.getChildren().addAll(statsCard, trendChart, historyTable);
        return center;
    }

    private HBox createStatsCard() {
        HBox card = new HBox(30);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: #1e2230; -fx-background-radius: 8; -fx-border-color: #2b3044; -fx-border-radius: 8;");

        VBox bmiBox = new VBox(4);
        Label titleLabel = new Label("CURRENT BMI");
        titleLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        titleLabel.setTextFill(Color.web("#94a3b8"));

        bmiValueLabel = new Label("--");
        bmiValueLabel.setFont(Font.font("System", FontWeight.BOLD, 36));
        bmiValueLabel.setTextFill(Color.web("#f8fafc"));
        bmiBox.getChildren().addAll(titleLabel, bmiValueLabel);

        VBox statusBox = new VBox(4);
        Label statusTitle = new Label("CLASSIFICATION");
        statusTitle.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        statusTitle.setTextFill(Color.web("#94a3b8"));

        bmiCategoryLabel = new Label("No Data Yet");
        bmiCategoryLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
        bmiCategoryLabel.setTextFill(Color.web("#38bdf8"));
        statusBox.getChildren().addAll(statusTitle, bmiCategoryLabel);

        card.getChildren().addAll(bmiBox, statusBox);
        return card;
    }

    @SuppressWarnings("unchecked")
    private void createHistoryTable() {
        historyTable = new TableView<>();
        historyTable.setStyle("-fx-background-color: #1a1d28; -fx-base: #1a1d28; -fx-control-inner-background: #1a1d28; -fx-text-fill: white;");

        TableColumn<MeasurementEntry, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return new javafx.beans.property.SimpleStringProperty(data.getValue().getRecordedAt().format(formatter));
        });
        dateCol.setPrefWidth(160);

        TableColumn<MeasurementEntry, Double> weightCol = new TableColumn<>("Weight (kg)");
        weightCol.setCellValueFactory(new PropertyValueFactory<>("weightKg"));
        weightCol.setPrefWidth(120);

        TableColumn<MeasurementEntry, Double> heightCol = new TableColumn<>("Height (cm)");
        heightCol.setCellValueFactory(new PropertyValueFactory<>("heightCm"));
        heightCol.setPrefWidth(120);

        TableColumn<MeasurementEntry, Double> bmiCol = new TableColumn<>("BMI");
        bmiCol.setCellValueFactory(new PropertyValueFactory<>("bmiValue"));
        bmiCol.setPrefWidth(100);

        historyTable.getColumns().addAll(dateCol, weightCol, heightCol, bmiCol);
    }

    private void handleRecordMeasurement() {
        try {
            double weight = Double.parseDouble(weightField.getText().trim());
            double height = Double.parseDouble(heightField.getText().trim());
            UnitSystem system = unitSystemComboBox.getValue();

            BmiResultDto result = healthTrackerService.recordMeasurement(currentProfile, weight, height, system);

            bmiValueLabel.setText(String.format("%.2f", result.bmiValue()));
            bmiCategoryLabel.setText(result.category().name().replace("_", " "));
            applyCategoryColor(result.category().name());

            weightField.clear();
            heightField.clear();

            refreshDashboardData();
        } catch (NumberFormatException _) {
            showError("Invalid Input", "Please enter valid numeric values for height and weight.");
        } catch (Exception ex) {
            showError("Error", ex.getMessage());
        }
    }

    private void refreshDashboardData() {
        List<MeasurementEntry> history = healthTrackerService.getMeasurementHistory(currentProfile.getId());
        ObservableList<MeasurementEntry> tableData = FXCollections.observableArrayList(history);
        historyTable.setItems(tableData);

        if (!history.isEmpty()) {
            MeasurementEntry latest = history.getFirst();
            bmiValueLabel.setText(String.format("%.2f", latest.getBmiValue()));
        }

        List<MeasurementEntry> trend = healthTrackerService.getTrendData(currentProfile.getId());
        bmiSeries.getData().clear();
        DateTimeFormatter chartFormatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
        for (MeasurementEntry entry : trend) {
            bmiSeries.getData().add(new XYChart.Data<>(entry.getRecordedAt().format(chartFormatter), entry.getBmiValue()));
        }
    }

    private void applyCategoryColor(String category) {
        if (category.contains("NORMAL")) {
            bmiCategoryLabel.setTextFill(Color.web("#22c55e"));
        } else if (category.contains("UNDERWEIGHT")) {
            bmiCategoryLabel.setTextFill(Color.web("#38bdf8"));
        } else if (category.contains("OVERWEIGHT")) {
            bmiCategoryLabel.setTextFill(Color.web("#f59e0b"));
        } else {
            bmiCategoryLabel.setTextFill(Color.web("#ef4444"));
        }
    }

    private void applyInputStyle(TextField tf) {
        tf.setStyle("-fx-background-color: #242938; -fx-text-fill: white; -fx-prompt-text-fill: #64748b; -fx-border-color: #334155; -fx-border-radius: 4; -fx-padding: 8;");
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
