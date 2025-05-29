package com.isai.demowebregistrationsystem.model.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentRequestDTO {
    private Integer studentId;
    @NotNull(message = "El ID de la persona no puede ser nulo.")
    private Integer Person;

    @NotNull(message = "El ID del apoderado no puede ser nulo.")
    private Integer tutorId;

    @NotBlank(message = "El código de estudiante no puede estar vacío.")
    @Size(max = 50, message = "El código de estudiante no puede exceder los 50 caracteres.")
    private String studentCode;

    @Size(max = 100, message = "El email educativo no puede exceder los 100 caracteres.")
    private String educationalEmail;
}