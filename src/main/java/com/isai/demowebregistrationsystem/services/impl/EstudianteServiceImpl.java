package com.isai.demowebregistrationsystem.services.impl;

import com.isai.demowebregistrationsystem.model.entities.Estudiante;
import com.isai.demowebregistrationsystem.model.entities.Persona;
import com.isai.demowebregistrationsystem.repositorys.EstudianteRepository;
import com.isai.demowebregistrationsystem.services.EstudianteService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


// EstudianteServiceImpl.java
@Service
@RequiredArgsConstructor
public class EstudianteServiceImpl implements EstudianteService {

    private final EstudianteRepository estudianteRepository;

    @Override
    public Page<Estudiante> listarEstudiantes(String filtro, Pageable pageable) {
        Specification<Estudiante> spec = (root, query, cb) -> {
            if (filtro == null || filtro.trim().isEmpty()) {
                return null;
            }
            // Usa join explícito INNER para asegurar que funciona bien
            Join<Estudiante, Persona> personaJoin = root.join("persona", JoinType.INNER);

            String filtroLike = "%" + filtro.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(personaJoin.get("nombres")), filtroLike),
                    cb.like(cb.lower(personaJoin.get("apellidos")), filtroLike),
                    cb.like(cb.lower(root.get("codigoEstudiante")), filtroLike)
            );
        };
        return estudianteRepository.findAll(spec, pageable);
    }


    @Override
    public Estudiante obtenerPorId(Integer id) {
        return estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
    }

    @Override
    public Estudiante registrar(Estudiante estudiante) {
        return estudianteRepository.save(estudiante);
    }

    @Override
    public Estudiante actualizar(Integer id, Estudiante estudianteActualizado) {
        Estudiante existente = obtenerPorId(id);
        estudianteActualizado.setIdEstudiante(existente.getIdEstudiante());
        return estudianteRepository.save(estudianteActualizado);
    }

    @Override
    public void eliminar(Integer id) {
        estudianteRepository.deleteById(id);
    }
}
