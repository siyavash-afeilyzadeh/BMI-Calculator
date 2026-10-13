package com.black.bmitracker.domain.model.entity;

import com.black.bmitracker.domain.model.enums.UnitSystem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "measurement_entries")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    @Column(name = "height_cm", nullable = false)
    private double heightCm;

    @Column(name = "weight_kg", nullable = false)
    private double weightKg;

    @Column(name = "bmi_value", nullable = false)
    private double bmiValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "recorded_unit_system", nullable = false, length = 15)
    private UnitSystem recordedUnitSystem;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;
}
