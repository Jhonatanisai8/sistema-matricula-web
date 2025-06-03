package com.isai.demowebregistrationsystem.services.impl;

import com.isai.demowebregistrationsystem.model.dtos.EstudianteDTO;
import com.isai.demowebregistrationsystem.model.entities.Apoderado;
import com.isai.demowebregistrationsystem.model.entities.Estudiante;
import com.isai.demowebregistrationsystem.model.entities.Persona;
import com.isai.demowebregistrationsystem.model.entities.Usuario;
import com.isai.demowebregistrationsystem.repositorys.ApoderadoRepository;
import com.isai.demowebregistrationsystem.repositorys.EstudianteRepository;
import com.isai.demowebregistrationsystem.repositorys.PersonaRepository;
import com.isai.demowebregistrationsystem.repositorys.UsuarioRepository;
import com.isai.demowebregistrationsystem.services.EstudianteService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


// EstudianteServiceImpl.java
@Service
@RequiredArgsConstructor
public class EstudianteServiceImpl implements EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final ApoderadoRepository apoderadoRepository;
    private final PersonaRepository personaRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;


    @Override
    public Page<Estudiante> listarEstudiantes(String filtro, Pageable pageable) {
        Specification<Estudiante> spec = (root, query, cb) -> {
            if (filtro == null || filtro.trim().isEmpty()) {
                return null;
            }
            //  join explícito INNER para asegurar que funciona bien
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
    public Estudiante registrar(EstudianteDTO estudianteDTO) {
        // --- 1. Crear y guardar la entidad Persona para el Estudiante ---
        Persona personaEstudiante = new Persona();
        personaEstudiante.setDni(estudianteDTO.getDniEstudiante());
        personaEstudiante.setNombres(estudianteDTO.getNombresEstudiante());
        personaEstudiante.setApellidos(estudianteDTO.getApellidosEstudiante());
        personaEstudiante.setFechaNacimiento(estudianteDTO.getFechaNacimientoEstudiante());
        personaEstudiante.setGenero(estudianteDTO.getGeneroEstudiante());
        personaEstudiante.setDireccion(estudianteDTO.getDireccionEstudiante());
        personaEstudiante.setTelefono(estudianteDTO.getTelefonoEstudiante());
        personaEstudiante.setEmailPersonal(estudianteDTO.getEmailPersonalEstudiante());
        personaEstudiante.setEstadoCivil(estudianteDTO.getEstadoCivilEstudiante());
        personaEstudiante.setTipoDocumento(estudianteDTO.getTipoDocumentoEstudiante());
        personaEstudiante = personaRepository.save(personaEstudiante);

        // --- 2. Buscar el Apoderado Principal existente por su DNI ---
        Apoderado apoderadoPrincipal = apoderadoRepository.findByPersonaDni(estudianteDTO.getDniApoderadoPrincipal())
                .orElseThrow(() -> new RuntimeException("Apoderado con DNI " + estudianteDTO.getDniApoderadoPrincipal() + " no encontrado. Por favor, registre al apoderado primero."));

        // --- 4. Cremos y guardamos el usuario ---
        Usuario usuario = new Usuario();
        usuario.setUserName(estudianteDTO.getNombresEstudiante().substring(0, 3).concat(estudianteDTO.getApellidosEstudiante().substring(0, 4)));
        usuario.setPasswordHash(passwordEncoder.encode(estudianteDTO.getNombresEstudiante().substring(0, 3).concat(estudianteDTO.getApellidosEstudiante().substring(0, 4)).concat("2025").toLowerCase()));
        usuario.setRol("ESTUDIANTE");
        usuario.setPersona(personaEstudiante);
        usuarioRepository.save(usuario);


        // --- 3. Crear y guardar la entidad Estudiante ---
        Estudiante estudiante = new Estudiante();
        estudiante.setPersona(personaEstudiante);
        estudiante.setApoderadoPrincipal(apoderadoPrincipal);

        estudiante.setCodigoEstudiante(estudianteDTO.getCodigoEstudiante());
        estudiante.setEmailEducativo(estudianteDTO.getEmailEducativoEstudiante());
        estudiante.setGradoAnterior(estudianteDTO.getGradoAnteriorEstudiante());
        estudiante.setInstitucionProcedencia(estudianteDTO.getInstitucionProcedenciaEstudiante());
        estudiante.setTipoSangre(estudianteDTO.getTipoSangreEstudiante());
        estudiante.setObservacionesMedicas(estudianteDTO.getObservacionesMedicasEstudiante());
        estudiante.setAlergias(estudianteDTO.getAlergiasEstudiante());
        estudiante.setContactoEmergencia(estudianteDTO.getContactoEmergenciaEstudiante());
        estudiante.setTelefonoEmergencia(estudianteDTO.getTelefonoEmergenciaEstudiante());
        estudiante.setSeguroEscolar(estudianteDTO.getSeguroEscolarEstudiante() != null ? estudianteDTO.getSeguroEscolarEstudiante() : false);
        estudiante.setFotoUrl(estudianteDTO.getFotoUrlEstudiante());

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
