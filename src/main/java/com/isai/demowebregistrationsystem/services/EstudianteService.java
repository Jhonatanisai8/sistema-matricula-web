package com.isai.demowebregistrationsystem.services;

import com.isai.demowebregistrationsystem.model.dtos.estudiantes.EstudianteDetalleDTO;
import com.isai.demowebregistrationsystem.model.dtos.estudiantes.EstudianteListadoDTO;
import com.isai.demowebregistrationsystem.model.dtos.estudiantes.EstudianteRegistroDTO;
import com.isai.demowebregistrationsystem.model.dtos.opciones.ApoderadoOptionDTO;
import com.isai.demowebregistrationsystem.model.dtos.opciones.GradoOptionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface EstudianteService {

    Page<EstudianteListadoDTO> listarEstudiantesPaginado(String keyword, Boolean activo, Pageable pageable);

    EstudianteDetalleDTO obtenerEstudianteDetalle(Integer idEstudiante);

    EstudianteRegistroDTO obtenerEstudianteParaEdicion(Integer idEstudiante);
    // Para precargar el formulario de edición

    void guardarEstudiante(EstudianteRegistroDTO estudianteDTO);

    // Crear o actualizar

    void eliminarEstudiante(Integer idEstudiante);
    // Considerar inactivar en lugar de eliminar físicamente


    // Métodos para cargar opciones en los formularios (selects)
    List<ApoderadoOptionDTO> obtenerApoderadosDisponibles();

    List<GradoOptionDTO> obtenerGradosDisponibles();

    Map<String, String> getEstadosCiviles();

    Map<String, String> getGeneros();

    Map<String, String> getTiposDocumento();

    Map<String, String> getTiposSangre();

    Map<String, String> getGradosAnteriores();

}
