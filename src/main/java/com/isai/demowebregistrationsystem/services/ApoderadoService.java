package com.isai.demowebregistrationsystem.services;


import com.isai.demowebregistrationsystem.model.dtos.ApoderadoDTO;
import com.isai.demowebregistrationsystem.model.dtos.ApoderadoRegistroDTO;
import com.isai.demowebregistrationsystem.model.entities.Apoderado;

import java.util.List;

// ApoderadoService.java
public interface ApoderadoService {
    List<ApoderadoDTO> listarApoderados();

    ApoderadoDTO mapApoderadoToDTO(Apoderado apoderado);

    Apoderado registrarApoderado(ApoderadoRegistroDTO apoderadoRegistroDTO);

    //metodo para las ediciones
    ApoderadoRegistroDTO obtenerApoderadoParaEditar(Integer idApoderado);

    Apoderado actualizarApoderado(ApoderadoRegistroDTO apoderado);

}
