package com.isai.demowebregistrationsystem.model.dtos.request;


import lombok.*;

import jakarta.validation.constraints.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRequestDTO {
    private Integer courseId;
    @NotBlank(message = "El código de curso no puede estar vacío.")
    @Size(max = 20, message = "El código de curso no puede exceder los 20 caracteres.")
    private String courseCode;

    @NotBlank(message = "El nombre de curso no puede estar vacío.")
    @Size(max = 100, message = "El nombre de curso no puede exceder los 100 caracteres.")
    private String courseName;

    @NotNull(message = "Los créditos no pueden ser nulos.")
    private Integer credits;

    @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres.")
    private String description;
}