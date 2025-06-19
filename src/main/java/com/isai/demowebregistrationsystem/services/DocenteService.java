package com.isai.demowebregistrationsystem.services;

import com.isai.demowebregistrationsystem.model.dtos.registroInicioSesion.RegistroDocenteDTO;
import com.isai.demowebregistrationsystem.model.entities.Docente;

import java.util.List;

public interface DocenteService {
    DocenteDTO registrarDocente(DocenteRegistroDTO docenteRegistroDTO);

    List<DocenteDTO> listarDocentes();

    Docente registrarNuevoDocente(RegistroDocenteDTO docenteRegistroDTO);
}