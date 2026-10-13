package com.black.bmitracker.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class BmiGaugeView extends Pane {

    private final Canvas canvas;
    private double currentBmi = 0.0;

    public BmiGaugeView() {
        this.canvas = new Canvas(280, 160);
        getChildren().add(canvas);
        drawGauge();
    }

    public void setBmi(double bmi) {
        this.currentBmi = bmi;
        drawGauge();
    }

    private void drawGauge() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double cx = 140;
        double cy = 135;
        double radius = 100;
        double strokeWidth = 16;

        gc.setLineWidth(strokeWidth);

        // Underweight: < 18.5 (180 deg to 135 deg)
        gc.setStroke(Color.web("#38bdf8"));
        gc.strokeArc(cx - radius, cy - radius, radius * 2, radius * 2, 135, 45, ArcType.OPEN);

        // Normal: 18.5 - 25.0 (135 deg to 75 deg)
        gc.setStroke(Color.web("#22c55e"));
        gc.strokeArc(cx - radius, cy - radius, radius * 2, radius * 2, 75, 60, ArcType.OPEN);

        // Overweight: 25.0 - 30.0 (75 deg to 35 deg)
        gc.setStroke(Color.web("#f59e0b"));
        gc.strokeArc(cx - radius, cy - radius, radius * 2, radius * 2, 35, 40, ArcType.OPEN);

        // Obese: >= 30.0 (35 deg to 0 deg)
        gc.setStroke(Color.web("#ef4444"));
        gc.strokeArc(cx - radius, cy - radius, radius * 2, radius * 2, 0, 35, ArcType.OPEN);

        // Draw Needle
        double clampedBmi = Math.max(12.0, Math.min(42.0, currentBmi == 0.0 ? 21.7 : currentBmi));
        // Linear angle mapping: BMI 12 = 180 deg, BMI 42 = 0 deg
        double angleRad = Math.toRadians(180.0 - ((clampedBmi - 12.0) / 30.0 * 180.0));

        double needleLength = radius - 15;
        double nx = cx + needleLength * Math.cos(angleRad);
        double ny = cy - needleLength * Math.sin(angleRad);

        gc.setStroke(Color.web("#e2e8f0"));
        gc.setLineWidth(3);
        gc.strokeLine(cx, cy, nx, ny);

        // Center Pivot
        gc.setFill(Color.web("#6366f1"));
        gc.fillOval(cx - 7, cy - 7, 14, 14);

        // Value text underneath
        gc.setFill(Color.web("#f8fafc"));
        gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(String.format("BMI: %.2f", currentBmi), cx, cy + 20);
    }
}
