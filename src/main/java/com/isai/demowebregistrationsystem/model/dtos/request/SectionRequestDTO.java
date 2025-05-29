package com.isai.demowebregistrationsystem.model.dtos.request;


import lombok.*;

import jakarta.validation.constraints.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionRequestDTO {
    private Integer sectionId;
    @NotBlank(message = "El nombre de la sección no puede estar vacío.")
    @Size(max = 50, message = "El nombre de la sección no puede exceder los 50 caracteres.")
    private String sectionName;

    @NotNull(message = "La capacidad máxima no puede ser nula.")
    @Min(value = 1, message = "La capacidad máxima debe ser al menos 1.")
    private Integer maxCapacity;

    @NotNull(message = "El ID del grado no puede ser nulo.")
    private Integer gradeId;

    @NotNull(message = "El ID del periodo académico no puede ser nulo.")
    private Integer academicPeriodId;

    private Integer homeroomTeacherId;
}