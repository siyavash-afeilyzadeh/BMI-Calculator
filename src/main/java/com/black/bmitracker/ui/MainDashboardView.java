package com.black.bmitracker.ui;

import com.black.bmitracker.domain.dto.BmiResultDto;
import com.black.bmitracker.domain.model.MeasurementEntry;
import com.black.bmitracker.domain.model.UserProfile;
import com.black.bmitracker.domain.model.enums.BmiCategory;
import com.black.bmitracker.domain.model.enums.UnitSystem;
import com.black.bmitracker.domain.service.BmiCategoryClassifier;
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
    private final BmiCategoryClassifier categoryClassifier = new BmiCategoryClassifier();

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
        rootPane.setStyle("-fx-background-color: #0b1120;");
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
        brandLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        brandLabel.setTextFill(Color.web("#818cf8"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLabel = new Label("Active User: " + activeProfile.getUsername());
        userLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        userLabel.setTextFill(Color.web("#f1f5f9"));

        Button switchUserBtn = new Button("Switch User");
        switchUserBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        switchUserBtn.setStyle("-fx-background-color: #4338ca; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 6 12 6 12;");
        switchUserBtn.setOnAction(e -> onBackToProfiles.run());

        header.getChildren().addAll(brandLabel, spacer, userLabel, switchUserBtn);
        header.setSpacing(15);
        return header;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(14);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(310);
        sidebar.setMinWidth(290);
        sidebar.setStyle("-fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-width: 0 1 0 0;");

        Label formTitle = new Label("New Measurement");
        formTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        formTitle.setTextFill(Color.web("#ffffff"));

        Label unitLabel = new Label("Unit System:");
        unitLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        unitLabel.setTextFill(Color.web("#e2e8f0"));

        // High contrast dropdown for Unit System
        unitSystemCombo = new ComboBox<>(FXCollections.observableArrayList(UnitSystem.values()));
        unitSystemCombo.getSelectionModel().select(UnitSystem.METRIC);
        unitSystemCombo.setMaxWidth(Double.MAX_VALUE);
        unitSystemCombo.setStyle(
                "-fx-background-color: #4f46e5; " +
                        "-fx-mark-color: #ffffff; " +
                        "-fx-border-color: #a5b4fc; " +
                        "-fx-border-width: 1.5; " +
                        "-fx-border-radius: 6; " +
                        "-fx-background-radius: 6; " +
                        "-fx-padding: 5;"
        );

        unitSystemCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(UnitSystem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.name());
                    setTextFill(Color.WHITE);
                    setStyle("-fx-background-color: #312e81; -fx-font-weight: bold;");
                }
            }
        });

        unitSystemCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(UnitSystem item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.name());
                setTextFill(Color.WHITE);
                setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
            }
        });

        weightLabel = new Label("Weight (kg)");
        weightLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        weightLabel.setTextFill(Color.web("#e2e8f0"));
        weightField = new TextField();
        weightField.setPromptText("e.g. 70.0");
        styleInput(weightField);

        heightLabel = new Label("Height (cm)");
        heightLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        heightLabel.setTextFill(Color.web("#e2e8f0"));
        heightField = new TextField();
        heightField.setPromptText("e.g. 175.0");
        styleInput(heightField);

        unitSystemCombo.setOnAction(e -> updateUnitSystemLabels());

        Label dateLabel = new Label("Measurement Date:");
        dateLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        dateLabel.setTextFill(Color.web("#e2e8f0"));

        datePicker = new DatePicker(LocalDate.now());
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setDisable(true);
        datePicker.setStyle("-fx-control-inner-background: #0f172a; -fx-text-fill: white;");

        useCurrentTimeCheck = new CheckBox("Use Current Time");
        useCurrentTimeCheck.setSelected(true);
        useCurrentTimeCheck.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        useCurrentTimeCheck.setTextFill(Color.web("#f1f5f9"));
        useCurrentTimeCheck.setOnAction(e -> datePicker.setDisable(useCurrentTimeCheck.isSelected()));

        Button recordBtn = new Button("Record Measurement");
        recordBtn.setMaxWidth(Double.MAX_VALUE);
        recordBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        recordBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 10; -fx-cursor: hand;");
        recordBtn.setOnAction(e -> handleRecordMeasurement());

        sidebar.getChildren().addAll(
                formTitle,
                unitLabel, unitSystemCombo,
                weightLabel, weightField,
                heightLabel, heightField,
                dateLabel, useCurrentTimeCheck, datePicker,
                recordBtn
        );
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
        tf.setStyle("-fx-background-color: #0f172a; -fx-text-fill: #ffffff; -fx-border-color: #475569; -fx-border-radius: 6; -fx-padding: 8;");
    }

    private VBox createCenterDashboard() {
        VBox centerBox = new VBox(16);
        centerBox.setPadding(new Insets(20));

        // Top Stat Panels: Result Card + Gauge Card
        HBox topPanels = new HBox(16);
        topPanels.setAlignment(Pos.CENTER_LEFT);

        VBox summaryCard = new VBox(10);
        summaryCard.setPadding(new Insets(16));
        summaryCard.setPrefWidth(260);
        summaryCard.setMinHeight(180);
        summaryCard.setAlignment(Pos.CENTER_LEFT);
        summaryCard.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 10; -fx-border-color: #334155;");

        Label bmiTitle = new Label("SELECTED / LATEST BMI");
        bmiTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        bmiTitle.setTextFill(Color.web("#94a3b8"));

        currentBmiLabel = new Label("--");
        currentBmiLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        currentBmiLabel.setTextFill(Color.web("#ffffff"));

        categoryLabel = new Label("NO DATA");
        categoryLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        categoryLabel.setTextFill(Color.web("#64748b"));

        summaryCard.getChildren().addAll(bmiTitle, currentBmiLabel, categoryLabel);

        VBox gaugeCard = new VBox();
        gaugeCard.setPadding(new Insets(10));
        gaugeCard.setMinHeight(180);
        gaugeCard.setAlignment(Pos.CENTER);
        gaugeCard.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 10; -fx-border-color: #334155;");
        gaugeView = new BmiGaugeView();
        gaugeCard.getChildren().add(gaugeView);

        HBox.setHgrow(gaugeCard, Priority.ALWAYS);
        topPanels.getChildren().addAll(summaryCard, gaugeCard);

        // Trend Chart
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Measurement Time");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("BMI Value");

        LineChart<String, Number> trendChart = new LineChart<>(xAxis, yAxis);
        trendChart.setTitle("BMI Trend Over Time");
        trendChart.setLegendVisible(false);
        trendChart.setPrefHeight(230);
        trendChart.setMinHeight(180);
        trendChart.getData().add(trendSeries);

        // History Table
        TableView<MeasurementEntry> table = createTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        centerBox.getChildren().addAll(topPanels, trendChart, table);
        return centerBox;
    }

    @SuppressWarnings("unchecked")
    private TableView<MeasurementEntry> createTable() {
        TableView<MeasurementEntry> table = new TableView<>(historyData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<MeasurementEntry, String> dateCol = new TableColumn<>("Date & Time");
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRecordedAt().format(FORMATTER)));

        TableColumn<MeasurementEntry, Number> weightCol = new TableColumn<>("Weight (kg)");
        weightCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getWeightKg()));

        TableColumn<MeasurementEntry, Number> heightCol = new TableColumn<>("Height (cm)");
        heightCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getHeightCm()));

        TableColumn<MeasurementEntry, Number> bmiCol = new TableColumn<>("BMI");
        bmiCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getBmiValue()));

        TableColumn<MeasurementEntry, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(data -> {
            BmiCategory cat = categoryClassifier.classify(data.getValue().getBmiValue());
            return new SimpleStringProperty(categoryClassifier.getDisplayName(cat));
        });

        table.getColumns().addAll(dateCol, weightCol, heightCol, bmiCol, catCol);

        // Interactive Selection: clicking any row updates the Gauge and Status Card immediately!
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateBmiDisplay(newVal.getBmiValue());
            }
        });

        return table;
    }

    private void handleRecordMeasurement() {
        try {
            double weight = Double.parseDouble(weightField.getText().trim());
            double height = Double.parseDouble(heightField.getText().trim());
            UnitSystem unit = unitSystemCombo.getValue();

            LocalDateTime timestamp = useCurrentTimeCheck.isSelected()
                    ? LocalDateTime.now()
                    : LocalDateTime.of(datePicker.getValue(), LocalTime.now());

            BmiResultDto result = healthTrackerService.recordMeasurement(activeProfile, weight, height, unit, timestamp);

            // Display newly recorded measurement immediately!
            updateBmiDisplay(result.bmiValue());

            weightField.clear();
            heightField.clear();
            loadUserData();
        } catch (NumberFormatException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter valid numeric values for weight and height.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void updateBmiDisplay(double bmi) {
        currentBmiLabel.setText(String.format("%.2f", bmi));
        BmiCategory category = categoryClassifier.classify(bmi);
        categoryLabel.setText(categoryClassifier.getDisplayName(category));

        categoryLabel.setTextFill(Color.web(switch (category) {
            case UNDERWEIGHT -> "#38bdf8";
            case NORMAL -> "#22c55e";
            case OVERWEIGHT -> "#eab308";
            case OBESE_CLASS_I -> "#f97316";
            case OBESE_CLASS_II -> "#ef4444";
            case OBESE_CLASS_III -> "#991b1b";
        }));

        gaugeView.setBmi(bmi);
    }

    private void loadUserData() {
        List<MeasurementEntry> history = healthTrackerService.getMeasurementHistory(activeProfile.getId());
        historyData.setAll(history);

        if (!history.isEmpty()) {
            MeasurementEntry latest = history.getFirst();
            updateBmiDisplay(latest.getBmiValue());
        }

        trendSeries.getData().clear();
        List<MeasurementEntry> trend = healthTrackerService.getTrendData(activeProfile.getId());
        for (MeasurementEntry entry : trend) {
            trendSeries.getData().add(new XYChart.Data<>(entry.getRecordedAt().format(FORMATTER), entry.getBmiValue()));
        }
    }
}
