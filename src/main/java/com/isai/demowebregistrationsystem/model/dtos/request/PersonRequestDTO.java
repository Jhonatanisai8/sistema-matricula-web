package com.isai.demowebregistrationsystem.model.dtos.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonRequestDTO {

    private Integer personId;


    @NotBlank(message = "El DNI no puede estar vacío.")
    @Size(min = 8, max = 8, message = "El DNI debe tener 8 dígitos.")
    @Pattern(regexp = "\\d+", message = "El DNI debe contener solo números.")
    private String nationalId;

    @NotBlank(message = "Los nombres no pueden estar vacíos.")
    @Size(max = 100, message = "Los nombres no pueden exceder los 100 caracteres.")
    private String firstNames;

    @NotBlank(message = "Los apellidos no pueden estar vacíos.")
    @Size(max = 100, message = "Los apellidos no pueden exceder los 100 caracteres.")
    private String lastNames;

    @NotNull(message = "La fecha de nacimiento no puede ser nula.")
    @Past(message = "La fecha de nacimiento debe ser en el pasado.")
    private LocalDate dateOfBirth;

    @NotBlank(message = "El género no puede estar vacío.")
    @Size(max = 1, message = "El género debe ser 'M' o 'F'.")
    private String gender;

    @NotBlank(message = "La dirección no puede estar vacía.")
    @Size(max = 255, message = "La dirección no puede exceder los 255 caracteres.")
    private String address;

    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres.")
    private String phoneNumber;

    @Email(message = "El formato del email personal no es válido.")
    @Size(max = 100, message = "El email personal no puede exceder los 100 caracteres.")
    private String personalEmail;

    @Size(max = 50, message = "El estado civil no puede exceder los 50 caracteres.")
    private String maritalStatus;

    @NotBlank(message = "El tipo de documento no puede estar vacío.")
    @Size(max = 50, message = "El tipo de documento no puede exceder los 50 caracteres.")
    private String documentType;

    @NotNull(message = "El estado activo no puede ser nulo.")
    private Boolean active;


}