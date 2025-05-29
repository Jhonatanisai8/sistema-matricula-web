package com.isai.demowebregistrationsystem.model.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorResponseDTO {
    private Integer tutorId;
    private PersonResponseDTO person;
    private String occupation;
    private String relationship;
    private Boolean isPrimary;
    private Boolean authorizedToPickUp;
}