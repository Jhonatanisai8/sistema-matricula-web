package com.isai.demowebregistrationsystem.services;

import com.isai.demowebregistrationsystem.model.dtos.DocenteDTO;
import com.isai.demowebregistrationsystem.model.dtos.DocenteRegistroDTO;

import java.util.List;

public interface DocenteService {
    DocenteDTO registrarDocente(DocenteRegistroDTO docenteRegistroDTO);

    List<DocenteDTO> listarDocentes();
}