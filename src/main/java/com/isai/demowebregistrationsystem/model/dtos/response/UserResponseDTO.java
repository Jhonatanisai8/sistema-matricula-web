package com.isai.demowebregistrationsystem.model.dtos.response;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Integer userId;
    private String username;
    private String role;
    private Boolean active;
    private PersonResponseDTO person;
}