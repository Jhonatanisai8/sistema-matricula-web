package com.isai.demowebregistrationsystem.model.dtos.request;


import jakarta.validation.constraints.*;
import lombok.*;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeRequestDTO {
    private Integer gradeId;
    @NotBlank(message = "El código de grado no puede estar vacío.")
    @Size(max = 20, message = "El código de grado no puede exceder los 20 caracteres.")
    private String gradeCode;

    @NotBlank(message = "El nombre de grado no puede estar vacío.")
    @Size(max = 100, message = "El nombre de grado no puede exceder los 100 caracteres.")
    private String gradeName;

    @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres.")
    private String description;
}