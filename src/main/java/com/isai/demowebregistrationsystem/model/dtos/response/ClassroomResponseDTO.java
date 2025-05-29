package com.isai.demowebregistrationsystem.model.dtos.response;


import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassroomResponseDTO {
    private Integer classId;
    private String classCode;
    private String className;
    private Integer capacity;
    private String location;
}