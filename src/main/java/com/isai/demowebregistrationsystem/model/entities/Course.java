package com.isai.demowebregistrationsystem.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "course")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseId;

    @Column(name = "course_code", unique = true, nullable = false, length = 20)
    private String courseCode;

    @Column(name = "course_name", nullable = false, length = 100)
    private String courseName;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "weekly_hours")
    private Integer weeklyHours;

    @Column(name = "credits")
    private Integer credits;

    @Column(name = "knowledge_area", length = 100)
    private String knowledgeArea;

    @Column(name = "is_mandatory", nullable = false)
    private Boolean isMandatory;

    @Column(name = "prerequisites", length = 255)
    private String prerequisites;

    @Column(name = "competencies", columnDefinition = "TEXT")
    private String competencies;

    @Column(nullable = false)
    private Boolean active;

    // Relación ManyToOne con Grado
    // Un 'Course' está asociado a un único 'Grade', permitiendo que un grado tenga múltiples cursos.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;

    @PrePersist
    protected void onCreate() {
        if (this.isMandatory == null) this.isMandatory = true;
        if (this.active == null) this.active = true;
    }
}