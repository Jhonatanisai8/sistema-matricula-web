package com.isai.demowebregistrationsystem.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "section", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"section_name", "grade_id", "period_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sectionId;

    @Column(name = "section_name", nullable = false, length = 10)
    private String sectionName;

    // Relación ManyToOne con Grado
    // Una 'Section' está asociada a un único 'Grade', permitiendo que un grado tenga múltiples secciones.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;

    // Relación ManyToOne con PeriodoAcademico
    // Una 'Section' está asociada a un único 'AcademicPeriod', indicando en qué período académico se imparte.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_id", nullable = false)
    private AcademicPeriod academicPeriod;

    // Relación ManyToOne con Docente (puede ser null)
    // Una 'Section' puede tener un 'Teacher' asignado como tutor, pero un docente puede ser tutor de varias secciones.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_tutor_id", referencedColumnName = "teacher_id")
    private Teacher tutorTeacher;
}