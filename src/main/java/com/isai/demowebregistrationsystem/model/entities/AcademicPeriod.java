package com.isai.demowebregistrationsystem.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "academic_period")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcademicPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer periodId;

    @Column(name = "academic_year", nullable = false)
    private Integer academicYear;

    @Column(name = "period_name", nullable = false, length = 100)
    private String periodName;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "status", length = 50)
    private String status;

    @Column(nullable = false)
    private Boolean active;

    @PrePersist
    protected void onCreate() {
        if (this.active == null) this.active = true;
    }
}