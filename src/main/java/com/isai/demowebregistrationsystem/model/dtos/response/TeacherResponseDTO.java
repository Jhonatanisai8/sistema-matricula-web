package com.isai.demowebregistrationsystem.model.dtos.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeacherResponseDTO {
    private Integer teacherId;
    private PersonResponseDTO person;
    private String teacherCode;
    private String institutionalEmail;
    private String specialty;
}