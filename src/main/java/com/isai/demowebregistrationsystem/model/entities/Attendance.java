package com.isai.demowebregistrationsystem.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "attendance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer attendanceId;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "observations", length = 255)
    private String observations;

    @Column(name = "arrival_time")
    private LocalTime arrivalTime;

    @Column(name = "justified", nullable = false)
    private Boolean justified;

    // Relación ManyToOne con Estudiante
    // Una 'Attendance' está asociada a un único 'Student'.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Relación ManyToOne con Horario
    // Una 'Attendance' está asociada a un único 'Schedule'.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @PrePersist
    protected void onCreate() {
        if (this.justified == null) this.justified = false;
    }
}