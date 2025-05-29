package com.isai.demowebregistrationsystem.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "role", nullable = false, length = 50)
    private String role;

    @Column(name = "last_access")
    private LocalDateTime lastAccess;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "failed_attempts", nullable = false)
    private Integer failedAttempts;

    // Relación OneToOne con Persona
    // Un 'User' está asociado a una única 'Person'
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_person", unique = true, nullable = false)
    private Person person;

    @PrePersist
    protected void onCreate() {
        this.creationDate = LocalDateTime.now();
        if (this.active == null) this.active = true;
        if (this.failedAttempts == null) this.failedAttempts = 0;
    }

    @PreUpdate
    protected void onUpdate() {

    }
}