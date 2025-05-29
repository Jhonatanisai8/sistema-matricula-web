package com.isai.demowebregistrationsystem.model.dtos.request;

import lombok.*;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherRequestDTO {
    private Integer teacherId;
    private Integer personId;
    private String teacherCode;
    private String institutionalEmail;
    private String specialty;
}