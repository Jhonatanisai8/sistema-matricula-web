package com.isai.demowebregistrationsystem.model.dtos.response;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherAssignmentResponseDTO { // AsignacionDocenteResponseDTO -> TeacherAssignmentResponseDTO
    private Integer assignmentId;
    private TeacherResponseDTO teacher;
    private CourseResponseDTO course;
    private GradeResponseDTO grade;
    private AcademicPeriodResponseDTO academicPeriod;
}