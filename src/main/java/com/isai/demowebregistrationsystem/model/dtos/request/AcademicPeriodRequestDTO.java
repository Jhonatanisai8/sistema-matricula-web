package com.isai.demowebregistrationsystem.model.dtos.request;

import lombok.*;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicPeriodRequestDTO {
    private Integer periodId;
    @NotNull(message = "El año académico no puede ser nulo.")
    @Min(value = 2000, message = "El año académico debe ser posterior al 2000.")
    private Integer academicYear;

    @NotBlank(message = "El nombre del periodo no puede estar vacío.")
    @Size(max = 100, message = "El nombre del periodo no puede exceder los 100 caracteres.")
    private String periodName;

    @NotNull(message = "La fecha de inicio no puede ser nula.")
    private LocalDate startDate;

    @NotNull(message = "La fecha de fin no puede ser nula.")
    private LocalDate endDate;

    private Boolean active;
}