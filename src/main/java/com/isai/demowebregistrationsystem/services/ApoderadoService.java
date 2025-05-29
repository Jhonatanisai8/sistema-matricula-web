package com.isai.demowebregistrationsystem.services;


import com.isai.demowebregistrationsystem.model.dtos.ApoderadoDTO;
import com.isai.demowebregistrationsystem.model.entities.Apoderado;

import java.util.List;

// ApoderadoService.java
public interface ApoderadoService {
    List<ApoderadoDTO> listarApoderados();

    ApoderadoDTO mapApoderadoToDTO(Apoderado apoderado);
}
