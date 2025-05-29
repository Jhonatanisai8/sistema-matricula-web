package com.isai.demowebregistrationsystem.model.dtos.response;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponseDTO { // CalificacionResponseDTO -> GradeResponseDTO
    private Integer ratingId;
    private String evaluationType;
    private BigDecimal scoreValue;
    private String comments;
    private StudentResponseDTO student;
    private CourseResponseDTO course;
    private AcademicPeriodResponseDTO academicPeriod;
    private TeacherAssignmentResponseDTO teacherAssignment;
}