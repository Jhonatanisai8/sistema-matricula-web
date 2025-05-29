package com.isai.demowebregistrationsystem.model.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDTO {

    private Integer userId;

    @NotBlank(message = "El nombre de usuario no puede estar vacío.")
    @Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre 4 y 50 caracteres.")
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía.")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres.")
    private String password;

    @NotBlank(message = "El rol no puede estar vacío.")
    @Pattern(regexp = "ADMIN|APODERADO|DOCENTE|ESTUDIANTE", message = "Rol no válido.")
    private String role;

    @NotNull(message = "El estado activo no puede ser nulo.")
    private Boolean active;


    @NotNull(message = "El ID de la persona asociada no puede ser nulo.")
    private Integer personId;


}