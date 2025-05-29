package com.isai.demowebregistrationsystem.model.dtos.response;


import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionResponseDTO {
    private Integer sectionId;
    private String sectionName;
    private Integer maxCapacity;
    private GradeResponseDTO grade;
    private AcademicPeriodResponseDTO academicPeriod;
    private TeacherResponseDTO homeroomTeacher;
    private Integer enrolledStudents;
}