package com.isai.demowebregistrationsystem.services;

import com.isai.demowebregistrationsystem.model.dtos.ApoderadoDTO;
import com.isai.demowebregistrationsystem.model.dtos.RegistroApoderadoDTO;
import com.isai.demowebregistrationsystem.model.entities.Apoderado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ApoderadoService {

    Page<ApoderadoDTO> buscarApoderados(String dni, Pageable pageable);

    Optional<ApoderadoDTO> buscarApoderadoPorId(Integer idApoderado);

    ApoderadoDTO guardarApoderado(ApoderadoDTO apoderado);

    void eliminarApoderado(Integer idApoderado);

    Apoderado registrarNuevoApoderado(RegistroApoderadoDTO apoderadoDTO);
}
