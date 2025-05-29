package com.isai.demowebregistrationsystem.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "grade_evaluation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer evaluationId;

    @Column(name = "evaluation_type", length = 100)
    private String evaluationType;

    @Column(name = "score", nullable = false)
    private Double score;

    @Column(name = "percentage_weight")
    private Double percentageWeight;

    @Column(name = "evaluation_date", nullable = false)
    private LocalDate evaluationDate;

    @Column(name = "observations", length = 255)
    private String observations;

    // Relación ManyToOne con Estudiante
    // Una 'GradeEvaluation' está asociada a un único 'Student'.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Relación ManyToOne con Curso
    // Una 'GradeEvaluation' está asociada a un único 'Course'.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // Relación ManyToOne con PeriodoAcademico
    // Una 'GradeEvaluation' está asociada a un único 'AcademicPeriod'.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_id", nullable = false)
    private AcademicPeriod academicPeriod;

    @PrePersist
    protected void onCreate() {
        if (this.evaluationDate == null) this.evaluationDate = LocalDate.now();
    }
}