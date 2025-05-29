package com.isai.demowebregistrationsystem.model.dtos.response;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponseDTO {
    private Integer studentId;
    private PersonResponseDTO person;
    private TutorResponseDTO primaryTutor;
    private String studentCode;
    private String educationalEmail;
    private String currentGrade;
    private String currentSection;
    private String currentAcademicPeriod;
}