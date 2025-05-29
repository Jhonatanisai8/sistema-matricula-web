package com.isai.demowebregistrationsystem.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tutor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tutor_id")
    private Integer tutorId;

    @Column(name = "occupation", length = 100)
    private String occupation;

    @Column(name = "work_location", length = 255)
    private String workLocation;

    @Column(name = "work_phone_number", length = 20)
    private String workPhoneNumber;

    @Column(name = "relationship", length = 50)
    private String relationship;

    @Column(name = "education_level", length = 50)
    private String educationLevel;

    @Column(name = "monthly_income")
    private Double monthlyIncome;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary;

    @Column(name = "authorized_to_pick_up", nullable = false)
    private Boolean authorizedToPickUp;

    @Column(name = "photo_url", length = 255)
    private String photoUrl;

    @Column(name = "personal_reference", length = 255)
    private String personalReference;

    @Column(name = "reference_phone_number", length = 20)
    private String referencePhoneNumber;

    // OneToOne relationship with Person
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", unique = true, nullable = false)
    private Person person;

    @PrePersist
    protected void onCreate() {
        if (this.isPrimary == null) this.isPrimary = false;
        if (this.authorizedToPickUp == null) this.authorizedToPickUp = false;
    }
}