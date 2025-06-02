package com.isai.demowebregistrationsystem.model.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    @NotNull(message = "El ID de usuario es requerido para la edición.")
    private Integer idUsuario;
    private Integer idPersona;

    @NotBlank(message = "El nombre de usuario no puede estar vacío.")
    @Size(min = 5, max = 50, message = "El nombre de usuario debe tener entre 5 y 50 caracteres.")
    private String userName;

    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres si se proporciona.")
    private String password;

    @NotBlank(message = "El rol no puede estar vacío.")
    private String rol;

    private LocalDateTime ultimoAcceso;
    @NotNull(message = "El estado activo no puede estar vacío.")
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private Integer intentosFallidos;


    private String dniPersona;
    private String nombresPersona;
    private String apellidosPersona;
}