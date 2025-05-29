package com.isai.demowebregistrationsystem.model.dtos.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorRequestDTO {

    private Integer tutorId;

    @NotNull(message = "El ID de la persona no puede ser nulo.")
    private Integer personId;

    @Size(max = 100, message = "La ocupación no puede exceder los 100 caracteres.")
    private String occupation;

    @NotBlank(message = "El parentesco no puede estar vacío.")
    @Size(max = 50, message = "El parentesco no puede exceder los 50 caracteres.")
    private String relationship;

    @NotNull(message = "El campo 'es principal' no puede ser nulo.")
    private Boolean isPrimary;

    @NotNull(message = "El campo 'autorizado a recoger' no puede ser nulo.")
    private Boolean authorizedToPickUp;


}