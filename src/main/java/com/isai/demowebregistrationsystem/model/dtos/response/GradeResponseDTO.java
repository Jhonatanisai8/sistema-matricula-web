package com.isai.demowebregistrationsystem.model.dtos.response;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeResponseDTO {
    private Integer gradeId;
    private String gradeCode;
    private String gradeName;
    private String description;
}