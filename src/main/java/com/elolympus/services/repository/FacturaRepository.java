package com.elolympus.services.repository;

import com.elolympus.data.Ventas.Factura;
import com.elolympus.data.Administracion.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    // Buscar por número de factura
    Optional<Factura> findByNumeroFactura(String numeroFactura);

    // Buscar por serie y número
    Optional<Factura> findBySerieAndNumeroFactura(String serie, String numeroFactura);

    // Buscar por cliente
    List<Factura> findByCliente(Persona cliente);

    // Buscar por estado
    List<Factura> findByEstado(String estado);

    // Buscar por serie
    List<Factura> findBySerie(String serie);

    // Buscar por rango de fechas
    List<Factura> findByFechaEmisionBetween(Date fechaInicio, Date fechaFin);

    // Buscar por cliente y estado
    List<Factura> findByClienteAndEstado(Persona cliente, String estado);

    // Buscar facturas emitidas
    @Query("SELECT f FROM Factura f WHERE f.estado = 'EMITIDA'")
    List<Factura> findFacturasEmitidas();

    // Buscar facturas vencidas
    @Query("SELECT f FROM Factura f WHERE f.fechaVencimiento < CURRENT_DATE AND f.estado IN ('EMITIDA', 'VENCIDA')")
    List<Factura> findFacturasVencidas();

    // Buscar facturas por vencer (próximos días)
    @Query(value = "SELECT * FROM ventas.factura WHERE fecha_vencimiento >= CURRENT_DATE AND fecha_vencimiento <= CURRENT_DATE + :dias AND estado = 'EMITIDA'", nativeQuery = true)
    List<Factura> findFacturasPorVencer(@Param("dias") int dias);

    // Contar facturas por estado
    @Query("SELECT COUNT(f) FROM Factura f WHERE f.estado = :estado")
    Long countByEstado(@Param("estado") String estado);

    // Obtener total de ventas en un rango de fechas
    @Query("SELECT SUM(f.total) FROM Factura f WHERE f.fechaEmision BETWEEN :fechaInicio AND :fechaFin AND f.estado != 'ANULADA'")
    java.math.BigDecimal getTotalVentasEnRango(@Param("fechaInicio") Date fechaInicio, @Param("fechaFin") Date fechaFin);

    // Buscar facturas por número que contenga el texto (para búsqueda parcial)
    List<Factura> findByNumeroFacturaContainingIgnoreCase(String numeroFactura);

    // Buscar por serie que contenga el texto
    List<Factura> findBySerieContainingIgnoreCase(String serie);

    // Buscar por nombre de cliente (búsqueda en nombres y apellidos)
    @Query("SELECT f FROM Factura f WHERE LOWER(f.cliente.nombres) LIKE LOWER(CONCAT('%', :nombre, '%')) OR LOWER(f.cliente.apellidos) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Factura> findByClienteNombreContaining(@Param("nombre") String nombre);

    // Buscar por tipo de documento
    List<Factura> findByTipoDocumento(String tipoDocumento);

    // Buscar por forma de pago
    List<Factura> findByFormaPago(String formaPago);

    // Buscar por moneda
    List<Factura> findByMoneda(String moneda);

    // Obtener último número por serie
    @Query("SELECT MAX(f.numeroFactura) FROM Factura f WHERE f.serie = :serie")
    String getUltimoNumeroPorSerie(@Param("serie") String serie);

    // Buscar facturas por orden de compra
    Optional<Factura> findByNumeroOrdenCompra(String numeroOrdenCompra);

    // Obtener total de IGV en un rango de fechas
    @Query("SELECT SUM(f.igv) FROM Factura f WHERE f.fechaEmision BETWEEN :fechaInicio AND :fechaFin AND f.estado != 'ANULADA'")
    java.math.BigDecimal getTotalIgvEnRango(@Param("fechaInicio") Date fechaInicio, @Param("fechaFin") Date fechaFin);

    // Buscar facturas con descuentos
    @Query("SELECT f FROM Factura f WHERE f.descuento > 0")
    List<Factura> findFacturasConDescuento();

    // Reporte de ventas por cliente en rango de fechas
    @Query("SELECT f.cliente, SUM(f.total) FROM Factura f WHERE f.fechaEmision BETWEEN :fechaInicio AND :fechaFin AND f.estado != 'ANULADA' GROUP BY f.cliente ORDER BY SUM(f.total) DESC")
    List<Object[]> getVentasPorCliente(@Param("fechaInicio") Date fechaInicio, @Param("fechaFin") Date fechaFin);
}