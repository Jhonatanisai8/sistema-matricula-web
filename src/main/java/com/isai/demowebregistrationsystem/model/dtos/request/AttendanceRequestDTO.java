package com.isai.demowebregistrationsystem.model.dtos.request;


import lombok.*;

import jakarta.validation.constraints.*;

import java.time.LocalDate;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRequestDTO { // AsistenciaRequestDTO -> AttendanceRequestDTO
    private Integer attendanceId;
    @NotNull(message = "La fecha de asistencia no puede ser nula.")
    private LocalDate attendanceDate;

    @NotBlank(message = "El estado de asistencia no puede estar vacío.")
    @Size(max = 20, message = "El estado de asistencia no puede exceder los 20 caracteres.")
    private String attendanceStatus;

    @Size(max = 255, message = "Las observaciones no pueden exceder los 255 caracteres.")
    private String observations;

    @NotNull(message = "El ID del estudiante no puede ser nulo.")
    private Integer studentId;

    @NotNull(message = "El ID del horario no puede ser nulo.")
    private Integer scheduleId;
}