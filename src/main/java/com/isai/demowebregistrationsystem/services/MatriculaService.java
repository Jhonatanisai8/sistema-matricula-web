package com.isai.demowebregistrationsystem.services;

import com.isai.demowebregistrationsystem.model.dtos.estudiantes.MatriculaGestionDTO;
import com.isai.demowebregistrationsystem.model.dtos.opciones.PeriodoAcademicoOptionDTO;
import com.isai.demowebregistrationsystem.model.dtos.opciones.SeccionOptionDTO;

import java.util.List;
import java.util.Map;

public interface MatriculaService {

    MatriculaGestionDTO obtenerMatriculaParaGestion(Integer idEstudiante, Integer idPeriodoAcademico); // Para editar o crear una nueva

    void guardarMatricula(MatriculaGestionDTO matriculaDTO);

    List<PeriodoAcademicoOptionDTO> obtenerPeriodosAcademicosDisponibles();

    List<SeccionOptionDTO> obtenerSeccionesPorGradoYPeriodo(Integer idGrado, Integer idPeriodo); // Para carga dinámica

    Map<String, String> getEstadosMatricula();

    Map<String, String> getModalidadesPago();
}
