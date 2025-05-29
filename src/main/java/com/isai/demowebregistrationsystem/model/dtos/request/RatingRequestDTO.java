package com.isai.demowebregistrationsystem.model.dtos.request;


import lombok.*;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingRequestDTO { // CalificacionRequestDTO -> GradeRequestDTO
    private Integer ratingId;
    @NotNull(message = "El tipo de evaluación no puede ser nulo.")
    @Size(max = 100, message = "El tipo de evaluación no puede exceder los 100 caracteres.")
    private String evaluationType;

    @NotNull(message = "La calificación no puede ser nula.")
    @Min(value = 0, message = "La calificación mínima es 0.")
    @Max(value = 20, message = "La calificación máxima es 20.")
    private BigDecimal scoreValue;

    @Size(max = 255, message = "Los comentarios no pueden exceder los 255 caracteres.")
    private String comments;

    @NotNull(message = "El ID del estudiante no puede ser nulo.")
    private Integer studentId;

    @NotNull(message = "El ID del curso no puede ser nulo.")
    private Integer courseId;

    @NotNull(message = "El ID del periodo académico no puede ser nulo.")
    private Integer academicPeriodId;


    private Integer teacherAssignmentId;
}