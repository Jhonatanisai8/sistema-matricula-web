package com.isai.demowebregistrationsystem.model.dtos.request;


import lombok.*;

import jakarta.validation.constraints.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherAssignmentRequestDTO { // AsignacionDocenteRequestDTO -> TeacherAssignmentRequestDTO
    private Integer assignmentId;
    @NotNull(message = "El ID del docente no puede ser nulo.")
    private Integer teacherId;

    @NotNull(message = "El ID del curso no puede ser nulo.")
    private Integer courseId;

    @NotNull(message = "El ID del grado no puede ser nulo.")
    private Integer gradeId;

    @NotNull(message = "El ID del periodo académico no puede ser nulo.")
    private Integer academicPeriodId;
}
