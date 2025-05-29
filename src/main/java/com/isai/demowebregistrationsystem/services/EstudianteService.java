package com.isai.demowebregistrationsystem.services;

import com.isai.demowebregistrationsystem.model.dtos.EstudianteDTO;
import com.isai.demowebregistrationsystem.model.entities.Estudiante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// EstudianteService.java
public interface EstudianteService {
    Page<Estudiante> listarEstudiantes(String filtro, Pageable pageable);

    Estudiante obtenerPorId(Integer id);

    Estudiante registrar(EstudianteDTO estudianteDTO);

    Estudiante actualizar(Integer id, Estudiante estudiante);

    void eliminar(Integer id);
}
