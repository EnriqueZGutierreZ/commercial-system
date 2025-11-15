package com.elolympus.services.repository;

import com.elolympus.data.Administracion.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends
        JpaRepository<Usuario, Long>,
        JpaSpecificationExecutor<Usuario> {
        
    List<Usuario> findByActivoTrue();
    
    @Query("SELECT u FROM Usuario u WHERE u.usuario = :usuario")
    Optional<Usuario> findByUsuario(@Param("usuario") String usuario);
    
    // Query optimizada para cargar usuario con relaciones para autenticación
    @Query("SELECT u FROM Usuario u " +
           "LEFT JOIN FETCH u.persona p " +
           "LEFT JOIN FETCH u.rol r " +
           "WHERE u.usuario = :usuario AND u.activo = true")
    Optional<Usuario> findByUsuarioWithRelations(@Param("usuario") String usuario);
    
    // Query optimizada para evitar N+1 en grid de usuarios
    @Query("SELECT u FROM Usuario u " +
           "LEFT JOIN FETCH u.persona p " +
           "LEFT JOIN FETCH u.rol r " +
           "WHERE u.activo = true")
    List<Usuario> findAllActiveWithRelations();
}
