package com.isai.demowebregistrationsystem.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "student")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer studentId;

    @Column(name = "student_code", unique = true, nullable = false, length = 50)
    private String studentCode;

    @Column(name = "educational_email", length = 100)
    private String educationalEmail;

    @Column(name = "previous_grade", length = 100)
    private String previousGrade;

    @Column(name = "origin_institution", length = 255)
    private String originInstitution;

    @Column(name = "blood_type", length = 10)
    private String bloodType;

    @Column(name = "medical_observations", columnDefinition = "TEXT")
    private String medicalObservations;

    @Column(name = "allergies", length = 255)
    private String allergies;

    @Column(name = "emergency_contact", length = 100)
    private String emergencyContact;

    @Column(name = "emergency_phone_number", length = 20)
    private String emergencyPhoneNumber;

    @Column(name = "school_insurance", nullable = false)
    private Boolean schoolInsurance;

    @Column(name = "photo_url", length = 255)
    private String photoUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_person", unique = true, nullable = false)
    private Person person;

    // ManyToOne relationship with Tutor (a guardian can have multiple students)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", referencedColumnName = "tutor_id")
    private Tutor principalTutor;

    // Constructor to initialize booleans
    @PrePersist
    protected void onCreate() {
        if (this.schoolInsurance == null) this.schoolInsurance = false;
    }
}