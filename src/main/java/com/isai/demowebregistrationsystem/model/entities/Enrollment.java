package com.isai.demowebregistrationsystem.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "enrollment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment { //matricula

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer enrollmentId;

    @Column(name = "enrollment_number", unique = true, nullable = false, length = 50)
    private String enrollmentNumber;

    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    @Column(name = "enrollment_status", length = 50)
    private String enrollmentStatus;

    @Column(name = "enrollment_fee")
    private Double enrollmentFee;

    @Column(name = "monthly_tuition_amount")
    private Double monthlyTuitionAmount;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @Column(name = "pending_documents", length = 255)
    private String pendingDocuments;

    @Column(name = "documents_complete", nullable = false)
    private Boolean documentsComplete;

    // Relación ManyToOne con Estudiante
    // Una 'Enrollment' está asociada a un único 'Student'.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Relación ManyToOne con Seccion
    // Una 'Enrollment' está asociada a una única 'Section'.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    // Relación ManyToOne con Grado (redundante pero para acceso directo si se desea)
    // Una 'Enrollment' está asociada a un único 'Grade'.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;

    // Relación ManyToOne con PeriodoAcademico (redundante pero para acceso directo si se desea)
    // Una 'Enrollment' está asociada a un único 'AcademicPeriod'.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_id", nullable = false)
    private AcademicPeriod academicPeriod;

    // Relación ManyToOne con Apoderado (quién realizó la matrícula)
    // Una 'Enrollment' está asociada a un único 'Guardian', que es quien realizó la matrícula.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor enrollingTutor;

    @PrePersist
    protected void onCreate() {
        if (this.enrollmentDate == null) this.enrollmentDate = LocalDate.now();
        if (this.documentsComplete == null) this.documentsComplete = false;
    }
}