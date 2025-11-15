package com.elolympus.services.repository;

import com.elolympus.data.Administracion.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaRepository extends
        JpaRepository<Persona, Long>,
        JpaSpecificationExecutor<Persona> {
        
    List<Persona> findByActivoTrue();
    
    // Query existente para búsqueda con filtros
    @Query("SELECT p FROM Persona p WHERE " +
           "(:dni IS NULL OR :dni = '' OR CAST(p.num_documento AS string) LIKE %:dni%) AND " +
           "(:nombres IS NULL OR :nombres = '' OR LOWER(p.nombres) LIKE LOWER(CONCAT('%', :nombres, '%'))) AND " +
           "(:apellidos IS NULL OR :apellidos = '' OR LOWER(p.apellidos) LIKE LOWER(CONCAT('%', :apellidos, '%'))) AND " +
           "p.activo = true")
    List<Persona> numDocumnetoNombresApellidosActivosContainsIgnoreCase(
        @Param("dni") String dni,
        @Param("nombres") String nombres,
        @Param("apellidos") String apellidos
    );
    
    // Query para encontrar por ID con dirección cargada
    @Query("SELECT p FROM Persona p LEFT JOIN FETCH p.direccion WHERE p.id = :id")
    Optional<Persona> findByIdWithDireccion(@Param("id") Long id);
    
    // Query optimizada para evitar N+1 cuando se necesita cargar direcciones
    @Query("SELECT p FROM Persona p " +
           "LEFT JOIN FETCH p.direccion d " +
           "WHERE " +
           "(:dni IS NULL OR :dni = '' OR CAST(p.num_documento AS string) LIKE %:dni%) AND " +
           "(:nombres IS NULL OR :nombres = '' OR LOWER(p.nombres) LIKE LOWER(CONCAT('%', :nombres, '%'))) AND " +
           "(:apellidos IS NULL OR :apellidos = '' OR LOWER(p.apellidos) LIKE LOWER(CONCAT('%', :apellidos, '%'))) AND " +
           "p.activo = true")
    List<Persona> findWithFiltersAndDireccion(
        @Param("dni") String dni,
        @Param("nombres") String nombres,
        @Param("apellidos") String apellidos
    );
    
    // Query para encontrar todas las personas activas con dirección (sin filtros)
    @Query("SELECT p FROM Persona p LEFT JOIN FETCH p.direccion WHERE p.activo = true")
    List<Persona> findAllActivosWithDireccion();
}