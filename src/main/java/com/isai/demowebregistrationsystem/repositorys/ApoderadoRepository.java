package com.isai.demowebregistrationsystem.repositorys;

import com.isai.demowebregistrationsystem.model.entities.Apoderado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApoderadoRepository extends JpaRepository<Apoderado, Integer> {
    Optional<Apoderado> findByPersonaDni(String dni);

    //metodos para la busqueda

    //busca por nombres y apellidos
    @Query("SELECT a FROM Apoderado a JOIN a.persona p WHERE " +
            "LOWER(p.nombres) LIKE LOWER(CONCAT('%', :nombreApellido, '%')) OR " +
            "LOWER(p.apellidos) LIKE LOWER(CONCAT('%', :nombreApellido, '%'))")
    List<Apoderado> findByPersonaNombresOrApellidosContainingIgnoreCase(@Param("nombreApellido") String nombreApellido);

    //por nombres y apellidos
    @Query("SELECT a FROM Apoderado a JOIN a.persona p WHERE " +
            "(LOWER(p.nombres) LIKE LOWER(CONCAT('%', :nombres, '%')) AND LOWER(p.apellidos) LIKE LOWER(CONCAT('%', :apellidos, '%')))")
    List<Apoderado> findByPersonaNombresContainingIgnoreCaseAndApellidosContainingIgnoreCase(@Param("nombres") String nombres, @Param("apellidos") String apellidos);

    //busqueda por nombres , apellidos o dni
    @Query("SELECT a FROM Apoderado a JOIN a.persona p WHERE " +
            "LOWER(p.nombres) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.apellidos) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.dni) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Apoderado> searchApoderados(@Param("searchTerm") String searchTerm);
}
