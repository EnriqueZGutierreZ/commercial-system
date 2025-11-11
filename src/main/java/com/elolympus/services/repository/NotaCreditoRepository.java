package com.elolympus.services.repository;

import com.elolympus.data.Ventas.NotaCredito;
import com.elolympus.data.Ventas.Factura;
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
public interface NotaCreditoRepository extends JpaRepository<NotaCredito, Long> {

    // Buscar por número de nota de crédito
    Optional<NotaCredito> findByNumeroNota(String numeroNota);

    // Buscar por serie y número
    Optional<NotaCredito> findBySerieAndNumeroNota(String serie, String numeroNota);

    // Buscar por cliente
    List<NotaCredito> findByCliente(Persona cliente);

    // Buscar por estado
    List<NotaCredito> findByEstado(String estado);

    // Buscar por serie
    List<NotaCredito> findBySerie(String serie);

    // Buscar por tipo de nota
    List<NotaCredito> findByTipoNota(String tipoNota);

    // Buscar por rango de fechas
    List<NotaCredito> findByFechaEmisionBetween(Date fechaInicio, Date fechaFin);

    // Buscar por cliente y estado
    List<NotaCredito> findByClienteAndEstado(Persona cliente, String estado);

    // Buscar notas de crédito emitidas
    @Query("SELECT nc FROM NotaCredito nc WHERE nc.estado = 'EMITIDA'")
    List<NotaCredito> findNotasCreditoEmitidas();

    // Buscar notas de crédito aplicadas
    @Query("SELECT nc FROM NotaCredito nc WHERE nc.estado = 'APLICADA'")
    List<NotaCredito> findNotasCreditoAplicadas();

    // Buscar por factura de referencia
    List<NotaCredito> findByFacturaReferencia(Factura factura);

    // Buscar por boleta de referencia
    List<NotaCredito> findByBoletaReferencia(Boleta boleta);

    // Buscar por tipo de documento de referencia
    List<NotaCredito> findByDocumentoReferenciaTipo(String tipoDocumento);

    // Buscar por documento de referencia específico
    @Query("SELECT nc FROM NotaCredito nc WHERE nc.documentoReferenciaTipo = :tipo AND nc.documentoReferenciaSerie = :serie AND nc.documentoReferenciaNumero = :numero")
    List<NotaCredito> findByDocumentoReferencia(@Param("tipo") String tipo, @Param("serie") String serie, @Param("numero") String numero);

    // Contar notas de crédito por estado
    @Query("SELECT COUNT(nc) FROM NotaCredito nc WHERE nc.estado = :estado")
    Long countByEstado(@Param("estado") String estado);

    // Obtener total de notas de crédito en un rango de fechas
    @Query("SELECT SUM(nc.total) FROM NotaCredito nc WHERE nc.fechaEmision BETWEEN :fechaInicio AND :fechaFin AND nc.estado != 'ANULADA'")
    java.math.BigDecimal getTotalNotasCreditoEnRango(@Param("fechaInicio") Date fechaInicio, @Param("fechaFin") Date fechaFin);

    // Buscar por número que contenga el texto (para búsqueda parcial)
    List<NotaCredito> findByNumeroNotaContainingIgnoreCase(String numeroNota);

    // Buscar por serie que contenga el texto
    List<NotaCredito> findBySerieContainingIgnoreCase(String serie);

    // Buscar por nombre de cliente (búsqueda en nombres y apellidos)
    @Query("SELECT nc FROM NotaCredito nc WHERE LOWER(nc.cliente.nombres) LIKE LOWER(CONCAT('%', :nombre, '%')) OR LOWER(nc.cliente.apellidos) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<NotaCredito> findByClienteNombreContaining(@Param("nombre") String nombre);

    // Buscar por motivo que contenga el texto
    List<NotaCredito> findByMotivoContainingIgnoreCase(String motivo);

    // Obtener último número por serie
    @Query("SELECT MAX(nc.numeroNota) FROM NotaCredito nc WHERE nc.serie = :serie")
    String getUltimoNumeroPorSerie(@Param("serie") String serie);

    // Buscar por moneda
    List<NotaCredito> findByMoneda(String moneda);

    // Buscar por tipo de documento
    List<NotaCredito> findByTipoDocumento(String tipoDocumento);

    // Obtener total de IGV en notas de crédito en un rango de fechas
    @Query("SELECT SUM(nc.igv) FROM NotaCredito nc WHERE nc.fechaEmision BETWEEN :fechaInicio AND :fechaFin AND nc.estado != 'ANULADA'")
    java.math.BigDecimal getTotalIgvNotasCreditoEnRango(@Param("fechaInicio") Date fechaInicio, @Param("fechaFin") Date fechaFin);

    // Reporte de notas de crédito por cliente en rango de fechas
    @Query("SELECT nc.cliente, SUM(nc.total) FROM NotaCredito nc WHERE nc.fechaEmision BETWEEN :fechaInicio AND :fechaFin AND nc.estado != 'ANULADA' GROUP BY nc.cliente ORDER BY SUM(nc.total) DESC")
    List<Object[]> getNotasCreditoPorCliente(@Param("fechaInicio") Date fechaInicio, @Param("fechaFin") Date fechaFin);

    // Reporte de notas de crédito por tipo
    @Query("SELECT nc.tipoNota, COUNT(nc), SUM(nc.total) FROM NotaCredito nc WHERE nc.fechaEmision BETWEEN :fechaInicio AND :fechaFin AND nc.estado != 'ANULADA' GROUP BY nc.tipoNota")
    List<Object[]> getReporteNotasCreditoPorTipo(@Param("fechaInicio") Date fechaInicio, @Param("fechaFin") Date fechaFin);

    // Buscar notas de crédito por documento de referencia usando LIKE
    @Query("SELECT nc FROM NotaCredito nc WHERE nc.documentoReferenciaNumero LIKE LOWER(CONCAT('%', :numero, '%'))")
    List<NotaCredito> findByDocumentoReferenciaNumeroContaining(@Param("numero") String numero);

    // Verificar si existe una nota de crédito para un documento específico
    @Query("SELECT COUNT(nc) > 0 FROM NotaCredito nc WHERE nc.documentoReferenciaTipo = :tipo AND nc.documentoReferenciaSerie = :serie AND nc.documentoReferenciaNumero = :numero AND nc.estado != 'ANULADA'")
    boolean existeNotaCreditoParaDocumento(@Param("tipo") String tipo, @Param("serie") String serie, @Param("numero") String numero);

    // Obtener el monto total de notas de crédito aplicadas a una factura específica
    @Query("SELECT SUM(nc.total) FROM NotaCredito nc WHERE nc.facturaReferencia = :factura AND nc.estado = 'APLICADA'")
    java.math.BigDecimal getTotalNotasCreditoAplicadasAFactura(@Param("factura") Factura factura);

    // Obtener el monto total de notas de crédito aplicadas a una boleta específica
    @Query("SELECT SUM(nc.total) FROM NotaCredito nc WHERE nc.boletaReferencia = :boleta AND nc.estado = 'APLICADA'")
    java.math.BigDecimal getTotalNotasCreditoAplicadasABoleta(@Param("boleta") Boleta boleta);
}