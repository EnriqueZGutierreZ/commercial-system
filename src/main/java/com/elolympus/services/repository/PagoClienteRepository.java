package com.elolympus.services.repository;

import com.elolympus.data.Ventas.PagoCliente;
import com.elolympus.data.Ventas.CuentaPorCobrar;
import com.elolympus.data.Administracion.Persona;
import com.elolympus.data.Empresa.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoClienteRepository extends JpaRepository<PagoCliente, Long>, JpaSpecificationExecutor<PagoCliente> {

    /**
     * Busca pago por número de recibo
     */
    Optional<PagoCliente> findByNumeroRecibo(String numeroRecibo);

    /**
     * Busca pagos por cuenta por cobrar
     */
    List<PagoCliente> findByCuentaPorCobrar(CuentaPorCobrar cuentaPorCobrar);

    /**
     * Busca pagos por cuenta por cobrar ordenados por fecha
     */
    List<PagoCliente> findByCuentaPorCobrarOrderByFechaPagoDesc(CuentaPorCobrar cuentaPorCobrar);

    /**
     * Busca pagos por cliente
     */
    List<PagoCliente> findByCliente(Persona cliente);

    /**
     * Busca pagos por cliente ordenados por fecha
     */
    List<PagoCliente> findByClienteOrderByFechaPagoDesc(Persona cliente);

    /**
     * Busca pagos por empresa
     */
    List<PagoCliente> findByEmpresa(Empresa empresa);

    /**
     * Busca pagos por estado
     */
    List<PagoCliente> findByEstado(String estado);

    /**
     * Busca pagos por forma de pago
     */
    List<PagoCliente> findByFormaPago(String formaPago);

    /**
     * Busca pagos pendientes de verificación
     */
    @Query("SELECT p FROM PagoCliente p WHERE p.estado = 'REGISTRADO' ORDER BY p.fechaCreacion ASC")
    List<PagoCliente> findPagosPendientesVerificacion();

    /**
     * Busca pagos verificados en un rango de fechas
     */
    @Query("SELECT p FROM PagoCliente p WHERE p.estado = 'VERIFICADO' AND p.fechaPago BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaPago DESC")
    List<PagoCliente> findPagosVerificadosEnRango(@Param("fechaInicio") Date fechaInicio, @Param("fechaFin") Date fechaFin);

    /**
     * Busca pagos por cliente y estado
     */
    List<PagoCliente> findByClienteAndEstado(Persona cliente, String estado);

    /**
     * Busca pagos por tipo de pago
     */
    List<PagoCliente> findByTipoPago(String tipoPago);

    /**
     * Busca cheques pendientes de depósito
     */
    @Query("SELECT p FROM PagoCliente p WHERE p.formaPago = 'CHEQUE' AND (p.fechaDeposito IS NULL OR p.fechaDeposito > :fechaActual) AND p.estado = 'VERIFICADO' ORDER BY p.fechaDeposito ASC")
    List<PagoCliente> findChequesPendientesDeposito(@Param("fechaActual") Date fechaActual);

    /**
     * Busca pagos no conciliados
     */
    @Query("SELECT p FROM PagoCliente p WHERE p.conciliado = false AND p.estado = 'VERIFICADO' AND p.formaPago IN ('TRANSFERENCIA', 'CHEQUE') ORDER BY p.fechaPago DESC")
    List<PagoCliente> findPagosNoConciliados();

    /**
     * Obtiene total de pagos por cliente en un rango de fechas
     */
    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM PagoCliente p WHERE p.cliente = :cliente AND p.fechaPago BETWEEN :fechaInicio AND :fechaFin AND p.estado = 'VERIFICADO'")
    BigDecimal getTotalPagosByClienteEnRango(@Param("cliente") Persona cliente, @Param("fechaInicio") Date fechaInicio, @Param("fechaFin") Date fechaFin);

    /**
     * Obtiene total de pagos diarios
     */
    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM PagoCliente p WHERE p.fechaPago = :fecha AND p.estado = 'VERIFICADO'")
    BigDecimal getTotalPagosDiarios(@Param("fecha") Date fecha);

    /**
     * Obtiene total de pagos por forma de pago en un rango
     */
    @Query("SELECT p.formaPago, COALESCE(SUM(p.monto), 0), COUNT(p) FROM PagoCliente p WHERE p.fechaPago BETWEEN :fechaInicio AND :fechaFin AND p.estado = 'VERIFICADO' GROUP BY p.formaPago")
    List<Object[]> getTotalPagosPorFormaPago(@Param("fechaInicio") Date fechaInicio, @Param("fechaFin") Date fechaFin);

    /**
     * Estadísticas de pagos mensuales
     */
    @Query("SELECT YEAR(p.fechaPago), MONTH(p.fechaPago), COALESCE(SUM(p.monto), 0), COUNT(p) FROM PagoCliente p WHERE p.estado = 'VERIFICADO' GROUP BY YEAR(p.fechaPago), MONTH(p.fechaPago) ORDER BY YEAR(p.fechaPago) DESC, MONTH(p.fechaPago) DESC")
    List<Object[]> getEstadisticasPagosMensuales();

    /**
     * Top clientes que más pagan
     */
    @Query("SELECT p.cliente, COALESCE(SUM(p.monto), 0), COUNT(p) FROM PagoCliente p WHERE p.estado = 'VERIFICADO' AND p.fechaPago BETWEEN :fechaInicio AND :fechaFin GROUP BY p.cliente ORDER BY SUM(p.monto) DESC")
    List<Object[]> getTopClientesPagadores(@Param("fechaInicio") Date fechaInicio, @Param("fechaFin") Date fechaFin);

    /**
     * Busca pagos con filtros avanzados
     */
    @Query("SELECT p FROM PagoCliente p WHERE " +
           "(:cliente IS NULL OR p.cliente = :cliente) AND " +
           "(:estado IS NULL OR p.estado = :estado) AND " +
           "(:formaPago IS NULL OR p.formaPago = :formaPago) AND " +
           "(:fechaDesde IS NULL OR p.fechaPago >= :fechaDesde) AND " +
           "(:fechaHasta IS NULL OR p.fechaPago <= :fechaHasta) AND " +
           "(:tipoPago IS NULL OR p.tipoPago = :tipoPago) AND " +
           "(:conciliado IS NULL OR p.conciliado = :conciliado) " +
           "ORDER BY p.fechaPago DESC")
    List<PagoCliente> buscarConFiltros(
        @Param("cliente") Persona cliente,
        @Param("estado") String estado,
        @Param("formaPago") String formaPago,
        @Param("fechaDesde") Date fechaDesde,
        @Param("fechaHasta") Date fechaHasta,
        @Param("tipoPago") String tipoPago,
        @Param("conciliado") Boolean conciliado
    );

    /**
     * Cuenta pagos pendientes de verificación
     */
    @Query("SELECT COUNT(p) FROM PagoCliente p WHERE p.estado = 'REGISTRADO'")
    Long countPagosPendientesVerificacion();

    /**
     * Obtiene pagos por verificar más antiguos
     */
    @Query("SELECT p FROM PagoCliente p WHERE p.estado = 'REGISTRADO' ORDER BY p.fechaCreacion ASC")
    List<PagoCliente> findPagosPendientesVerificacionMasAntiguos();

    /**
     * Busca pagos por número de operación
     */
    Optional<PagoCliente> findByNumeroOperacion(String numeroOperacion);

    /**
     * Busca pagos por banco y número de operación
     */
    Optional<PagoCliente> findByBancoAndNumeroOperacion(String banco, String numeroOperacion);

    /**
     * Obtiene resumen diario de recaudación
     */
    @Query("SELECT p.fechaPago, p.formaPago, COALESCE(SUM(p.monto), 0), COUNT(p) FROM PagoCliente p WHERE p.estado = 'VERIFICADO' AND p.fechaPago BETWEEN :fechaInicio AND :fechaFin GROUP BY p.fechaPago, p.formaPago ORDER BY p.fechaPago DESC, p.formaPago")
    List<Object[]> getResumenDiarioRecaudacion(@Param("fechaInicio") Date fechaInicio, @Param("fechaFin") Date fechaFin);

    /**
     * Verifica duplicados de pagos
     */
    @Query("SELECT COUNT(p) > 1 FROM PagoCliente p WHERE p.cliente = :cliente AND p.monto = :monto AND p.fechaPago = :fechaPago AND p.formaPago = :formaPago AND p.estado != 'RECHAZADO'")
    boolean existePagoDuplicado(@Param("cliente") Persona cliente, @Param("monto") BigDecimal monto, @Param("fechaPago") Date fechaPago, @Param("formaPago") String formaPago);

    /**
     * Busca pagos rechazados para análisis
     */
    @Query("SELECT p FROM PagoCliente p WHERE p.estado = 'RECHAZADO' ORDER BY p.fechaVerificacion DESC")
    List<PagoCliente> findPagosRechazados();
}