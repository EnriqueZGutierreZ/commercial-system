package com.elolympus.services.services;

import com.elolympus.data.Ventas.Boleta;
import com.elolympus.data.Ventas.BoletaDetalle;
import com.elolympus.data.Administracion.Persona;
import com.elolympus.data.Almacen.Almacen;
import com.elolympus.services.repository.BoletaRepository;
import com.elolympus.services.repository.BoletaDetalleRepository;
import com.elolympus.services.services.StockService;
import com.elolympus.services.services.AlmacenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BoletaService {

    private final BoletaRepository boletaRepository;
    private final BoletaDetalleRepository boletaDetalleRepository;
    private final StockService stockService;
    private final AlmacenService almacenService;

    @Autowired
    public BoletaService(BoletaRepository boletaRepository,
                        BoletaDetalleRepository boletaDetalleRepository,
                        StockService stockService,
                        AlmacenService almacenService) {
        this.boletaRepository = boletaRepository;
        this.boletaDetalleRepository = boletaDetalleRepository;
        this.stockService = stockService;
        this.almacenService = almacenService;
    }

    // CRUD básico
    public List<Boleta> findAll() {
        return boletaRepository.findAll();
    }

    public Optional<Boleta> findById(Long id) {
        return boletaRepository.findById(id);
    }

    public Boleta save(Boleta boleta) {
        // Generar número de boleta automáticamente si no existe
        if (boleta.getNumeroBoleta() == null || boleta.getNumeroBoleta().isEmpty()) {
            boleta.setNumeroBoleta(generateNumeroBoleta());
        }
        
        // Establecer fecha de creación si es nueva
        if (boleta.getId() == null) {
            boleta.setFechaCreacion(new Timestamp(System.currentTimeMillis()));
        }
        
        // Calcular IGV automáticamente si no está establecido
        if (boleta.getSubtotal() != null && boleta.getIgv() == null) {
            boleta.calculateIgvFromSubtotal();
        }
        
        return boletaRepository.save(boleta);
    }

    public Boleta update(Boleta boleta) {
        return boletaRepository.save(boleta);
    }

    public void delete(Boleta boleta) {
        // No eliminar físicamente, mejor anular
        boleta.setEstado("ANULADA");
        boletaRepository.save(boleta);
    }

    public void deleteById(Long id) {
        Optional<Boleta> boleta = findById(id);
        if (boleta.isPresent()) {
            delete(boleta.get());
        }
    }

    // Métodos de búsqueda específicos
    public Optional<Boleta> findByNumeroBoleta(String numeroBoleta) {
        return boletaRepository.findByNumeroBoleta(numeroBoleta);
    }

    public List<Boleta> findByCliente(Persona cliente) {
        return boletaRepository.findByCliente(cliente);
    }

    public List<Boleta> findByEstado(String estado) {
        return boletaRepository.findByEstado(estado);
    }

    public List<Boleta> findByFechaEmisionBetween(Date fechaInicio, Date fechaFin) {
        return boletaRepository.findByFechaEmisionBetween(fechaInicio, fechaFin);
    }

    public List<Boleta> findBoletasPendientes() {
        return boletaRepository.findBoletasPendientes();
    }

    public List<Boleta> findBoletasVencidas() {
        return boletaRepository.findBoletasVencidas();
    }

    // Métodos de búsqueda para filtros
    public List<Boleta> findByNumeroBoletaContaining(String numeroBoleta) {
        return boletaRepository.findByNumeroBoletaContainingIgnoreCase(numeroBoleta);
    }

    public List<Boleta> findByClienteNombreContaining(String nombre) {
        return boletaRepository.findByClienteNombreContaining(nombre);
    }

    // Métodos para cambiar estado
    public Boleta marcarComoPagada(Long boletaId, String formaPago) {
        Optional<Boleta> optionalBoleta = findById(boletaId);
        if (optionalBoleta.isPresent()) {
            Boleta boleta = optionalBoleta.get();
            boleta.setEstado("PAGADA");
            boleta.setFormaPago(formaPago);
            boleta.setFechaPago(Date.valueOf(LocalDate.now()));
            return update(boleta);
        }
        throw new RuntimeException("Boleta no encontrada");
    }

    public Boleta anularBoleta(Long boletaId, String motivo) {
        Optional<Boleta> optionalBoleta = findById(boletaId);
        if (optionalBoleta.isPresent()) {
            Boleta boleta = optionalBoleta.get();
            
            // Reversar movimientos de stock si la boleta estaba pendiente o pagada
            if ("PENDIENTE".equals(boleta.getEstado()) || "PAGADA".equals(boleta.getEstado())) {
                reversarMovimientosStock(boleta);
            }
            
            boleta.setEstado("ANULADA");
            boleta.setObservaciones(motivo);
            return update(boleta);
        }
        throw new RuntimeException("Boleta no encontrada");
    }

    // Métodos de estadísticas
    public Long countByEstado(String estado) {
        return boletaRepository.countByEstado(estado);
    }

    public BigDecimal getTotalVentasEnRango(Date fechaInicio, Date fechaFin) {
        BigDecimal total = boletaRepository.getTotalVentasEnRango(fechaInicio, fechaFin);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getTotalVentasDelDia() {
        Date hoy = Date.valueOf(LocalDate.now());
        return getTotalVentasEnRango(hoy, hoy);
    }

    public BigDecimal getTotalVentasDelMes() {
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        LocalDate finMes = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        return getTotalVentasEnRango(Date.valueOf(inicioMes), Date.valueOf(finMes));
    }

    // Generar número de boleta automático
    private String generateNumeroBoleta() {
        long count = boletaRepository.count() + 1;
        return String.format("B%06d", count);
    }

    // Validar si un número de boleta ya existe
    public boolean existeNumeroBoleta(String numeroBoleta) {
        return boletaRepository.findByNumeroBoleta(numeroBoleta).isPresent();
    }

    // Método para buscar con múltiples criterios
    public List<Boleta> buscarConFiltros(String numeroBoleta, String clienteNombre, String estado, Date fechaInicio, Date fechaFin) {
        List<Boleta> result = findAll();
        
        if (numeroBoleta != null && !numeroBoleta.isEmpty()) {
            result = result.stream()
                    .filter(b -> b.getNumeroBoleta().toLowerCase().contains(numeroBoleta.toLowerCase()))
                    .toList();
        }
        
        if (clienteNombre != null && !clienteNombre.isEmpty()) {
            result = result.stream()
                    .filter(b -> {
                        String nombreCompleto = b.getCliente().getNombres() + " " + b.getCliente().getApellidos();
                        return nombreCompleto.toLowerCase().contains(clienteNombre.toLowerCase());
                    })
                    .toList();
        }
        
        if (estado != null && !estado.isEmpty()) {
            result = result.stream()
                    .filter(b -> b.getEstado().equals(estado))
                    .toList();
        }
        
        if (fechaInicio != null && fechaFin != null) {
            result = result.stream()
                    .filter(b -> !b.getFechaEmision().before(fechaInicio) && !b.getFechaEmision().after(fechaFin))
                    .toList();
        }
        
        return result;
    }

    // MÉTODOS PARA GESTIÓN DE DETALLES Y STOCK

    /**
     * Obtener detalles de una boleta
     */
    public List<BoletaDetalle> getDetallesBoleta(Boleta boleta) {
        return boletaDetalleRepository.findByBoletaOrderById(boleta);
    }

    /**
     * Agregar detalle a una boleta (con validación de stock)
     */
    public BoletaDetalle agregarDetalle(Boleta boleta, BoletaDetalle detalle) {
        // Validar que hay stock disponible
        if (!stockService.validarStockParaVenta(detalle.getProducto(), detalle.getCantidad(), null)) {
            throw new RuntimeException("Stock insuficiente para el producto: " + detalle.getProducto().getNombre());
        }

        detalle.setBoleta(boleta);
        detalle.calculateTotals();
        
        BoletaDetalle detalleGuardado = boletaDetalleRepository.save(detalle);
        
        // Actualizar totales de la boleta
        actualizarTotalesBoleta(boleta);
        
        return detalleGuardado;
    }

    /**
     * Guardar boleta con validación y procesamiento de stock
     */
    public Boleta saveConStock(Boleta boleta) {
        boolean esNueva = boleta.getId() == null;
        
        // Generar número de boleta automáticamente si no existe
        if (boleta.getNumeroBoleta() == null || boleta.getNumeroBoleta().isEmpty()) {
            boleta.setNumeroBoleta(generateNumeroBoleta());
        }
        
        // Establecer fecha de creación si es nueva
        if (esNueva) {
            boleta.setFechaCreacion(new Timestamp(System.currentTimeMillis()));
        }
        
        // Calcular IGV automáticamente si no está establecido
        if (boleta.getSubtotal() != null && boleta.getIgv() == null) {
            boleta.calculateIgvFromSubtotal();
        }
        
        Boleta boletaGuardada = boletaRepository.save(boleta);
        
        // Si es una boleta nueva y está en estado PENDIENTE, procesar movimientos de stock
        if (esNueva && "PENDIENTE".equals(boleta.getEstado())) {
            procesarMovimientosStock(boletaGuardada);
        }
        
        return boletaGuardada;
    }

    /**
     * Procesar boleta completa con detalles (método principal para ventas)
     */
    public Boleta procesarBoletaCompleta(Boleta boleta, List<BoletaDetalle> detalles) {
        // Validar stock para todos los productos
        for (BoletaDetalle detalle : detalles) {
            if (!stockService.validarStockParaVenta(detalle.getProducto(), detalle.getCantidad(), null)) {
                throw new RuntimeException("Stock insuficiente para el producto: " + 
                    detalle.getProducto().getNombre() + ". Stock disponible: " + 
                    stockService.getStockDisponibleTotalProducto(detalle.getProducto()));
            }
        }

        // Calcular totales de la boleta
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal igv = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;

        for (BoletaDetalle detalle : detalles) {
            detalle.calculateTotals();
            subtotal = subtotal.add(detalle.getSubtotal());
            igv = igv.add(detalle.getIgv());
            total = total.add(detalle.getTotal());
        }

        boleta.setSubtotal(subtotal);
        boleta.setIgv(igv);
        boleta.setTotal(total);

        // Guardar boleta
        Boleta boletaGuardada = saveConStock(boleta);

        // Guardar detalles
        for (BoletaDetalle detalle : detalles) {
            detalle.setBoleta(boletaGuardada);
            boletaDetalleRepository.save(detalle);
        }

        // Procesar movimientos de stock si está pendiente
        if ("PENDIENTE".equals(boletaGuardada.getEstado())) {
            procesarMovimientosStockConDetalles(boletaGuardada, detalles);
        }

        return boletaGuardada;
    }

    /**
     * Procesar movimientos de stock para una boleta
     */
    private void procesarMovimientosStock(Boleta boleta) {
        List<BoletaDetalle> detalles = getDetallesBoleta(boleta);
        procesarMovimientosStockConDetalles(boleta, detalles);
    }

    /**
     * Procesar movimientos de stock con lista de detalles
     */
    private void procesarMovimientosStockConDetalles(Boleta boleta, List<BoletaDetalle> detalles) {
        Almacen almacenPrincipal = stockService.getAlmacenPrincipal();
        String numeroBoleta = boleta.getNumeroBoleta();

        for (BoletaDetalle detalle : detalles) {
            boolean stockDescontado = stockService.procesarSalidaPorVenta(
                detalle.getProducto(),
                detalle.getCantidad(),
                almacenPrincipal,
                "VENTA_BOLETA",
                numeroBoleta
            );

            if (!stockDescontado) {
                throw new RuntimeException("Error al descontar stock para producto: " + 
                    detalle.getProducto().getNombre());
            }
        }
    }

    /**
     * Reversar movimientos de stock (para anulaciones)
     */
    private void reversarMovimientosStock(Boleta boleta) {
        List<BoletaDetalle> detalles = getDetallesBoleta(boleta);
        Almacen almacenPrincipal = stockService.getAlmacenPrincipal();
        String numeroBoleta = boleta.getNumeroBoleta();

        for (BoletaDetalle detalle : detalles) {
            stockService.reversarSalidaPorVenta(
                detalle.getProducto(),
                detalle.getCantidad(),
                almacenPrincipal,
                "VENTA_BOLETA",
                numeroBoleta
            );
        }
    }

    /**
     * Actualizar totales de boleta basado en sus detalles
     */
    private void actualizarTotalesBoleta(Boleta boleta) {
        List<BoletaDetalle> detalles = getDetallesBoleta(boleta);
        
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal igv = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;

        for (BoletaDetalle detalle : detalles) {
            subtotal = subtotal.add(detalle.getSubtotal() != null ? detalle.getSubtotal() : BigDecimal.ZERO);
            igv = igv.add(detalle.getIgv() != null ? detalle.getIgv() : BigDecimal.ZERO);
            total = total.add(detalle.getTotal() != null ? detalle.getTotal() : BigDecimal.ZERO);
        }

        boleta.setSubtotal(subtotal);
        boleta.setIgv(igv);
        boleta.setTotal(total);
        
        boletaRepository.save(boleta);
    }

    /**
     * Validar disponibilidad de stock antes de confirmar boleta
     */
    public boolean validarStockParaBoleta(List<BoletaDetalle> detalles) {
        for (BoletaDetalle detalle : detalles) {
            if (!stockService.validarStockParaVenta(detalle.getProducto(), detalle.getCantidad(), null)) {
                return false;
            }
        }
        return true;
    }
}