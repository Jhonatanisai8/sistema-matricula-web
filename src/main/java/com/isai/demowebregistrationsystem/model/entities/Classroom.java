package com.isai.demowebregistrationsystem.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "SALON")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer classroomId;

    @Column(name = "classroom_code", unique = true, nullable = false, length = 20)
    private String classroomCode;

    @Column(name = "classroom_name", nullable = false, length = 100)
    private String classroomName;

    @Column(name = "maximum_capacity")
    private Integer maximumCapacity;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "floor", length = 50)
    private String floor;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "available_resources", length = 255)
    private String availableResources;

    @Column(name = "has_projector", nullable = false)
    private Boolean hasProjector;

    @Column(name = "has_air_conditioning", nullable = false)
    private Boolean hasAirConditioning;

    @Column(nullable = false)
    private Boolean active;

    @PrePersist
    protected void onCreate() {
        if (this.hasProjector == null) this.hasProjector = false;
        if (this.hasAirConditioning == null) this.hasAirConditioning = false;
        if (this.active == null) this.active = true;
    }
}