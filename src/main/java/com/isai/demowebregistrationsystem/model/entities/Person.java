package com.isai.demowebregistrationsystem.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "person")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private Integer personId;

    @Column(unique = true, nullable = false, length = 20)
    private String nationalId;

    @Column(nullable = false, length = 100)
    private String firstNames;

    @Column(nullable = false, length = 100)
    private String lastNames;

    private LocalDate dateOfBirth;

    @Column(length = 10)
    private String gender;

    @Column(length = 255)
    private String address;

    @Column(length = 20)
    private String phoneNumber;

    @Column(name = "personal_email", length = 100)
    private String personalEmail;

    @Column(name = "marital_status", length = 50)
    private String maritalStatus;

    @Column(name = "document_type", length = 50)
    private String documentType;

    @Column(name = "registration_date", nullable = false, updatable = false)
    private LocalDateTime registrationDate;

    @Column(name = "last_update_date", nullable = false)
    private LocalDateTime lastUpdateDate;

    @Column(nullable = false)
    private Boolean active;

    @PrePersist
    protected void onCreate() {
        this.registrationDate = LocalDateTime.now();
        this.lastUpdateDate = LocalDateTime.now();
        if (this.active == null) {
            this.active = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdateDate = LocalDateTime.now();
    }
}