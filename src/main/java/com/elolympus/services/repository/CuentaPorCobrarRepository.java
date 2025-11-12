package com.elolympus.services.repository;

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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaPorCobrarRepository extends JpaRepository<CuentaPorCobrar, Long>, JpaSpecificationExecutor<CuentaPorCobrar> {

    /**
     * Busca cuenta por cobrar por número de documento
     */
    Optional<CuentaPorCobrar> findByNumeroDocumento(String numeroDocumento);

    /**
     * Busca cuentas por cobrar por cliente
     */
    List<CuentaPorCobrar> findByCliente(Persona cliente);

    /**
     * Busca cuentas por cobrar por cliente ordenadas por fecha de vencimiento
     */
    List<CuentaPorCobrar> findByClienteOrderByFechaVencimientoAsc(Persona cliente);

    /**
     * Busca cuentas por cobrar por empresa
     */
    List<CuentaPorCobrar> findByEmpresa(Empresa empresa);

    /**
     * Busca cuentas por cobrar por estado
     */
    List<CuentaPorCobrar> findByEstado(String estado);

    /**
     * Busca cuentas por cobrar pendientes (PENDIENTE o PAGADO_PARCIAL)
     */
    @Query("SELECT c FROM CuentaPorCobrar c WHERE c.estado IN ('PENDIENTE', 'PAGADO_PARCIAL', 'VENCIDO') ORDER BY c.fechaVencimiento ASC")
    List<CuentaPorCobrar> findCuentasPendientes();

    /**
     * Busca cuentas vencidas
     */
    @Query("SELECT c FROM CuentaPorCobrar c WHERE c.diasVencimiento > 0 AND c.estado IN ('PENDIENTE', 'PAGADO_PARCIAL', 'VENCIDO') ORDER BY c.diasVencimiento DESC")
    List<CuentaPorCobrar> findCuentasVencidas();

    /**
     * Busca cuentas por vencer en los próximos X días
     */
    @Query("SELECT c FROM CuentaPorCobrar c WHERE c.fechaVencimiento BETWEEN :fechaInicio AND :fechaFin AND c.estado IN ('PENDIENTE', 'PAGADO_PARCIAL') ORDER BY c.fechaVencimiento ASC")
    List<CuentaPorCobrar> findCuentasPorVencer(@Param("fechaInicio") Date fechaInicio, @Param("fechaFin") Date fechaFin);

    /**
     * Busca cuentas por rango de vencimiento
     */
    List<CuentaPorCobrar> findByRangoVencimiento(String rangoVencimiento);

    /**
     * Busca cuentas por tipo de documento
     */
    List<CuentaPorCobrar> findByTipoDocumento(String tipoDocumento);

    /**
     * Busca cuentas por cliente y estado
     */
    List<CuentaPorCobrar> findByClienteAndEstado(Persona cliente, String estado);

    /**
     * Obtiene total de saldo pendiente por cliente
     */
    @Query("SELECT COALESCE(SUM(c.saldoPendiente), 0) FROM CuentaPorCobrar c WHERE c.cliente = :cliente AND c.estado IN ('PENDIENTE', 'PAGADO_PARCIAL', 'VENCIDO')")
    BigDecimal getTotalSaldoPendienteByCliente(@Param("cliente") Persona cliente);

    /**
     * Obtiene total de saldo pendiente general
     */
    @Query("SELECT COALESCE(SUM(c.saldoPendiente), 0) FROM CuentaPorCobrar c WHERE c.estado IN ('PENDIENTE', 'PAGADO_PARCIAL', 'VENCIDO')")
    BigDecimal getTotalSaldoPendienteGeneral();

    /**
     * Obtiene total de saldo vencido
     */
    @Query("SELECT COALESCE(SUM(c.saldoPendiente), 0) FROM CuentaPorCobrar c WHERE c.diasVencimiento > 0 AND c.estado IN ('PENDIENTE', 'PAGADO_PARCIAL', 'VENCIDO')")
    BigDecimal getTotalSaldoVencido();

    /**
     * Busca cuentas que necesitan seguimiento (sin contacto reciente)
     */
    @Query("SELECT c FROM CuentaPorCobrar c WHERE c.estado IN ('PENDIENTE', 'PAGADO_PARCIAL', 'VENCIDO') AND (c.fechaUltimoContacto IS NULL OR c.fechaUltimoContacto < :fechaLimite) ORDER BY c.diasVencimiento DESC")
    List<CuentaPorCobrar> findCuentasParaSeguimiento(@Param("fechaLimite") Date fechaLimite);

    /**
     * Busca cuentas con seguimiento programado para hoy
     */
    @Query("SELECT c FROM CuentaPorCobrar c WHERE c.fechaProximoSeguimiento = :fecha AND c.estado IN ('PENDIENTE', 'PAGADO_PARCIAL', 'VENCIDO') ORDER BY c.prioridad DESC")
    List<CuentaPorCobrar> findCuentasConSeguimientoHoy(@Param("fecha") Date fecha);

    /**
     * Estadísticas de antigüedad de saldos
     */
    @Query("SELECT c.rangoVencimiento, COALESCE(SUM(c.saldoPendiente), 0), COUNT(c) FROM CuentaPorCobrar c WHERE c.estado IN ('PENDIENTE', 'PAGADO_PARCIAL', 'VENCIDO') GROUP BY c.rangoVencimiento")
    List<Object[]> getEstadisticasAntiguedadSaldos();

    /**
     * Top clientes con mayor saldo pendiente
     */
    @Query("SELECT c.cliente, COALESCE(SUM(c.saldoPendiente), 0) FROM CuentaPorCobrar c WHERE c.estado IN ('PENDIENTE', 'PAGADO_PARCIAL', 'VENCIDO') GROUP BY c.cliente ORDER BY SUM(c.saldoPendiente) DESC")
    List<Object[]> getTopClientesSaldoPendiente();

    /**
     * Busca por referencia de documento
     */
    Optional<CuentaPorCobrar> findByTipoDocumentoAndDocumentoReferenciaId(String tipoDocumento, Long documentoReferenciaId);

    /**
     * Busca cuentas por cliente con filtros
     */
    @Query("SELECT c FROM CuentaPorCobrar c WHERE " +
           "(:cliente IS NULL OR c.cliente = :cliente) AND " +
           "(:estado IS NULL OR c.estado = :estado) AND " +
           "(:tipoDocumento IS NULL OR c.tipoDocumento = :tipoDocumento) AND " +
           "(:fechaDesde IS NULL OR c.fechaEmision >= :fechaDesde) AND " +
           "(:fechaHasta IS NULL OR c.fechaEmision <= :fechaHasta) AND " +
           "(:rangoVencimiento IS NULL OR c.rangoVencimiento = :rangoVencimiento) " +
           "ORDER BY c.fechaVencimiento ASC")
    List<CuentaPorCobrar> buscarConFiltros(
        @Param("cliente") Persona cliente,
        @Param("estado") String estado,
        @Param("tipoDocumento") String tipoDocumento,
        @Param("fechaDesde") Date fechaDesde,
        @Param("fechaHasta") Date fechaHasta,
        @Param("rangoVencimiento") String rangoVencimiento
    );

    /**
     * Cuenta documentos pendientes por cliente
     */
    @Query("SELECT COUNT(c) FROM CuentaPorCobrar c WHERE c.cliente = :cliente AND c.estado IN ('PENDIENTE', 'PAGADO_PARCIAL', 'VENCIDO')")
    Long countDocumentosPendientesByCliente(@Param("cliente") Persona cliente);

    /**
     * Obtiene cuentas con mayor prioridad para seguimiento
     */
    @Query("SELECT c FROM CuentaPorCobrar c WHERE c.estado IN ('PENDIENTE', 'PAGADO_PARCIAL', 'VENCIDO') ORDER BY " +
           "CASE WHEN c.prioridad = 'ALTA' THEN 1 WHEN c.prioridad = 'MEDIA' THEN 2 ELSE 3 END, " +
           "c.diasVencimiento DESC, c.saldoPendiente DESC")
    List<CuentaPorCobrar> findCuentasOrdenPrioridad();
}