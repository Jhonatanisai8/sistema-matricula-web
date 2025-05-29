package com.isai.demowebregistrationsystem.model.dtos.response;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentResponseDTO { // MatriculaResponseDTO -> EnrollmentResponseDTO
    private Integer enrollmentId;
    private String enrollmentNumber;
    private LocalDate enrollmentDate;
    private BigDecimal enrollmentFee;
    private BigDecimal tuitionFee;
    private String paymentMethod;
    private String pendingDocuments;
    private String enrollmentStatus;
    private StudentResponseDTO student;
    private TutorResponseDTO enrollingTutor;
    private SectionResponseDTO section;
    private GradeResponseDTO grade;
    private AcademicPeriodResponseDTO academicPeriod;
}