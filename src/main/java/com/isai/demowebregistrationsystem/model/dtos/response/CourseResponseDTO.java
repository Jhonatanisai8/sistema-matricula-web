package com.isai.demowebregistrationsystem.model.dtos.response;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponseDTO {
    private Integer courseId;
    private String courseCode;
    private String courseName;
    private Integer credits;
    private String description;
}
