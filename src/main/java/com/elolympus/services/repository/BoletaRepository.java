package com.elolympus.services.repository;

import com.elolympus.data.Ventas.Boleta;
import com.elolympus.data.Administracion.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface BoletaRepository extends JpaRepository<Boleta, Long> {

    // Buscar por número de boleta
    Optional<Boleta> findByNumeroBoleta(String numeroBoleta);

    // Buscar por cliente
    List<Boleta> findByCliente(Persona cliente);

    // Buscar por estado
    List<Boleta> findByEstado(String estado);

    // Buscar por rango de fechas
    List<Boleta> findByFechaEmisionBetween(Date fechaInicio, Date fechaFin);

    // Buscar por cliente y estado
    List<Boleta> findByClienteAndEstado(Persona cliente, String estado);

    // Buscar boletas pendientes
    @Query("SELECT b FROM Boleta b WHERE b.estado = 'PENDIENTE'")
    List<Boleta> findBoletasPendientes();

    // Buscar boletas vencidas
    @Query("SELECT b FROM Boleta b WHERE b.fechaVencimiento < CURRENT_DATE AND b.estado = 'PENDIENTE'")
    List<Boleta> findBoletasVencidas();

    // Contar boletas por estado
    @Query("SELECT COUNT(b) FROM Boleta b WHERE b.estado = :estado")
    Long countByEstado(@Param("estado") String estado);

    // Obtener total de ventas en un rango de fechas
    @Query("SELECT SUM(b.total) FROM Boleta b WHERE b.fechaEmision BETWEEN :fechaInicio AND :fechaFin AND b.estado != 'ANULADA'")
    java.math.BigDecimal getTotalVentasEnRango(@Param("fechaInicio") Date fechaInicio, @Param("fechaFin") Date fechaFin);

    // Buscar boletas por número que contenga el texto (para búsqueda parcial)
    List<Boleta> findByNumeroBoletaContainingIgnoreCase(String numeroBoleta);

    // Buscar por nombre de cliente (búsqueda en nombres y apellidos)
    @Query("SELECT b FROM Boleta b WHERE LOWER(b.cliente.nombres) LIKE LOWER(CONCAT('%', :nombre, '%')) OR LOWER(b.cliente.apellidos) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Boleta> findByClienteNombreContaining(@Param("nombre") String nombre);
}