package com.isai.demowebregistrationsystem.repositorys;

import com.isai.demowebregistrationsystem.model.entities.Persona;
import com.isai.demowebregistrationsystem.model.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUserName(String userName);

    Optional<Usuario> findByPersona(Persona persona);

    @Query("SELECT u FROM Usuario u JOIN FETCH u.persona p WHERE " +
            "LOWER(u.userName) LIKE LOWER(CONCAT('%', :terminoBusqueda, '%')) OR " +
            "LOWER(u.rol) LIKE LOWER(CONCAT('%', :terminoBusqueda, '%')) OR " +
            "LOWER(p.dni) LIKE LOWER(CONCAT('%', :terminoBusqueda, '%')) OR " +
            "LOWER(p.nombres) LIKE LOWER(CONCAT('%', :terminoBusqueda, '%')) OR " +
            "LOWER(p.apellidos) LIKE LOWER(CONCAT('%', :terminoBusqueda, '%'))")
    List<Usuario> searchUsuarios(@org.springframework.data.repository.query.Param("terminoBusqueda") String terminoBusqueda);

}
