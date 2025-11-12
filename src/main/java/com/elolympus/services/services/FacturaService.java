package com.elolympus.services.services;

import com.elolympus.data.Ventas.Factura;
import com.elolympus.data.Ventas.FacturaDetalle;
import com.elolympus.data.Administracion.Persona;
import com.elolympus.data.Almacen.Almacen;
import com.elolympus.services.repository.FacturaRepository;
import com.elolympus.services.repository.FacturaDetalleRepository;
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
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final FacturaDetalleRepository facturaDetalleRepository;
    private final StockService stockService;
    private final AlmacenService almacenService;

    @Autowired
    public FacturaService(FacturaRepository facturaRepository, 
                         FacturaDetalleRepository facturaDetalleRepository,
                         StockService stockService,
                         AlmacenService almacenService) {
        this.facturaRepository = facturaRepository;
        this.facturaDetalleRepository = facturaDetalleRepository;
        this.stockService = stockService;
        this.almacenService = almacenService;
    }

    // CRUD básico
    public List<Factura> findAll() {
        return facturaRepository.findAll();
    }

    public Optional<Factura> findById(Long id) {
        return facturaRepository.findById(id);
    }

    public Factura save(Factura factura) {
        // Generar número de factura automáticamente si no existe
        if (factura.getNumeroFactura() == null || factura.getNumeroFactura().isEmpty()) {
            String serie = factura.getSerie() != null ? factura.getSerie() : "F001";
            factura.setSerie(serie);
            factura.setNumeroFactura(generateNumeroFactura(serie));
        }
        
        // Establecer fecha de creación si es nueva
        if (factura.getId() == null) {
            factura.setFechaCreacion(new Timestamp(System.currentTimeMillis()));
        }
        
        // Calcular IGV automáticamente si no está establecido
        if (factura.getSubtotal() != null && factura.getIgv() == null) {
            factura.calculateIgvFromSubtotal();
        }
        
        return facturaRepository.save(factura);
    }

    public Factura update(Factura factura) {
        return facturaRepository.save(factura);
    }

    public void delete(Factura factura) {
        // No eliminar físicamente, mejor anular
        factura.setEstado("ANULADA");
        facturaRepository.save(factura);
    }

    public void deleteById(Long id) {
        Optional<Factura> factura = findById(id);
        if (factura.isPresent()) {
            delete(factura.get());
        }
    }

    // Métodos de búsqueda específicos
    public Optional<Factura> findByNumeroFactura(String numeroFactura) {
        return facturaRepository.findByNumeroFactura(numeroFactura);
    }

    public Optional<Factura> findBySerieAndNumero(String serie, String numero) {
        return facturaRepository.findBySerieAndNumeroFactura(serie, numero);
    }

    public List<Factura> findByCliente(Persona cliente) {
        return facturaRepository.findByCliente(cliente);
    }

    public List<Factura> findByEstado(String estado) {
        return facturaRepository.findByEstado(estado);
    }

    public List<Factura> findBySerie(String serie) {
        return facturaRepository.findBySerie(serie);
    }

    public List<Factura> findByFechaEmisionBetween(Date fechaInicio, Date fechaFin) {
        return facturaRepository.findByFechaEmisionBetween(fechaInicio, fechaFin);
    }

    public List<Factura> findFacturasEmitidas() {
        return facturaRepository.findFacturasEmitidas();
    }

    public List<Factura> findFacturasVencidas() {
        return facturaRepository.findFacturasVencidas();
    }

    public List<Factura> findFacturasPorVencer(int dias) {
        return facturaRepository.findFacturasPorVencer(dias);
    }

    // Métodos de búsqueda para filtros
    public List<Factura> findByNumeroFacturaContaining(String numeroFactura) {
        return facturaRepository.findByNumeroFacturaContainingIgnoreCase(numeroFactura);
    }

    public List<Factura> findByClienteNombreContaining(String nombre) {
        return facturaRepository.findByClienteNombreContaining(nombre);
    }

    public List<Factura> findBySerieContaining(String serie) {
        return facturaRepository.findBySerieContainingIgnoreCase(serie);
    }

    // Métodos para cambiar estado
    public Factura marcarComoPagada(Long facturaId, String formaPago) {
        Optional<Factura> optionalFactura = findById(facturaId);
        if (optionalFactura.isPresent()) {
            Factura factura = optionalFactura.get();
            factura.setEstado("PAGADA");
            factura.setFormaPago(formaPago);
            factura.setFechaPago(Date.valueOf(LocalDate.now()));
            return update(factura);
        }
        throw new RuntimeException("Factura no encontrada");
    }

    public Factura anularFactura(Long facturaId, String motivo) {
        Optional<Factura> optionalFactura = findById(facturaId);
        if (optionalFactura.isPresent()) {
            Factura factura = optionalFactura.get();
            
            // Reversar movimientos de stock si la factura estaba emitida
            if ("EMITIDA".equals(factura.getEstado()) || "PAGADA".equals(factura.getEstado())) {
                reversarMovimientosStock(factura);
            }
            
            factura.setEstado("ANULADA");
            factura.setObservaciones(motivo);
            return update(factura);
        }
        throw new RuntimeException("Factura no encontrada");
    }

    public Factura marcarComoVencida(Long facturaId) {
        Optional<Factura> optionalFactura = findById(facturaId);
        if (optionalFactura.isPresent()) {
            Factura factura = optionalFactura.get();
            factura.setEstado("VENCIDA");
            return update(factura);
        }
        throw new RuntimeException("Factura no encontrada");
    }

    // Métodos de estadísticas
    public Long countByEstado(String estado) {
        return facturaRepository.countByEstado(estado);
    }

    public BigDecimal getTotalVentasEnRango(Date fechaInicio, Date fechaFin) {
        BigDecimal total = facturaRepository.getTotalVentasEnRango(fechaInicio, fechaFin);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getTotalIgvEnRango(Date fechaInicio, Date fechaFin) {
        BigDecimal total = facturaRepository.getTotalIgvEnRango(fechaInicio, fechaFin);
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

    // Generar número de factura automático por serie
    private String generateNumeroFactura(String serie) {
        String ultimoNumero = facturaRepository.getUltimoNumeroPorSerie(serie);
        int siguienteNumero = 1;
        
        if (ultimoNumero != null) {
            try {
                siguienteNumero = Integer.parseInt(ultimoNumero) + 1;
            } catch (NumberFormatException e) {
                // Si no se puede parsear, empezar desde 1
                siguienteNumero = 1;
            }
        }
        
        return String.format("%08d", siguienteNumero);
    }

    // Validar si un número de factura ya existe en una serie
    public boolean existeNumeroFactura(String serie, String numeroFactura) {
        return facturaRepository.findBySerieAndNumeroFactura(serie, numeroFactura).isPresent();
    }

    // Método para buscar con múltiples criterios
    public List<Factura> buscarConFiltros(String numeroFactura, String serie, String clienteNombre, 
                                         String estado, Date fechaInicio, Date fechaFin) {
        List<Factura> result = findAll();
        
        if (numeroFactura != null && !numeroFactura.isEmpty()) {
            result = result.stream()
                    .filter(f -> f.getNumeroFactura().toLowerCase().contains(numeroFactura.toLowerCase()))
                    .toList();
        }
        
        if (serie != null && !serie.isEmpty()) {
            result = result.stream()
                    .filter(f -> f.getSerie() != null && f.getSerie().toLowerCase().contains(serie.toLowerCase()))
                    .toList();
        }
        
        if (clienteNombre != null && !clienteNombre.isEmpty()) {
            result = result.stream()
                    .filter(f -> {
                        String nombreCompleto = f.getCliente().getNombres() + " " + f.getCliente().getApellidos();
                        return nombreCompleto.toLowerCase().contains(clienteNombre.toLowerCase());
                    })
                    .toList();
        }
        
        if (estado != null && !estado.isEmpty()) {
            result = result.stream()
                    .filter(f -> f.getEstado().equals(estado))
                    .toList();
        }
        
        if (fechaInicio != null && fechaFin != null) {
            result = result.stream()
                    .filter(f -> !f.getFechaEmision().before(fechaInicio) && !f.getFechaEmision().after(fechaFin))
                    .toList();
        }
        
        return result;
    }

    // Métodos para reportes
    public List<Object[]> getVentasPorCliente(Date fechaInicio, Date fechaFin) {
        return facturaRepository.getVentasPorCliente(fechaInicio, fechaFin);
    }

    public List<Factura> findFacturasConDescuento() {
        return facturaRepository.findFacturasConDescuento();
    }

    // Proceso automático para marcar facturas vencidas
    public void procesarFacturasVencidas() {
        List<Factura> facturasEmitidas = findByEstado("EMITIDA");
        Date hoy = Date.valueOf(LocalDate.now());
        
        for (Factura factura : facturasEmitidas) {
            if (factura.getFechaVencimiento() != null && factura.getFechaVencimiento().before(hoy)) {
                factura.setEstado("VENCIDA");
                update(factura);
            }
        }
    }

    // MÉTODOS PARA GESTIÓN DE DETALLES Y STOCK

    /**
     * Obtener detalles de una factura
     */
    public List<FacturaDetalle> getDetallesFactura(Factura factura) {
        return facturaDetalleRepository.findByFacturaOrderById(factura);
    }

    /**
     * Agregar detalle a una factura (con validación de stock)
     */
    public FacturaDetalle agregarDetalle(Factura factura, FacturaDetalle detalle) {
        // Validar que hay stock disponible
        if (!stockService.validarStockParaVenta(detalle.getProducto(), detalle.getCantidad(), null)) {
            throw new RuntimeException("Stock insuficiente para el producto: " + detalle.getProducto().getNombre());
        }

        detalle.setFactura(factura);
        detalle.calculateTotals();
        
        FacturaDetalle detalleGuardado = facturaDetalleRepository.save(detalle);
        
        // Actualizar totales de la factura
        actualizarTotalesFactura(factura);
        
        return detalleGuardado;
    }

    /**
     * Guardar factura con validación y procesamiento de stock
     */
    public Factura saveConStock(Factura factura) {
        boolean esNueva = factura.getId() == null;
        
        // Generar número de factura automáticamente si no existe
        if (factura.getNumeroFactura() == null || factura.getNumeroFactura().isEmpty()) {
            String serie = factura.getSerie() != null ? factura.getSerie() : "F001";
            factura.setSerie(serie);
            factura.setNumeroFactura(generateNumeroFactura(serie));
        }
        
        // Establecer fecha de creación si es nueva
        if (esNueva) {
            factura.setFechaCreacion(new Timestamp(System.currentTimeMillis()));
        }
        
        // Calcular IGV automáticamente si no está establecido
        if (factura.getSubtotal() != null && factura.getIgv() == null) {
            factura.calculateIgvFromSubtotal();
        }
        
        Factura facturaGuardada = facturaRepository.save(factura);
        
        // Si es una factura nueva y está en estado EMITIDA, procesar movimientos de stock
        if (esNueva && "EMITIDA".equals(factura.getEstado())) {
            procesarMovimientosStock(facturaGuardada);
        }
        
        return facturaGuardada;
    }

    /**
     * Procesar factura completa con detalles (método principal para ventas)
     */
    public Factura procesarFacturaCompleta(Factura factura, List<FacturaDetalle> detalles) {
        // Validar stock para todos los productos
        for (FacturaDetalle detalle : detalles) {
            if (!stockService.validarStockParaVenta(detalle.getProducto(), detalle.getCantidad(), null)) {
                throw new RuntimeException("Stock insuficiente para el producto: " + 
                    detalle.getProducto().getNombre() + ". Stock disponible: " + 
                    stockService.getStockDisponibleTotalProducto(detalle.getProducto()));
            }
        }

        // Calcular totales de la factura
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal igv = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;

        for (FacturaDetalle detalle : detalles) {
            detalle.calculateTotals();
            subtotal = subtotal.add(detalle.getSubtotal());
            igv = igv.add(detalle.getIgv());
            total = total.add(detalle.getTotal());
        }

        factura.setSubtotal(subtotal);
        factura.setIgv(igv);
        factura.setTotal(total);

        // Guardar factura
        Factura facturaGuardada = saveConStock(factura);

        // Guardar detalles
        for (FacturaDetalle detalle : detalles) {
            detalle.setFactura(facturaGuardada);
            facturaDetalleRepository.save(detalle);
        }

        // Procesar movimientos de stock si está emitida
        if ("EMITIDA".equals(facturaGuardada.getEstado())) {
            procesarMovimientosStockConDetalles(facturaGuardada, detalles);
        }

        return facturaGuardada;
    }

    /**
     * Procesar movimientos de stock para una factura
     */
    private void procesarMovimientosStock(Factura factura) {
        List<FacturaDetalle> detalles = getDetallesFactura(factura);
        procesarMovimientosStockConDetalles(factura, detalles);
    }

    /**
     * Procesar movimientos de stock con lista de detalles
     */
    private void procesarMovimientosStockConDetalles(Factura factura, List<FacturaDetalle> detalles) {
        Almacen almacenPrincipal = stockService.getAlmacenPrincipal();
        String numeroFactura = factura.getSerie() + "-" + factura.getNumeroFactura();

        for (FacturaDetalle detalle : detalles) {
            boolean stockDescontado = stockService.procesarSalidaPorVenta(
                detalle.getProducto(),
                detalle.getCantidad(),
                almacenPrincipal,
                "VENTA_FACTURA",
                numeroFactura
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
    private void reversarMovimientosStock(Factura factura) {
        List<FacturaDetalle> detalles = getDetallesFactura(factura);
        Almacen almacenPrincipal = stockService.getAlmacenPrincipal();
        String numeroFactura = factura.getSerie() + "-" + factura.getNumeroFactura();

        for (FacturaDetalle detalle : detalles) {
            stockService.reversarSalidaPorVenta(
                detalle.getProducto(),
                detalle.getCantidad(),
                almacenPrincipal,
                "VENTA_FACTURA",
                numeroFactura
            );
        }
    }

    /**
     * Actualizar totales de factura basado en sus detalles
     */
    private void actualizarTotalesFactura(Factura factura) {
        List<FacturaDetalle> detalles = getDetallesFactura(factura);
        
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal igv = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;

        for (FacturaDetalle detalle : detalles) {
            subtotal = subtotal.add(detalle.getSubtotal() != null ? detalle.getSubtotal() : BigDecimal.ZERO);
            igv = igv.add(detalle.getIgv() != null ? detalle.getIgv() : BigDecimal.ZERO);
            total = total.add(detalle.getTotal() != null ? detalle.getTotal() : BigDecimal.ZERO);
        }

        factura.setSubtotal(subtotal);
        factura.setIgv(igv);
        factura.setTotal(total);
        
        facturaRepository.save(factura);
    }

    /**
     * Validar disponibilidad de stock antes de confirmar factura
     */
    public boolean validarStockParaFactura(List<FacturaDetalle> detalles) {
        for (FacturaDetalle detalle : detalles) {
            if (!stockService.validarStockParaVenta(detalle.getProducto(), detalle.getCantidad(), null)) {
                return false;
            }
        }
        return true;
    }
}