package com.black.bmitracker.ui;

import com.black.bmitracker.domain.dto.BmiResultDto;
import com.black.bmitracker.domain.model.MeasurementEntry;
import com.black.bmitracker.domain.model.UserProfile;
import com.black.bmitracker.domain.model.enums.UnitSystem;
import com.black.bmitracker.domain.service.HealthTrackerService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainDashboardView {

    private final HealthTrackerService healthTrackerService;
    private final UserProfile activeProfile;
    private final Runnable onBackToProfiles;

    private final BorderPane rootPane = new BorderPane();
    private final ObservableList<MeasurementEntry> historyData = FXCollections.observableArrayList();
    private final XYChart.Series<String, Number> trendSeries = new XYChart.Series<>();

    private Label currentBmiLabel;
    private Label categoryLabel;
    private BmiGaugeView gaugeView;

    private ComboBox<UnitSystem> unitSystemCombo;
    private Label weightLabel;
    private TextField weightField;
    private Label heightLabel;
    private TextField heightField;
    private DatePicker datePicker;
    private CheckBox useCurrentTimeCheck;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public MainDashboardView(HealthTrackerService healthTrackerService, UserProfile activeProfile, Runnable onBackToProfiles) {
        this.healthTrackerService = healthTrackerService;
        this.activeProfile = activeProfile;
        this.onBackToProfiles = onBackToProfiles;

        initializeUi();
        loadUserData();
    }

    public BorderPane getView() {
        return rootPane;
    }

    private void initializeUi() {
        rootPane.setStyle("-fx-background-color: #0f172a;");
        rootPane.setTop(createHeader());
        rootPane.setLeft(createSidebar());
        rootPane.setCenter(createCenterDashboard());
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 24, 16, 24));
        header.setStyle("-fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-width: 0 0 1 0;");

        Label brandLabel = new Label("BMI TRACKER PRO");
        brandLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        brandLabel.setTextFill(Color.web("#818cf8"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLabel = new Label("User: " + activeProfile.getUsername());
        userLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 13));
        userLabel.setTextFill(Color.web("#cbd5e1"));

        Button switchUserBtn = new Button("Switch User");
        switchUserBtn.setStyle("-fx-background-color: #334155; -fx-text-fill: #cbd5e1; -fx-background-radius: 6; -fx-cursor: hand;");
        switchUserBtn.setOnAction(e -> onBackToProfiles.run());

        header.getChildren().addAll(brandLabel, spacer, userLabel, switchUserBtn);
        header.setSpacing(15);
        return header;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(14);
        sidebar.setPadding(new Insets(24));
        sidebar.setPrefWidth(280);
        sidebar.setStyle("-fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-width: 0 1 0 0;");

        Label formTitle = new Label("New Measurement");
        formTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        formTitle.setTextFill(Color.web("#f8fafc"));

        Label unitLabel = new Label("Unit System");
        unitLabel.setTextFill(Color.web("#94a3b8"));

        unitSystemCombo = new ComboBox<>(FXCollections.observableArrayList(UnitSystem.values()));
        unitSystemCombo.getSelectionModel().select(UnitSystem.METRIC);
        unitSystemCombo.setMaxWidth(Double.MAX_VALUE);
        unitSystemCombo.setStyle("-fx-background-color: #475569; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6;");

        weightLabel = new Label("Weight (kg)");
        weightLabel.setTextFill(Color.web("#94a3b8"));
        weightField = new TextField();
        weightField.setPromptText("e.g. 70.0");
        styleInput(weightField);

        heightLabel = new Label("Height (cm)");
        heightLabel.setTextFill(Color.web("#94a3b8"));
        heightField = new TextField();
        heightField.setPromptText("e.g. 175.0");
        styleInput(heightField);

        unitSystemCombo.setOnAction(e -> updateUnitSystemLabels());

        Label dateLabel = new Label("Measurement Date");
        dateLabel.setTextFill(Color.web("#94a3b8"));

        datePicker = new DatePicker(LocalDate.now());
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setDisable(true);

        useCurrentTimeCheck = new CheckBox("Use Current Time");
        useCurrentTimeCheck.setSelected(true);
        useCurrentTimeCheck.setTextFill(Color.web("#cbd5e1"));
        useCurrentTimeCheck.setOnAction(e -> datePicker.setDisable(useCurrentTimeCheck.isSelected()));

        Button recordBtn = new Button("Record Measurement");
        recordBtn.setMaxWidth(Double.MAX_VALUE);
        recordBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        recordBtn.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 10; -fx-cursor: hand;");
        recordBtn.setOnAction(e -> handleRecordMeasurement());

        sidebar.getChildren().addAll(formTitle, unitLabel, unitSystemCombo, weightLabel, weightField, heightLabel, heightField, dateLabel, useCurrentTimeCheck, datePicker, recordBtn);
        return sidebar;
    }

    private void updateUnitSystemLabels() {
        if (unitSystemCombo.getValue() == UnitSystem.METRIC) {
            weightLabel.setText("Weight (kg)");
            weightField.setPromptText("e.g. 70.0");
            heightLabel.setText("Height (cm)");
            heightField.setPromptText("e.g. 175.0");
        } else {
            weightLabel.setText("Weight (lbs)");
            weightField.setPromptText("e.g. 154.3");
            heightLabel.setText("Height (in)");
            heightField.setPromptText("e.g. 68.9");
        }
    }

    private void styleInput(TextField tf) {
        tf.setStyle("-fx-background-color: #0f172a; -fx-text-fill: #f8fafc; -fx-border-color: #475569; -fx-border-radius: 6; -fx-padding: 8;");
    }

    private VBox createCenterDashboard() {
        VBox centerBox = new VBox(16);
        centerBox.setPadding(new Insets(20));

        HBox statCards = new HBox(16);
        VBox summaryCard = new VBox(8);
        summaryCard.setPadding(new Insets(16));
        summaryCard.setPrefWidth(260);
        summaryCard.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 8; -fx-border-color: #334155; -fx-border-radius: 8;");

        Label bmiTitle = new Label("CURRENT BMI");
        bmiTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        bmiTitle.setTextFill(Color.web("#94a3b8"));

        currentBmiLabel = new Label("--");
        currentBmiLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        currentBmiLabel.setTextFill(Color.web("#f8fafc"));

        categoryLabel = new Label("NO DATA");
        categoryLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        categoryLabel.setTextFill(Color.web("#64748b"));

        summaryCard.getChildren().addAll(bmiTitle, currentBmiLabel, categoryLabel);

        VBox gaugeCard = new VBox(4);
        gaugeCard.setPadding(new Insets(10));
        gaugeCard.setAlignment(Pos.CENTER);
        gaugeCard.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 8; -fx-border-color: #334155; -fx-border-radius: 8;");
        gaugeView = new BmiGaugeView();
        gaugeCard.getChildren().add(gaugeView);

        statCards.getChildren().addAll(summaryCard, gaugeCard);

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Date");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("BMI");

        LineChart<String, Number> trendChart = new LineChart<>(xAxis, yAxis);
        trendChart.setTitle("BMI Trend Over Time");
        trendChart.setLegendVisible(false);
        trendChart.setPrefHeight(200);
        trendChart.getData().add(trendSeries);

        TableView<MeasurementEntry> table = createTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        centerBox.getChildren().addAll(statCards, trendChart, table);
        return centerBox;
    }

    @SuppressWarnings("unchecked")
    private TableView<MeasurementEntry> createTable() {
        TableView<MeasurementEntry> table = new TableView<>(historyData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<MeasurementEntry, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRecordedAt().format(FORMATTER)));

        TableColumn<MeasurementEntry, Number> weightCol = new TableColumn<>("Weight (kg)");
        weightCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getWeightKg()));

        TableColumn<MeasurementEntry, Number> heightCol = new TableColumn<>("Height (cm)");
        heightCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getHeightCm()));

        TableColumn<MeasurementEntry, Number> bmiCol = new TableColumn<>("BMI");
        bmiCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getBmiValue()));

        table.getColumns().addAll(dateCol, weightCol, heightCol, bmiCol);
        return table;
    }

    private void handleRecordMeasurement() {
        try {
            double weight = Double.parseDouble(weightField.getText().trim());
            double height = Double.parseDouble(heightField.getText().trim());
            UnitSystem unit = unitSystemCombo.getValue();

            LocalDateTime timestamp = useCurrentTimeCheck.isSelected()
                    ? LocalDateTime.now()
                    : LocalDateTime.of(datePicker.getValue(), LocalTime.of(12, 0));

            BmiResultDto result = healthTrackerService.recordMeasurement(activeProfile, weight, height, unit, timestamp);

            currentBmiLabel.setText(String.format("%.2f", result.bmiValue()));
            categoryLabel.setText(result.category().name());
            categoryLabel.setTextFill(Color.web(switch (result.category()) {
                case UNDERWEIGHT -> "#38bdf8";
                case NORMAL -> "#22c55e";
                case OVERWEIGHT -> "#f59e0b";
                default -> "#ef4444";
            }));

            gaugeView.setBmi(result.bmiValue());
            weightField.clear();
            heightField.clear();
            loadUserData();
        } catch (NumberFormatException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter valid numeric values for weight and height.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void loadUserData() {
        List<MeasurementEntry> history = healthTrackerService.getMeasurementHistory(activeProfile.getId());
        historyData.setAll(history);

        if (!history.isEmpty()) {
            MeasurementEntry latest = history.getFirst();
            currentBmiLabel.setText(String.format("%.2f", latest.getBmiValue()));
            gaugeView.setBmi(latest.getBmiValue());
        }

        trendSeries.getData().clear();
        List<MeasurementEntry> trend = healthTrackerService.getTrendData(activeProfile.getId());
        for (MeasurementEntry entry : trend) {
            trendSeries.getData().add(new XYChart.Data<>(entry.getRecordedAt().format(FORMATTER), entry.getBmiValue()));
        }
    }
}
