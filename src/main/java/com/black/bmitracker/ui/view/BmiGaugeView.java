package com.black.bmitracker.ui.view;

import com.black.bmitracker.domain.model.enums.BmiCategory;
import com.black.bmitracker.domain.service.BmiCategoryClassifier;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class BmiGaugeView extends VBox {

    private final Canvas canvas;
    private final BmiCategoryClassifier classifier = new BmiCategoryClassifier();
    private double currentBmi = 0.0;

    public BmiGaugeView() {
        setAlignment(Pos.CENTER);
        this.canvas = new Canvas(320, 180);
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

        double cx = 160.0;
        double cy = 135.0;
        double radius = 100.0;
        double strokeWidth = 16.0;

        gc.setLineWidth(strokeWidth);

        // WHO 6-Class Spectrum Mapping (Angles: 180 deg to 0 deg)
        // 1. Underweight (< 18.5) -> Arc: 180 down to 146 deg (34 deg span)
        gc.setStroke(Color.web("#38bdf8"));
        gc.strokeArc(cx - radius, cy - radius, radius * 2, radius * 2, 146, 34, ArcType.OPEN);

        // 2. Normal (18.5 - 25.0) -> Arc: 146 down to 108 deg (38 deg span)
        gc.setStroke(Color.web("#22c55e"));
        gc.strokeArc(cx - radius, cy - radius, radius * 2, radius * 2, 108, 38, ArcType.OPEN);

        // 3. Overweight (25.0 - 30.0) -> Arc: 108 down to 80 deg (28 deg span)
        gc.setStroke(Color.web("#eab308"));
        gc.strokeArc(cx - radius, cy - radius, radius * 2, radius * 2, 80, 28, ArcType.OPEN);

        // 4. Obese Class I (30.0 - 35.0) -> Arc: 80 down to 52 deg (28 deg span)
        gc.setStroke(Color.web("#f97316"));
        gc.strokeArc(cx - radius, cy - radius, radius * 2, radius * 2, 52, 28, ArcType.OPEN);

        // 5. Obese Class II (35.0 - 40.0) -> Arc: 52 down to 24 deg (28 deg span)
        gc.setStroke(Color.web("#ef4444"));
        gc.strokeArc(cx - radius, cy - radius, radius * 2, radius * 2, 24, 28, ArcType.OPEN);

        // 6. Obese Class III (>= 40.0) -> Arc: 24 down to 0 deg (24 deg span)
        gc.setStroke(Color.web("#991b1b"));
        gc.strokeArc(cx - radius, cy - radius, radius * 2, radius * 2, 0, 24, ArcType.OPEN);

        // Map BMI (12.0 to 45.0) to Needle Angle (180 deg to 0 deg)
        double needleBmi = currentBmi <= 0.0 ? 22.0 : Math.max(12.0, Math.min(45.0, currentBmi));
        double angleDeg = 180.0 - ((needleBmi - 12.0) / (45.0 - 12.0) * 180.0);
        double angleRad = Math.toRadians(angleDeg);

        double needleLength = radius - 15;
        double nx = cx + needleLength * Math.cos(angleRad);
        double ny = cy - needleLength * Math.sin(angleRad);

        // Needle
        gc.setStroke(Color.web("#f8fafc"));
        gc.setLineWidth(3.0);
        gc.strokeLine(cx, cy, nx, ny);

        // Needle Base Pivot
        gc.setFill(Color.web("#6366f1"));
        gc.fillOval(cx - 7, cy - 7, 14, 14);

        // Labels
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        gc.setFill(Color.web("#f1f5f9"));

        if (currentBmi > 0.0) {
            BmiCategory category = classifier.classify(currentBmi);
            gc.fillText(String.format("BMI: %.2f  •  %s", currentBmi, classifier.getDisplayName(category)), cx, cy + 28);
        } else {
            gc.fillText("Select or Record BMI", cx, cy + 28);
        }
    }
}
