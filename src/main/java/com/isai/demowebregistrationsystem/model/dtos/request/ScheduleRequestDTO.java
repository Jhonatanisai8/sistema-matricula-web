package com.isai.demowebregistrationsystem.model.dtos.request;


import lombok.*;

import jakarta.validation.constraints.*;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleRequestDTO { // HorarioRequestDTO -> ScheduleRequestDTO
    private Integer scheduleId;
    @NotBlank(message = "El día de la semana no puede estar vacío.")
    @Size(max = 20, message = "El día de la semana no puede exceder los 20 caracteres.")
    private String dayOfWeek;

    @NotNull(message = "La hora de inicio no puede ser nula.")
    private LocalTime startTime;

    @NotNull(message = "La hora de fin no puede ser nula.")
    private LocalTime endTime;

    @NotNull(message = "El ID del curso no puede ser nulo.")
    private Integer courseId;

    @NotNull(message = "El ID del docente no puede ser nulo.")
    private Integer teacherId;

    @NotNull(message = "El ID del salón no puede ser nulo.")
    private Integer classroomId;

    @NotNull(message = "El ID de la sección no puede ser nulo.")
    private Integer sectionId;
}