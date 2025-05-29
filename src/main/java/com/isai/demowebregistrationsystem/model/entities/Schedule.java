package com.isai.demowebregistrationsystem.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "schedule")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Schedule { //  Horario

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scheduleId;

    @Column(name = "day_of_week", nullable = false, length = 20)
    private String dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "class_type", length = 50)
    private String classType;

    @Column(name = "observations", length = 255)
    private String observations;

    @Column(nullable = false)
    private Boolean active;

    // Relación ManyToOne con Curso
    // Un 'Schedule' está asociado a un único 'Course'.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_curso", nullable = false)
    private Course course;

    // Relación ManyToOne con Docente
    // Un 'Schedule' está asociado a un único 'Teacher'.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_docente", nullable = false)
    private Teacher teacher;

    // Relación ManyToOne con Salon
    // Un 'Schedule' está asociado a un único 'Classroom'.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_salon", nullable = false)
    private Classroom classroom;

    // Relación ManyToOne con Seccion
    // Un 'Schedule' está asociado a una única 'Section'.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    // Relación ManyToOne con Grado (redundante pero para acceso directo si se desea)
    // Un 'Schedule' está asociado a un único 'Grade'.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;

    // Relación ManyToOne con PeriodoAcademico (redundante pero para acceso directo si se desea)
    // Un 'Schedule' está asociado a un único 'AcademicPeriod'.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_id", nullable = false)
    private AcademicPeriod academicPeriod;

    @PrePersist
    protected void onCreate() {
        if (this.active == null) this.active = true;
    }
}