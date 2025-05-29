package com.isai.demowebregistrationsystem.model.dtos.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonResponseDTO {
    private Integer personId;
    private String nationalId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String phoneNumber;
    private String personalEmail;
    private String maritalStatus;
    private String documentType;
    private Boolean active;
}