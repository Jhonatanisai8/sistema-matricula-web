package com.isai.demowebregistrationsystem.model.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime; // Aunque para el DTO de registro no se usa en la vista, se mantiene por si acaso

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudianteDTO {

    // --- Datos de la Persona del Estudiante ---
    // Mapean a la entidad Persona
    private String dniEstudiante;
    private String nombresEstudiante;
    private String apellidosEstudiante;
    @DateTimeFormat(pattern = "yyyy-MM-dd") // Para que Spring pueda convertir String a LocalDate del formulario
    private LocalDate fechaNacimientoEstudiante;
    private String generoEstudiante;
    private String direccionEstudiante;
    private String telefonoEstudiante;
    private String emailPersonalEstudiante;
    private String estadoCivilEstudiante;
    private String tipoDocumentoEstudiante;

    // --- Datos Específicos del Estudiante ---
    // Mapean a la entidad Estudiante
    private String codigoEstudiante;
    private String emailEducativoEstudiante;
    private String gradoAnteriorEstudiante;
    private String institucionProcedenciaEstudiante;
    private String tipoSangreEstudiante;
    private String observacionesMedicasEstudiante;
    private String alergiasEstudiante;
    private String contactoEmergenciaEstudiante;
    private String telefonoEmergenciaEstudiante;
    private Boolean seguroEscolarEstudiante;
    private String fotoUrlEstudiante;

    // --- Referencia al Apoderado Principal ---
    // Para buscar un Apoderado existente por su DNI.
    private String dniApoderadoPrincipal;
    // Podrías añadir un campo para el ID del Apoderado si prefieres que se seleccione por ID
    // private Integer idApoderadoPrincipal;

    // Notas:
    // - Los campos de auditoría (fecha_registro, fecha_actualizacion, activo) se manejan en las entidades con @PrePersist/@PreUpdate.
    // - No se incluye idPersona, idApoderado, idEstudiante porque son generados automáticamente.
}