package com.isai.demowebregistrationsystem.model.dtos.request;


import jakarta.validation.constraints.*;
import lombok.*;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomRequestDTO {
    private Integer classId;
    @NotBlank(message = "El código de salón no puede estar vacío.")
    @Size(max = 20, message = "El código de salón no puede exceder los 20 caracteres.")
    private String classCode;

    @NotBlank(message = "El nombre de salón no puede estar vacío.")
    @Size(max = 100, message = "El nombre de salón no puede exceder los 100 caracteres.")
    private String className;

    @NotNull(message = "La capacidad no puede ser nula.")
    @Min(value = 1, message = "La capacidad debe ser al menos 1.")
    private Integer capacity;

    @Size(max = 255, message = "La ubicación no puede exceder los 255 caracteres.")
    private String location;
}