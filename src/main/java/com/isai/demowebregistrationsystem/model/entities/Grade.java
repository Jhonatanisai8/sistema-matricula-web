package com.isai.demowebregistrationsystem.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "grade")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer gradeId;

    @Column(name = "grade_code", unique = true, nullable = false, length = 20)
    private String gradeCode;

    @Column(name = "grade_name", nullable = false, length = 100)
    private String gradeName;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "level")
    private Integer level;

    @Column(name = "order_in_sequence")
    private Integer orderInSequence;

    @Column(name = "minimum_age")
    private Integer minimumAge;

    @Column(name = "maximum_age")
    private Integer maximumAge;

    @Column(name = "available_slots")
    private Integer availableSlots;

    @Column(name = "monthly_tuition")
    private Double monthlyTuition;

    @Column(nullable = false)
    private Boolean active;

    @PrePersist
    protected void onCreate() {
        if (this.active == null) this.active = true;
    }
}