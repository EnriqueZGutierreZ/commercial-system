package com.elolympus.services.repository;

import com.elolympus.data.Auxiliar.ConfiguracionSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracionSistemaRepository extends JpaRepository<ConfiguracionSistema, Long> {

    /**
     * Obtiene la configuración activa del sistema.
     * Solo debe existir un registro activo en el sistema.
     */
    @Query("SELECT c FROM ConfiguracionSistema c WHERE c.activo = true")
    Optional<ConfiguracionSistema> findConfiguracionActiva();

    /**
     * Verifica si existe una configuración activa
     */
    @Query("SELECT COUNT(c) > 0 FROM ConfiguracionSistema c WHERE c.activo = true")
    boolean existsConfiguracionActiva();

    /**
     * Busca configuración por usuario que la modificó
     */
    Optional<ConfiguracionSistema> findByUsuarioModificacion(String usuarioModificacion);

}