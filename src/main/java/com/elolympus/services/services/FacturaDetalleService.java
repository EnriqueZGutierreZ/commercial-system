package com.elolympus.services.services;

import com.elolympus.data.Ventas.FacturaDetalle;
import com.elolympus.data.Ventas.Factura;
import com.elolympus.data.Logistica.Producto;
import com.elolympus.services.repository.FacturaDetalleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FacturaDetalleService {

    private final FacturaDetalleRepository facturaDetalleRepository;

    @Autowired
    public FacturaDetalleService(FacturaDetalleRepository facturaDetalleRepository) {
        this.facturaDetalleRepository = facturaDetalleRepository;
    }

    // CRUD básico
    public List<FacturaDetalle> findAll() {
        return facturaDetalleRepository.findAll();
    }

    public Optional<FacturaDetalle> findById(Long id) {
        return facturaDetalleRepository.findById(id);
    }

    public FacturaDetalle save(FacturaDetalle facturaDetalle) {
        facturaDetalle.calculateTotals();
        return facturaDetalleRepository.save(facturaDetalle);
    }

    public FacturaDetalle update(FacturaDetalle facturaDetalle) {
        facturaDetalle.calculateTotals();
        return facturaDetalleRepository.save(facturaDetalle);
    }

    public void deleteById(Long id) {
        facturaDetalleRepository.deleteById(id);
    }

    // Métodos de búsqueda específicos
    public List<FacturaDetalle> findByFactura(Factura factura) {
        return facturaDetalleRepository.findByFactura(factura);
    }

    public List<FacturaDetalle> findByProducto(Producto producto) {
        return facturaDetalleRepository.findByProducto(producto);
    }

    public List<FacturaDetalle> findByFacturaOrderById(Factura factura) {
        return facturaDetalleRepository.findByFacturaOrderById(factura);
    }

    // Métodos de estadísticas
    public BigDecimal getCantidadVendidaEnRango(Producto producto, Date fechaInicio, Date fechaFin) {
        BigDecimal cantidad = facturaDetalleRepository.getCantidadVendidaEnRango(producto, fechaInicio, fechaFin);
        return cantidad != null ? cantidad : BigDecimal.ZERO;
    }

    public BigDecimal getTotalVendidoProducto(Producto producto) {
        BigDecimal total = facturaDetalleRepository.getTotalVendidoProducto(producto);
        return total != null ? total : BigDecimal.ZERO;
    }

    public List<Object[]> getProductosMasVendidos(Date fechaInicio, Date fechaFin) {
        return facturaDetalleRepository.getProductosMasVendidos(fechaInicio, fechaFin);
    }

    public boolean productoTieneVentas(Producto producto) {
        return facturaDetalleRepository.productoTieneVentas(producto);
    }

    // Métodos de negocio
    public BigDecimal calcularTotalFactura(Factura factura) {
        List<FacturaDetalle> detalles = findByFactura(factura);
        return detalles.stream()
                .map(FacturaDetalle::getTotal)
                .filter(total -> total != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularSubtotalFactura(Factura factura) {
        List<FacturaDetalle> detalles = findByFactura(factura);
        return detalles.stream()
                .map(FacturaDetalle::getSubtotal)
                .filter(subtotal -> subtotal != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularIgvFactura(Factura factura) {
        List<FacturaDetalle> detalles = findByFactura(factura);
        return detalles.stream()
                .map(FacturaDetalle::getIgv)
                .filter(igv -> igv != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}