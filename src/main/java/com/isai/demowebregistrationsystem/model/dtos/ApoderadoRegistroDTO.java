package com.isai.demowebregistrationsystem.model.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApoderadoRegistroDTO {

    //campos para la edicion
    private Integer idPersona;
    private Integer idUsuario;
    private Integer idApoderado;

    // --- Campos de Persona ---
    @NotBlank(message = "El DNI no puede estar vacío.")
    @Size(min = 8, max = 20, message = "El DNI debe tener entre 8 y 20 caracteres.")
    private String dni;

    @NotBlank(message = "Los nombres no pueden estar vacíos.")
    @Size(max = 100, message = "Los nombres no pueden exceder los 100 caracteres.")
    private String nombres;

    @NotBlank(message = "Los apellidos no pueden estar vacíos.")
    @Size(max = 100, message = "Los apellidos no pueden exceder los 100 caracteres.")
    private String apellidos;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    @Size(max = 10, message = "El género no puede exceder los 10 caracteres.")
    private String genero;

    @Size(max = 255, message = "La dirección no puede exceder los 255 caracteres.")
    private String direccion;

    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres.")
    private String telefono;

    @Email(message = "Debe ser un formato de correo electrónico válido.")
    @Size(max = 100, message = "El email personal no puede exceder los 100 caracteres.")
    private String emailPersonal;

    @Size(max = 50, message = "El estado civil no puede exceder los 50 caracteres.")
    private String estadoCivil;

    @Size(max = 50, message = "El tipo de documento no puede exceder los 50 caracteres.")
    private String tipoDocumento;

    // --- Campos de Apoderado ---
    @Size(max = 100, message = "La ocupación no puede exceder los 100 caracteres.")
    private String ocupacion;

    @Size(max = 255, message = "El lugar de trabajo no puede exceder los 255 caracteres.")
    private String lugarTrabajo;

    @Size(max = 20, message = "El teléfono de trabajo no puede exceder los 20 caracteres.")
    private String telefonoTrabajo;

    @NotBlank(message = "El parentesco no puede estar vacío.")
    @Size(max = 50, message = "El parentesco no puede exceder los 50 caracteres.")
    private String parentesco;

    @Size(max = 50, message = "El nivel educativo no puede exceder los 50 caracteres.")
    private String nivelEducativo;

    private BigDecimal ingresoMensual;

    @NotNull(message = "Debe indicar si es apoderado principal.")
    private Boolean esPrincipal;

    @NotNull(message = "Debe indicar si está autorizado a recoger.")
    private Boolean autorizadoRecoger;

    @Size(max = 255, message = "La URL de la foto no puede exceder los 255 caracteres.")
    private String fotoUrl;

    @Size(max = 255, message = "La referencia personal no puede exceder los 255 caracteres.")
    private String referenciaPersonal;

    @Size(max = 20, message = "El teléfono de referencia no puede exceder los 20 caracteres.")
    private String telefonoReferencia;

    // --- Campos de Usuario ---
    @NotBlank(message = "El nombre de usuario no puede estar vacío.")
    @Size(min = 5, max = 50, message = "El nombre de usuario debe tener entre 5 y 50 caracteres.")
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía.")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    private String password;

    private String rol; // Por defecto "APODERADO"
}