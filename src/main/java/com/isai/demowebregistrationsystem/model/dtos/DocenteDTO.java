package com.isai.demowebregistrationsystem.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocenteDTO {
    private Integer idDocente;
    private String codigoDocente;
    private String nombresCompletos;
    private String dni;
    private String emailInstitucional;
    private String especialidadPrincipal;
    private String tituloProfesional;
    private LocalDate fechaContratacion;
    private BigDecimal salarioBase;
    private Boolean coordinador;
    private String username;
    private String passwordGenerada;
}