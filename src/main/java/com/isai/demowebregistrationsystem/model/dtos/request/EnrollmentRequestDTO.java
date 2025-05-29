package com.isai.demowebregistrationsystem.model.dtos.request;


import lombok.*;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentRequestDTO { // MatriculaRequestDTO -> EnrollmentRequestDTO
    private Integer enrollmentId;
    @NotBlank(message = "El número de matrícula no puede estar vacío.")
    @Size(max = 50, message = "El número de matrícula no puede exceder los 50 caracteres.")
    private String enrollmentNumber;

    @NotNull(message = "La fecha de matrícula no puede ser nula.")
    private LocalDate enrollmentDate;

    @NotNull(message = "El monto de matrícula no puede ser nulo.")
    private BigDecimal enrollmentFee;

    @NotNull(message = "El monto de pensión no puede ser nulo.")
    private BigDecimal tuitionFee;

    @NotBlank(message = "La modalidad de pago no puede estar vacía.")
    @Size(max = 50, message = "La modalidad de pago no puede exceder los 50 caracteres.")
    private String paymentMethod;

    @Size(max = 255, message = "Los documentos pendientes no pueden exceder los 255 caracteres.")
    private String pendingDocuments;

    @NotBlank(message = "El estado de la matrícula no puede estar vacío.")
    @Size(max = 50, message = "El estado de la matrícula no puede exceder los 50 caracteres.")
    private String enrollmentStatus;

    @NotNull(message = "El ID del estudiante no puede ser nulo.")
    private Integer studentId;

    @NotNull(message = "El ID del apoderado que realiza la matrícula no puede ser nulo.")
    private Integer enrollingGuardianId;

    @NotNull(message = "El ID de la sección no puede ser nulo.")
    private Integer sectionId;

    @NotNull(message = "El ID del grado no puede ser nulo.")
    private Integer gradeId;

    @NotNull(message = "El ID del periodo académico no puede ser nulo.")
    private Integer academicPeriodId;
}