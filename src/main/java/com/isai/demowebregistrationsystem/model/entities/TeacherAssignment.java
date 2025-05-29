package com.isai.demowebregistrationsystem.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "teacher_assignment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherAssignment { //asignacion docente

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer assignmentId;

    @Column(name = "assignment_date", nullable = false)
    private LocalDate assignmentDate;

    @Column(name = "assignment_status", length = 50)
    private String assignmentStatus;

    @Column(name = "observations", length = 255)
    private String observations;

    @Column(name = "is_main_teacher", nullable = false)
    private Boolean isMainTeacher;

    // Relación ManyToOne con Docente
    // Una 'TeacherAssignment' está asociada a un único 'Teacher'.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    // Relación ManyToOne con Curso
    // Una 'TeacherAssignment' está asociada a un único 'Course'.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // Relación ManyToOne con Grado
    // Una 'TeacherAssignment' está asociada a un único 'Grade'.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;

    // Relación ManyToOne con PeriodoAcademico
    // Una 'TeacherAssignment' está asociada a un único 'AcademicPeriod'.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_id", nullable = false)
    private AcademicPeriod academicPeriod;

    @PrePersist
    protected void onCreate() {
        if (this.assignmentDate == null) this.assignmentDate = LocalDate.now();
        if (this.isMainTeacher == null) this.isMainTeacher = false;
    }
}