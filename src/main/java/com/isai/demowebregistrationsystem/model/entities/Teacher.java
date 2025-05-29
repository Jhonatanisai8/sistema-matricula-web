package com.isai.demowebregistrationsystem.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "teacher")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teacher_id")
    private Integer teacherId;

    @Column(name = "teacher_code", unique = true, nullable = false, length = 50)
    private String teacherCode;

    @Column(name = "institutional_email", length = 100)
    private String institutionalEmail;

    @Column(name = "primary_specialty", length = 100)
    private String primarySpecialty;

    @Column(name = "secondary_specialty", length = 100)
    private String secondarySpecialty;

    @Column(name = "professional_title", length = 100)
    private String professionalTitle;

    @Column(name = "graduation_university", length = 255)
    private String graduationUniversity;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "base_salary")
    private Double baseSalary;

    @Column(name = "contract_type", length = 50)
    private String contractType;

    @Column(name = "employment_status", length = 50)
    private String employmentStatus;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(name = "cv_url", length = 255)
    private String cvUrl;

    @Column(name = "photo_url", length = 255)
    private String photoUrl;

    @Column(nullable = false)
    private Boolean coordinator;

    // Relación OneToOne con Persona
    //Un Teacher está asociado a una y solo una Person.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", unique = true, nullable = false)
    private Person person;

    @PrePersist
    protected void onCreate() {
        if (this.coordinator == null) this.coordinator = false;
    }
}