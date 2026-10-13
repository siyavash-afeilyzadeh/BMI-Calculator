package com.black.bmitracker.ui.view;

import com.black.bmitracker.application.dto.BmiResultDto;
import com.black.bmitracker.application.service.HealthTrackerService;
import com.black.bmitracker.domain.model.entity.MeasurementEntry;
import com.black.bmitracker.domain.model.entity.UserProfile;
import com.black.bmitracker.domain.model.enums.UnitSystem;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainDashboardView extends BorderPane {

    private final HealthTrackerService healthTrackerService;
    private final UserProfile activeProfile;
    private final Runnable onSwitchUserRequested;

    private ComboBox<UnitSystem> unitSystemBox;
    private TextField weightField;
    private TextField heightField;
    private DatePicker datePicker;
    private CheckBox useCurrentTimeCheck;

    private Label currentBmiValueLabel;
    private Label currentBmiCategoryLabel;
    private BmiGaugeView bmiGaugeView;

    private LineChart<String, Number> trendChart;
    private XYChart.Series<String, Number> trendSeries;
    private TableView<MeasurementEntry> historyTable;

    public MainDashboardView(HealthTrackerService healthTrackerService,
                             UserProfile activeProfile,
                             Runnable onSwitchUserRequested) {
        this.healthTrackerService = healthTrackerService;
        this.activeProfile = activeProfile;
        this.onSwitchUserRequested = onSwitchUserRequested;

        initUi();
        loadUserData();
    }

    private void initUi() {
        setStyle("-fx-background-color: #0b1120;");
        setTop(buildHeader());
        setLeft(buildSidebar());
        setCenter(buildCenterContent());
    }

    private HBox buildHeader() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 28, 16, 28));
        header.setStyle("-fx-background-color: #0f172a; -fx-border-color: #1e293b; -fx-border-width: 0 0 1 0;");

        Label brand = new Label("BMI TRACKER");
        brand.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        brand.setTextFill(Color.web("#818cf8"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLabel = new Label("Active User: " + activeProfile.getUsername());
        userLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        userLabel.setTextFill(Color.web("#f8fafc"));

        Button switchUserBtn = new Button("Switch User");
        switchUserBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        switchUserBtn.setStyle("-fx-background-color: #4338ca; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 7 14;");
        switchUserBtn.setOnAction(e -> onSwitchUserRequested.run());

        // Exit / Close Button (Red 'X')
        Button exitBtn = new Button("✕");
        exitBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        exitBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 6 12;");
        exitBtn.setTooltip(new Tooltip("Exit Application"));
        exitBtn.setOnAction(e -> {
            Platform.exit();
            System.exit(0);
        });

        header.getChildren().addAll(brand, spacer, userLabel, switchUserBtn, exitBtn);
        return header;
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(14);
        sidebar.setPrefWidth(280);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #0f172a; -fx-border-color: #1e293b; -fx-border-width: 0 1 0 0;");

        Label sidebarTitle = new Label("New Measurement");
        sidebarTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        sidebarTitle.setTextFill(Color.web("#f8fafc"));

        Label unitLabel = new Label("Unit System:");
        unitLabel.setTextFill(Color.web("#94a3b8"));
        unitSystemBox = new ComboBox<>(FXCollections.observableArrayList(UnitSystem.values()));
        unitSystemBox.setValue(UnitSystem.METRIC);
        unitSystemBox.setMaxWidth(Double.MAX_VALUE);
        unitSystemBox.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: white; -fx-background-radius: 6;");

        Label weightLabel = new Label("Weight (kg)");
        weightLabel.setTextFill(Color.web("#94a3b8"));
        weightField = new TextField();
        weightField.setPromptText("e.g. 70.0");
        styleInput(weightField);

        Label heightLabel = new Label("Height (cm)");
        heightLabel.setTextFill(Color.web("#94a3b8"));
        heightField = new TextField();
        heightField.setPromptText("e.g. 175.0");
        styleInput(heightField);

        unitSystemBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == UnitSystem.IMPERIAL) {
                weightLabel.setText("Weight (lbs)");
                heightLabel.setText("Height (in)");
                weightField.setPromptText("e.g. 154.0");
                heightField.setPromptText("e.g. 68.9");
            } else {
                weightLabel.setText("Weight (kg)");
                heightLabel.setText("Height (cm)");
                weightField.setPromptText("e.g. 70.0");
                heightField.setPromptText("e.g. 175.0");
            }
        });

        Label dateLabel = new Label("Measurement Date:");
        dateLabel.setTextFill(Color.web("#94a3b8"));

        useCurrentTimeCheck = new CheckBox("Use Current Time");
        useCurrentTimeCheck.setSelected(true);
        useCurrentTimeCheck.setTextFill(Color.web("#cbd5e1"));

        datePicker = new DatePicker(LocalDate.now());
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setDisable(true);
        datePicker.setStyle("-fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-radius: 6;");

        useCurrentTimeCheck.selectedProperty().addListener((obs, oldVal, isNow) -> datePicker.setDisable(Boolean.TRUE.equals(isNow)));

        Button recordBtn = new Button("Record Measurement");
        recordBtn.setMaxWidth(Double.MAX_VALUE);
        recordBtn.setPrefHeight(40);
        recordBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        recordBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
        recordBtn.setOnAction(e -> handleRecordMeasurement());

        sidebar.getChildren().addAll(
                sidebarTitle,
                unitLabel, unitSystemBox,
                weightLabel, weightField,
                heightLabel, heightField,
                dateLabel, useCurrentTimeCheck, datePicker,
                recordBtn
        );

        return sidebar;
    }

    private void styleInput(TextField field) {
        field.setStyle("-fx-background-color: #1e293b; -fx-text-fill: #f8fafc; -fx-prompt-text-fill: #64748b; -fx-border-color: #334155; -fx-border-radius: 6; -fx-padding: 8;");
    }

    private VBox buildCenterContent() {
        VBox centerBox = new VBox(16);
        centerBox.setPadding(new Insets(20));

        HBox topRow = new HBox(16);
        topRow.setAlignment(Pos.CENTER);

        VBox mergedGaugeCard = buildMergedBmiCard();
        VBox chartCard = buildChartCard();
        HBox.setHgrow(chartCard, Priority.ALWAYS);

        topRow.getChildren().addAll(mergedGaugeCard, chartCard);

        historyTable = buildHistoryTable();
        VBox.setVgrow(historyTable, Priority.ALWAYS);

        centerBox.getChildren().addAll(topRow, historyTable);
        return centerBox;
    }

    private VBox buildMergedBmiCard() {
        VBox card = new VBox(10);
        card.setPrefWidth(380);
        card.setMinWidth(380);
        card.setPrefHeight(230);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 10; -fx-border-color: #334155; -fx-border-radius: 10;");

        Label title = new Label("SELECTED / LATEST BMI");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        title.setTextFill(Color.web("#94a3b8"));

        bmiGaugeView = new BmiGaugeView();
        bmiGaugeView.setPrefSize(240, 120);

        HBox statsBox = new HBox(14);
        statsBox.setAlignment(Pos.CENTER);

        currentBmiValueLabel = new Label("--");
        currentBmiValueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        currentBmiValueLabel.setTextFill(Color.web("#f8fafc"));

        currentBmiCategoryLabel = new Label("NO DATA");
        currentBmiCategoryLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        currentBmiCategoryLabel.setTextFill(Color.web("#64748b"));

        statsBox.getChildren().addAll(currentBmiValueLabel, currentBmiCategoryLabel);
        card.getChildren().addAll(title, bmiGaugeView, statsBox);
        return card;
    }

    private VBox buildChartCard() {
        VBox chartCard = new VBox(5);
        chartCard.setPadding(new Insets(10));
        chartCard.setPrefHeight(230);
        chartCard.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 10; -fx-border-color: #334155; -fx-border-radius: 10;");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Measurement Time");
        xAxis.setTickLabelFill(Color.web("#64748b"));

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("BMI Value");
        yAxis.setTickLabelFill(Color.web("#64748b"));

        trendChart = new LineChart<>(xAxis, yAxis);
        trendChart.setTitle("BMI Trend Over Time");
        trendChart.setLegendVisible(false);
        trendChart.setAnimated(false);
        trendChart.setStyle("-fx-background-color: transparent;");

        trendSeries = new XYChart.Series<>();
        trendChart.getData().add(trendSeries);

        VBox.setVgrow(trendChart, Priority.ALWAYS);
        chartCard.getChildren().add(trendChart);
        return chartCard;
    }

    private String classifyBmi(double bmi) {
        if (bmi < 18.5) return "Underweight";
        if (bmi < 25.0) return "Normal weight";
        if (bmi < 30.0) return "Overweight";
        if (bmi < 35.0) return "Obesity Class I";
        if (bmi < 40.0) return "Obesity Class II";
        return "Obesity Class III";
    }

    private TableView<MeasurementEntry> buildHistoryTable() {
        TableView<MeasurementEntry> table = new TableView<>();
        table.setStyle("-fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-radius: 8;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        TableColumn<MeasurementEntry, String> dateCol = new TableColumn<>("Date & Time");
        dateCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getRecordedAt() != null ? d.getValue().getRecordedAt().format(dtf) : ""));

        TableColumn<MeasurementEntry, Double> weightCol = new TableColumn<>("Weight (kg)");
        weightCol.setCellValueFactory(new PropertyValueFactory<>("weightKg"));

        TableColumn<MeasurementEntry, Double> heightCol = new TableColumn<>("Height (cm)");
        heightCol.setCellValueFactory(new PropertyValueFactory<>("heightCm"));

        TableColumn<MeasurementEntry, Double> bmiCol = new TableColumn<>("BMI");
        bmiCol.setCellValueFactory(new PropertyValueFactory<>("bmiValue"));

        TableColumn<MeasurementEntry, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(d -> {
            MeasurementEntry me = d.getValue();
            if (me == null) return new SimpleStringProperty("N/A");
            return new SimpleStringProperty(classifyBmi(me.getBmiValue()));
        });

        TableColumn<MeasurementEntry, Void> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(90);
        actionCol.setMaxWidth(110);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");

            {
                deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-cursor: hand; -fx-padding: 3 10;");
                deleteBtn.setOnAction(e -> {
                    MeasurementEntry entry = getTableView().getItems().get(getIndex());
                    if (entry != null && entry.getId() != null) {
                        healthTrackerService.deleteMeasurement(entry.getId());
                        loadUserData();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setAlignment(Pos.CENTER);
                    setGraphic(deleteBtn);
                }
            }
        });

        table.getColumns().addAll(List.of(dateCol, weightCol, heightCol, bmiCol, catCol, actionCol));

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                updateBmiDisplay(selected.getBmiValue(), classifyBmi(selected.getBmiValue()));
            }
        });

        return table;
    }

    private void handleRecordMeasurement() {
        try {
            double weight = Double.parseDouble(weightField.getText().trim());
            double height = Double.parseDouble(heightField.getText().trim());
            UnitSystem system = unitSystemBox.getValue();

            LocalDateTime timestamp = useCurrentTimeCheck.isSelected()
                    ? LocalDateTime.now()
                    : datePicker.getValue().atTime(LocalDateTime.now().toLocalTime());

            BmiResultDto dto = healthTrackerService.recordMeasurement(
                    activeProfile.getId(),
                    weight,
                    height,
                    system,
                    timestamp
            );

            updateBmiDisplay(dto.bmiValue(), classifyBmi(dto.bmiValue()));
            weightField.clear();
            heightField.clear();
            loadUserData();
        } catch (NumberFormatException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter valid numeric values for height and weight.", ButtonType.OK);
            alert.showAndWait();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to record measurement: " + ex.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void updateBmiDisplay(double bmi, String categoryName) {
        currentBmiValueLabel.setText(String.format("%.2f", bmi));
        currentBmiCategoryLabel.setText(categoryName);
        bmiGaugeView.setBmi(bmi);
    }

    private void loadUserData() {
        List<MeasurementEntry> history = healthTrackerService.getMeasurementHistory(activeProfile.getId());
        historyTable.setItems(FXCollections.observableArrayList(history));

        if (!history.isEmpty()) {
            MeasurementEntry latest = history.getFirst();
            updateBmiDisplay(latest.getBmiValue(), classifyBmi(latest.getBmiValue()));
        } else {
            currentBmiValueLabel.setText("--");
            currentBmiCategoryLabel.setText("NO DATA");
            bmiGaugeView.setBmi(0);
        }

        trendSeries.getData().clear();
        List<MeasurementEntry> trendData = healthTrackerService.getTrendData(activeProfile.getId());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (MeasurementEntry me : trendData) {
            String timeLabel = me.getRecordedAt() != null ? me.getRecordedAt().format(dtf) : "";
            trendSeries.getData().add(new XYChart.Data<>(timeLabel, me.getBmiValue()));
        }
    }
}
