package com.elolympus.services.services;

import com.elolympus.data.Ventas.PagoCliente;
import com.elolympus.data.Ventas.CuentaPorCobrar;
import com.elolympus.data.Administracion.Persona;
import com.elolympus.services.repository.PagoClienteRepository;
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
public class PagoClienteService {

    private final PagoClienteRepository pagoRepository;
    private final CuentaPorCobrarRepository cuentaRepository;

    @Autowired
    public PagoClienteService(PagoClienteRepository pagoRepository, CuentaPorCobrarRepository cuentaRepository) {
        this.pagoRepository = pagoRepository;
        this.cuentaRepository = cuentaRepository;
    }

    // =============== OPERACIONES CRUD BÁSICAS ===============

    public List<PagoCliente> findAll() {
        return pagoRepository.findAll();
    }

    public Optional<PagoCliente> findById(Long id) {
        return pagoRepository.findById(id);
    }

    public PagoCliente save(PagoCliente pagoCliente) {
        return pagoRepository.save(pagoCliente);
    }

    public void deleteById(Long id) {
        pagoRepository.deleteById(id);
    }

    public Page<PagoCliente> findAll(Pageable pageable) {
        return pagoRepository.findAll(pageable);
    }

    // =============== REGISTRO DE PAGOS ===============

    /**
     * Registra un nuevo pago y actualiza la cuenta por cobrar
     */
    @Transactional
    public PagoCliente registrarPago(Long cuentaPorCobrarId, BigDecimal monto, String formaPago, 
                                    String numeroRecibo, String numeroOperacion, String banco, 
                                    Date fechaDeposito, String observaciones, String usuarioRegistro) {
        
        // Verificar que la cuenta por cobrar existe
        Optional<CuentaPorCobrar> cuentaOpt = cuentaRepository.findById(cuentaPorCobrarId);
        if (!cuentaOpt.isPresent()) {
            throw new IllegalArgumentException("La cuenta por cobrar no existe");
        }

        CuentaPorCobrar cuenta = cuentaOpt.get();
        
        // Validar que el monto no exceda el saldo pendiente
        if (monto.compareTo(cuenta.getSaldoPendiente()) > 0) {
            throw new IllegalArgumentException("El monto del pago no puede exceder el saldo pendiente");
        }

        // Verificar duplicados
        if (pagoRepository.existePagoDuplicado(cuenta.getCliente(), monto, new Date(System.currentTimeMillis()), formaPago)) {
            throw new IllegalArgumentException("Ya existe un pago similar registrado para este cliente");
        }

        // Crear el pago
        PagoCliente pago = new PagoCliente(cuenta, monto, formaPago, numeroRecibo);
        pago.setNumeroOperacion(numeroOperacion);
        pago.setBanco(banco);
        pago.setFechaDeposito(fechaDeposito);
        pago.setObservaciones(observaciones);
        pago.setUsuarioRegistro(usuarioRegistro);

        // Guardar el pago
        PagoCliente pagoGuardado = save(pago);

        // Actualizar la cuenta por cobrar
        cuenta.registrarPago(monto, "Pago registrado con recibo: " + numeroRecibo);
        cuentaRepository.save(cuenta);

        return pagoGuardado;
    }

    /**
     * Verifica un pago registrado
     */
    @Transactional
    public void verificarPago(Long pagoId, String usuario, String observacion) {
        Optional<PagoCliente> pagoOpt = findById(pagoId);
        if (!pagoOpt.isPresent()) {
            throw new IllegalArgumentException("El pago no existe");
        }

        PagoCliente pago = pagoOpt.get();
        if (!"REGISTRADO".equals(pago.getEstado())) {
            throw new IllegalArgumentException("Solo se pueden verificar pagos en estado REGISTRADO");
        }

        pago.verificarPago(usuario, observacion);
        save(pago);
    }

    /**
     * Rechaza un pago registrado
     */
    @Transactional
    public void rechazarPago(Long pagoId, String usuario, String motivo) {
        Optional<PagoCliente> pagoOpt = findById(pagoId);
        if (!pagoOpt.isPresent()) {
            throw new IllegalArgumentException("El pago no existe");
        }

        PagoCliente pago = pagoOpt.get();
        if (!"REGISTRADO".equals(pago.getEstado())) {
            throw new IllegalArgumentException("Solo se pueden rechazar pagos en estado REGISTRADO");
        }

        // Revertir el pago en la cuenta por cobrar
        CuentaPorCobrar cuenta = pago.getCuentaPorCobrar();
        BigDecimal montoActual = cuenta.getMontoPagado();
        cuenta.setMontoPagado(montoActual.subtract(pago.getMonto()));
        cuenta.actualizarCalculos();
        cuentaRepository.save(cuenta);

        // Rechazar el pago
        pago.rechazarPago(usuario, motivo);
        save(pago);
    }

    /**
     * Concilia un pago con el banco
     */
    public void conciliarPago(Long pagoId, String usuario) {
        Optional<PagoCliente> pagoOpt = findById(pagoId);
        if (pagoOpt.isPresent()) {
            PagoCliente pago = pagoOpt.get();
            pago.conciliar(usuario);
            save(pago);
        }
    }

    // =============== CONSULTAS ESPECIALIZADAS ===============

    public Optional<PagoCliente> findByNumeroRecibo(String numeroRecibo) {
        return pagoRepository.findByNumeroRecibo(numeroRecibo);
    }

    public List<PagoCliente> findByCliente(Persona cliente) {
        return pagoRepository.findByClienteOrderByFechaPagoDesc(cliente);
    }

    public List<PagoCliente> findByCuentaPorCobrar(CuentaPorCobrar cuenta) {
        return pagoRepository.findByCuentaPorCobrarOrderByFechaPagoDesc(cuenta);
    }

    public List<PagoCliente> findByEstado(String estado) {
        return pagoRepository.findByEstado(estado);
    }

    public List<PagoCliente> findPagosPendientesVerificacion() {
        return pagoRepository.findPagosPendientesVerificacion();
    }

    public List<PagoCliente> findChequesPendientesDeposito() {
        return pagoRepository.findChequesPendientesDeposito(Date.valueOf(LocalDate.now()));
    }

    public List<PagoCliente> findPagosNoConciliados() {
        return pagoRepository.findPagosNoConciliados();
    }

    // =============== REPORTES Y ESTADÍSTICAS ===============

    public BigDecimal getTotalPagosByClienteEnRango(Persona cliente, Date fechaInicio, Date fechaFin) {
        return pagoRepository.getTotalPagosByClienteEnRango(cliente, fechaInicio, fechaFin);
    }

    public BigDecimal getTotalPagosDiarios(Date fecha) {
        return pagoRepository.getTotalPagosDiarios(fecha);
    }

    public List<Object[]> getTotalPagosPorFormaPago(Date fechaInicio, Date fechaFin) {
        return pagoRepository.getTotalPagosPorFormaPago(fechaInicio, fechaFin);
    }

    public List<Object[]> getEstadisticasPagosMensuales() {
        return pagoRepository.getEstadisticasPagosMensuales();
    }

    public List<Object[]> getTopClientesPagadores(Date fechaInicio, Date fechaFin) {
        return pagoRepository.getTopClientesPagadores(fechaInicio, fechaFin);
    }

    public List<Object[]> getResumenDiarioRecaudacion(Date fechaInicio, Date fechaFin) {
        return pagoRepository.getResumenDiarioRecaudacion(fechaInicio, fechaFin);
    }

    public Long countPagosPendientesVerificacion() {
        return pagoRepository.countPagosPendientesVerificacion();
    }

    // =============== FILTROS AVANZADOS ===============

    public List<PagoCliente> buscarConFiltros(Persona cliente, String estado, String formaPago, 
                                               Date fechaDesde, Date fechaHasta, String tipoPago, Boolean conciliado) {
        return pagoRepository.buscarConFiltros(cliente, estado, formaPago, fechaDesde, fechaHasta, tipoPago, conciliado);
    }

    // =============== DASHBOARD Y KPIs ===============

    public Map<String, Object> getDashboardPagos() {
        Map<String, Object> dashboard = new HashMap<>();
        
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        Date fechaHoy = Date.valueOf(hoy);
        Date fechaInicioMes = Date.valueOf(inicioMes);
        
        // Totales del día
        dashboard.put("recaudacionHoy", getTotalPagosDiarios(fechaHoy));
        
        // Totales del mes
        BigDecimal recaudacionMes = pagoRepository.findPagosVerificadosEnRango(fechaInicioMes, fechaHoy)
            .stream()
            .map(PagoCliente::getMonto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboard.put("recaudacionMes", recaudacionMes);
        
        // Contadores
        dashboard.put("pagosPendientesVerificacion", countPagosPendientesVerificacion());
        dashboard.put("chequesPendientesDeposito", findChequesPendientesDeposito().size());
        dashboard.put("pagosNoConciliados", findPagosNoConciliados().size());
        
        // Estadísticas por forma de pago del mes
        List<Object[]> formasPago = getTotalPagosPorFormaPago(fechaInicioMes, fechaHoy);
        Map<String, BigDecimal> montoPorFormaPago = new HashMap<>();
        Map<String, Long> cantidadPorFormaPago = new HashMap<>();
        
        for (Object[] forma : formasPago) {
            String formaPago = (String) forma[0];
            BigDecimal monto = (BigDecimal) forma[1];
            Long cantidad = (Long) forma[2];
            
            montoPorFormaPago.put(formaPago, monto);
            cantidadPorFormaPago.put(formaPago, cantidad);
        }
        
        dashboard.put("montoPorFormaPago", montoPorFormaPago);
        dashboard.put("cantidadPorFormaPago", cantidadPorFormaPago);
        
        // Top 5 clientes que más pagan este mes
        List<Object[]> topPagadores = getTopClientesPagadores(fechaInicioMes, fechaHoy);
        List<Map<String, Object>> topPagadoresInfo = new ArrayList<>();
        
        for (int i = 0; i < Math.min(5, topPagadores.size()); i++) {
            Object[] pagador = topPagadores.get(i);
            Map<String, Object> pagadorInfo = new HashMap<>();
            Persona p = (Persona) pagador[0];
            pagadorInfo.put("cliente", p.getNombres() + " " + p.getApellidos());
            pagadorInfo.put("totalPagado", (BigDecimal) pagador[1]);
            pagadorInfo.put("cantidadPagos", (Long) pagador[2]);
            topPagadoresInfo.add(pagadorInfo);
        }
        
        dashboard.put("topPagadores", topPagadoresInfo);
        
        return dashboard;
    }

    // =============== ALERTAS Y NOTIFICACIONES ===============

    public List<String> getAlertasPagos() {
        List<String> alertas = new ArrayList<>();
        
        // Pagos pendientes de verificación
        Long pendientesVerificacion = countPagosPendientesVerificacion();
        if (pendientesVerificacion > 0) {
            alertas.add("⚠️ Tienes " + pendientesVerificacion + " pagos pendientes de verificación");
        }
        
        // Cheques pendientes de depósito
        List<PagoCliente> chequesPendientes = findChequesPendientesDeposito();
        if (!chequesPendientes.isEmpty()) {
            alertas.add("🏦 " + chequesPendientes.size() + " cheques pendientes de depósito");
        }
        
        // Pagos no conciliados
        List<PagoCliente> noConciliados = findPagosNoConciliados();
        if (!noConciliados.isEmpty()) {
            alertas.add("📊 " + noConciliados.size() + " pagos pendientes de conciliación bancaria");
        }
        
        // Cheques con fecha de depósito vencida
        long chequesVencidos = chequesPendientes.stream()
            .filter(pago -> pago.getFechaDeposito() != null && 
                           pago.getFechaDeposito().before(Date.valueOf(LocalDate.now())))
            .count();
        
        if (chequesVencidos > 0) {
            alertas.add("🚨 " + chequesVencidos + " cheques con fecha de depósito vencida");
        }
        
        return alertas;
    }

    // =============== GENERACIÓN DE NÚMEROS DE RECIBO ===============

    public String generarNumeroRecibo() {
        // Formato: REC-YYYYMMDD-XXX
        LocalDate hoy = LocalDate.now();
        String fecha = hoy.toString().replace("-", "");
        
        // Buscar el último número del día
        String patron = "REC-" + fecha + "-%";
        List<PagoCliente> pagosHoy = pagoRepository.findAll().stream()
            .filter(pago -> pago.getNumeroRecibo().matches("REC-" + fecha + "-\\d+"))
            .collect(Collectors.toList());
        
        int siguienteNumero = pagosHoy.size() + 1;
        return String.format("REC-%s-%03d", fecha, siguienteNumero);
    }

    // =============== EXPORTACIÓN DE DATOS ===============

    public List<Map<String, Object>> exportarPagos(List<PagoCliente> pagos) {
        return pagos.stream().map(pago -> {
            Map<String, Object> map = new HashMap<>();
            map.put("numeroRecibo", pago.getNumeroRecibo());
            map.put("fechaPago", pago.getFechaPago());
            map.put("cliente", pago.getCliente().getNombres() + " " + pago.getCliente().getApellidos());
            map.put("numeroDocumento", pago.getCuentaPorCobrar().getNumeroDocumento());
            map.put("tipoDocumento", pago.getCuentaPorCobrar().getTipoDocumento());
            map.put("monto", pago.getMonto());
            map.put("formaPago", pago.getFormaPago());
            map.put("numeroOperacion", pago.getNumeroOperacion());
            map.put("banco", pago.getBanco());
            map.put("estado", pago.getEstado());
            map.put("tipoPago", pago.getTipoPago());
            map.put("conciliado", pago.getConciliado());
            map.put("observaciones", pago.getObservaciones());
            return map;
        }).collect(Collectors.toList());
    }

    // =============== VALIDACIONES DE NEGOCIO ===============

    public void validarPago(PagoCliente pago) {
        // Validar que la cuenta por cobrar está activa
        CuentaPorCobrar cuenta = pago.getCuentaPorCobrar();
        if (cuenta == null || cuenta.isPagadoTotal()) {
            throw new IllegalArgumentException("La cuenta por cobrar no está disponible para pagos");
        }

        // Validar monto
        if (pago.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }

        // Validar que no excede el saldo
        if (pago.getMonto().compareTo(cuenta.getSaldoPendiente()) > 0) {
            throw new IllegalArgumentException("El monto excede el saldo pendiente");
        }

        // Validar información requerida según forma de pago
        if (pago.requiresAccountNumber() && 
            (pago.getNumeroOperacion() == null || pago.getNumeroOperacion().trim().isEmpty())) {
            throw new IllegalArgumentException("Se requiere número de operación para " + pago.getFormaPago());
        }

        if (pago.isCheque() && 
            (pago.getBanco() == null || pago.getBanco().trim().isEmpty())) {
            throw new IllegalArgumentException("Se requiere información del banco para cheques");
        }
    }
}