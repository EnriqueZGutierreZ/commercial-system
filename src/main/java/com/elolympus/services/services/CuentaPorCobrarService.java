package com.elolympus.services.services;

import com.elolympus.data.Ventas.CuentaPorCobrar;
import com.elolympus.data.Ventas.Factura;
import com.elolympus.data.Ventas.Boleta;
import com.elolympus.data.Administracion.Persona;
import com.elolympus.data.Empresa.Empresa;
import com.elolympus.services.repository.CuentaPorCobrarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CuentaPorCobrarService {

    private final CuentaPorCobrarRepository repository;

    @Autowired
    public CuentaPorCobrarService(CuentaPorCobrarRepository repository) {
        this.repository = repository;
    }

    // =============== OPERACIONES CRUD BÁSICAS ===============

    public List<CuentaPorCobrar> findAll() {
        return repository.findAll();
    }

    public Optional<CuentaPorCobrar> findById(Long id) {
        return repository.findById(id);
    }

    public CuentaPorCobrar save(CuentaPorCobrar cuentaPorCobrar) {
        return repository.save(cuentaPorCobrar);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Page<CuentaPorCobrar> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    // =============== CREACIÓN AUTOMÁTICA DESDE DOCUMENTOS ===============

    /**
     * Crea cuenta por cobrar automáticamente desde una factura
     */
    public CuentaPorCobrar crearDesdeFactura(Factura factura) {
        // Verificar si ya existe
        Optional<CuentaPorCobrar> existente = repository.findByTipoDocumentoAndDocumentoReferenciaId("FACTURA", factura.getId());
        if (existente.isPresent()) {
            return existente.get();
        }

        CuentaPorCobrar cuenta = new CuentaPorCobrar(
            factura.getNumeroFactura(),
            "FACTURA",
            factura.getId(),
            factura.getCliente(),
            factura.getEmpresa(),
            factura.getFechaEmision(),
            factura.getTotal()
        );

        cuenta.setFechaVencimiento(factura.getFechaVencimiento());
        cuenta.setFormaPagoAcordada(factura.getFormaPago());
        cuenta.setPrioridad(determinarPrioridad(factura.getTotal()));

        return save(cuenta);
    }

    /**
     * Crea cuenta por cobrar automáticamente desde una boleta
     */
    public CuentaPorCobrar crearDesdeBoleta(Boleta boleta) {
        // Verificar si ya existe
        Optional<CuentaPorCobrar> existente = repository.findByTipoDocumentoAndDocumentoReferenciaId("BOLETA", boleta.getId());
        if (existente.isPresent()) {
            return existente.get();
        }

        CuentaPorCobrar cuenta = new CuentaPorCobrar(
            boleta.getNumeroBoleta(),
            "BOLETA",
            boleta.getId(),
            boleta.getCliente(),
            boleta.getEmpresa(),
            boleta.getFechaEmision(),
            boleta.getTotal()
        );

        cuenta.setFechaVencimiento(boleta.getFechaVencimiento());
        cuenta.setFormaPagoAcordada(boleta.getFormaPago());
        cuenta.setPrioridad(determinarPrioridad(boleta.getTotal()));

        return save(cuenta);
    }

    private String determinarPrioridad(BigDecimal monto) {
        if (monto.compareTo(new BigDecimal("10000")) >= 0) {
            return "ALTA";
        } else if (monto.compareTo(new BigDecimal("2000")) >= 0) {
            return "MEDIA";
        } else {
            return "BAJA";
        }
    }

    // =============== CONSULTAS ESPECIALIZADAS ===============

    public Optional<CuentaPorCobrar> findByNumeroDocumento(String numeroDocumento) {
        return repository.findByNumeroDocumento(numeroDocumento);
    }

    public List<CuentaPorCobrar> findByCliente(Persona cliente) {
        return repository.findByClienteOrderByFechaVencimientoAsc(cliente);
    }

    public List<CuentaPorCobrar> findByEstado(String estado) {
        return repository.findByEstado(estado);
    }

    public List<CuentaPorCobrar> findCuentasPendientes() {
        return repository.findCuentasPendientes();
    }

    public List<CuentaPorCobrar> findCuentasVencidas() {
        return repository.findCuentasVencidas();
    }

    public List<CuentaPorCobrar> findCuentasPorVencerEnDias(int dias) {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimite = hoy.plusDays(dias);
        
        return repository.findCuentasPorVencer(
            Date.valueOf(hoy), 
            Date.valueOf(fechaLimite)
        );
    }

    // =============== GESTIÓN DE PAGOS ===============

    public void registrarPago(Long cuentaId, BigDecimal montoPago, String observacion) {
        Optional<CuentaPorCobrar> cuentaOpt = findById(cuentaId);
        if (cuentaOpt.isPresent()) {
            CuentaPorCobrar cuenta = cuentaOpt.get();
            cuenta.registrarPago(montoPago, observacion);
            save(cuenta);
        }
    }

    public void registrarContacto(Long cuentaId, String resultado, Date fechaProximoSeguimiento) {
        Optional<CuentaPorCobrar> cuentaOpt = findById(cuentaId);
        if (cuentaOpt.isPresent()) {
            CuentaPorCobrar cuenta = cuentaOpt.get();
            cuenta.registrarContacto(resultado, fechaProximoSeguimiento);
            save(cuenta);
        }
    }

    public void actualizarPrioridad(Long cuentaId, String nuevaPrioridad) {
        Optional<CuentaPorCobrar> cuentaOpt = findById(cuentaId);
        if (cuentaOpt.isPresent()) {
            CuentaPorCobrar cuenta = cuentaOpt.get();
            cuenta.setPrioridad(nuevaPrioridad);
            save(cuenta);
        }
    }

    // =============== REPORTES Y ESTADÍSTICAS ===============

    public BigDecimal getTotalSaldoPendienteByCliente(Persona cliente) {
        return repository.getTotalSaldoPendienteByCliente(cliente);
    }

    public BigDecimal getTotalSaldoPendienteGeneral() {
        return repository.getTotalSaldoPendienteGeneral();
    }

    public BigDecimal getTotalSaldoVencido() {
        return repository.getTotalSaldoVencido();
    }

    public List<Object[]> getEstadisticasAntiguedadSaldos() {
        return repository.getEstadisticasAntiguedadSaldos();
    }

    public List<Object[]> getTopClientesSaldoPendiente() {
        return repository.getTopClientesSaldoPendiente();
    }

    public Long countDocumentosPendientesByCliente(Persona cliente) {
        return repository.countDocumentosPendientesByCliente(cliente);
    }

    // =============== SEGUIMIENTO Y COBRANZA ===============

    public List<CuentaPorCobrar> getCuentasParaSeguimiento(int diasSinContacto) {
        LocalDate fechaLimite = LocalDate.now().minusDays(diasSinContacto);
        return repository.findCuentasParaSeguimiento(Date.valueOf(fechaLimite));
    }

    public List<CuentaPorCobrar> getCuentasConSeguimientoHoy() {
        return repository.findCuentasConSeguimientoHoy(Date.valueOf(LocalDate.now()));
    }

    public List<CuentaPorCobrar> getCuentasOrdenPrioridad() {
        return repository.findCuentasOrdenPrioridad();
    }

    // =============== FILTROS AVANZADOS ===============

    public List<CuentaPorCobrar> buscarConFiltros(Persona cliente, String estado, String tipoDocumento, 
                                                   Date fechaDesde, Date fechaHasta, String rangoVencimiento) {
        return repository.buscarConFiltros(cliente, estado, tipoDocumento, fechaDesde, fechaHasta, rangoVencimiento);
    }

    // =============== DASHBOARD Y KPIs ===============

    public Map<String, Object> getDashboardCuentasPorCobrar() {
        Map<String, Object> dashboard = new HashMap<>();
        
        // Totales generales
        dashboard.put("totalPendiente", getTotalSaldoPendienteGeneral());
        dashboard.put("totalVencido", getTotalSaldoVencido());
        
        // Contadores
        dashboard.put("cuentasPendientes", findCuentasPendientes().size());
        dashboard.put("cuentasVencidas", findCuentasVencidas().size());
        dashboard.put("cuentasPorVencer7Dias", findCuentasPorVencerEnDias(7).size());
        dashboard.put("cuentasParaSeguimiento", getCuentasParaSeguimiento(7).size());
        
        // Estadísticas por rango de vencimiento
        List<Object[]> estadisticasAntiguedad = getEstadisticasAntiguedadSaldos();
        Map<String, BigDecimal> montoPorRango = new HashMap<>();
        Map<String, Long> cantidadPorRango = new HashMap<>();
        
        for (Object[] stat : estadisticasAntiguedad) {
            String rango = (String) stat[0];
            BigDecimal monto = (BigDecimal) stat[1];
            Long cantidad = (Long) stat[2];
            
            montoPorRango.put(rango, monto);
            cantidadPorRango.put(rango, cantidad);
        }
        
        dashboard.put("montoPorRango", montoPorRango);
        dashboard.put("cantidadPorRango", cantidadPorRango);
        
        // Top 5 clientes con mayor deuda
        List<Object[]> topClientes = getTopClientesSaldoPendiente();
        List<Map<String, Object>> topClientesInfo = new ArrayList<>();
        
        for (int i = 0; i < Math.min(5, topClientes.size()); i++) {
            Object[] cliente = topClientes.get(i);
            Map<String, Object> clienteInfo = new HashMap<>();
            Persona p = (Persona) cliente[0];
            clienteInfo.put("cliente", p.getNombres() + " " + p.getApellidos());
            clienteInfo.put("saldoPendiente", (BigDecimal) cliente[1]);
            topClientesInfo.add(clienteInfo);
        }
        
        dashboard.put("topClientes", topClientesInfo);
        
        return dashboard;
    }

    // =============== ALERTAS Y NOTIFICACIONES ===============

    public List<String> getAlertasCuentasPorCobrar() {
        List<String> alertas = new ArrayList<>();
        
        // Cuentas vencidas
        List<CuentaPorCobrar> vencidas = findCuentasVencidas();
        if (!vencidas.isEmpty()) {
            alertas.add("⚠️ Tienes " + vencidas.size() + " cuentas vencidas que requieren seguimiento inmediato");
        }
        
        // Cuentas por vencer en 3 días
        List<CuentaPorCobrar> porVencer = findCuentasPorVencerEnDias(3);
        if (!porVencer.isEmpty()) {
            alertas.add("⏰ " + porVencer.size() + " cuentas vencen en los próximos 3 días");
        }
        
        // Seguimientos programados para hoy
        List<CuentaPorCobrar> seguimientos = getCuentasConSeguimientoHoy();
        if (!seguimientos.isEmpty()) {
            alertas.add("📞 Tienes " + seguimientos.size() + " seguimientos programados para hoy");
        }
        
        // Cuentas sin contacto reciente
        List<CuentaPorCobrar> sinContacto = getCuentasParaSeguimiento(15);
        if (!sinContacto.isEmpty()) {
            alertas.add("📝 " + sinContacto.size() + " cuentas requieren contacto (sin seguimiento en 15+ días)");
        }
        
        return alertas;
    }

    // =============== ACTUALIZACIÓN MASIVA ===============

    @Transactional
    public void actualizarTodosDiasVencimiento() {
        List<CuentaPorCobrar> todasLasCuentas = findCuentasPendientes();
        for (CuentaPorCobrar cuenta : todasLasCuentas) {
            cuenta.calcularDiasVencimiento();
            cuenta.actualizarRangoVencimiento();
            cuenta.actualizarEstado();
        }
        repository.saveAll(todasLasCuentas);
    }

    // =============== EXPORTACIÓN DE DATOS ===============

    public List<Map<String, Object>> exportarCuentasPorCobrar(List<CuentaPorCobrar> cuentas) {
        return cuentas.stream().map(cuenta -> {
            Map<String, Object> map = new HashMap<>();
            map.put("numeroDocumento", cuenta.getNumeroDocumento());
            map.put("tipoDocumento", cuenta.getTipoDocumento());
            map.put("cliente", cuenta.getCliente().getNombres() + " " + cuenta.getCliente().getApellidos());
            map.put("fechaEmision", cuenta.getFechaEmision());
            map.put("fechaVencimiento", cuenta.getFechaVencimiento());
            map.put("montoOriginal", cuenta.getMontoOriginal());
            map.put("montoPagado", cuenta.getMontoPagado());
            map.put("saldoPendiente", cuenta.getSaldoPendiente());
            map.put("estado", cuenta.getEstado());
            map.put("diasVencimiento", cuenta.getDiasVencimiento());
            map.put("rangoVencimiento", cuenta.getRangoVencimiento());
            map.put("prioridad", cuenta.getPrioridad());
            return map;
        }).collect(Collectors.toList());
    }
}