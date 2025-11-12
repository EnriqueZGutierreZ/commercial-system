package com.elolympus.services.services;

import com.elolympus.data.Ventas.BoletaDetalle;
import com.elolympus.data.Ventas.Boleta;
import com.elolympus.data.Logistica.Producto;
import com.elolympus.services.repository.BoletaDetalleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BoletaDetalleService {

    private final BoletaDetalleRepository boletaDetalleRepository;

    @Autowired
    public BoletaDetalleService(BoletaDetalleRepository boletaDetalleRepository) {
        this.boletaDetalleRepository = boletaDetalleRepository;
    }

    // CRUD básico
    public List<BoletaDetalle> findAll() {
        return boletaDetalleRepository.findAll();
    }

    public Optional<BoletaDetalle> findById(Long id) {
        return boletaDetalleRepository.findById(id);
    }

    public BoletaDetalle save(BoletaDetalle boletaDetalle) {
        boletaDetalle.calculateTotals();
        return boletaDetalleRepository.save(boletaDetalle);
    }

    public BoletaDetalle update(BoletaDetalle boletaDetalle) {
        boletaDetalle.calculateTotals();
        return boletaDetalleRepository.save(boletaDetalle);
    }

    public void deleteById(Long id) {
        boletaDetalleRepository.deleteById(id);
    }

    // Métodos de búsqueda específicos
    public List<BoletaDetalle> findByBoleta(Boleta boleta) {
        return boletaDetalleRepository.findByBoleta(boleta);
    }

    public List<BoletaDetalle> findByProducto(Producto producto) {
        return boletaDetalleRepository.findByProducto(producto);
    }

    public List<BoletaDetalle> findByBoletaOrderById(Boleta boleta) {
        return boletaDetalleRepository.findByBoletaOrderById(boleta);
    }

    // Métodos de estadísticas
    public BigDecimal getCantidadVendidaEnRango(Producto producto, Date fechaInicio, Date fechaFin) {
        BigDecimal cantidad = boletaDetalleRepository.getCantidadVendidaEnRango(producto, fechaInicio, fechaFin);
        return cantidad != null ? cantidad : BigDecimal.ZERO;
    }

    public BigDecimal getTotalVendidoProducto(Producto producto) {
        BigDecimal total = boletaDetalleRepository.getTotalVendidoProducto(producto);
        return total != null ? total : BigDecimal.ZERO;
    }

    public List<Object[]> getProductosMasVendidos(Date fechaInicio, Date fechaFin) {
        return boletaDetalleRepository.getProductosMasVendidos(fechaInicio, fechaFin);
    }

    public boolean productoTieneVentas(Producto producto) {
        return boletaDetalleRepository.productoTieneVentas(producto);
    }

    // Métodos de negocio
    public BigDecimal calcularTotalBoleta(Boleta boleta) {
        List<BoletaDetalle> detalles = findByBoleta(boleta);
        return detalles.stream()
                .map(BoletaDetalle::getTotal)
                .filter(total -> total != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularSubtotalBoleta(Boleta boleta) {
        List<BoletaDetalle> detalles = findByBoleta(boleta);
        return detalles.stream()
                .map(BoletaDetalle::getSubtotal)
                .filter(subtotal -> subtotal != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularIgvBoleta(Boleta boleta) {
        List<BoletaDetalle> detalles = findByBoleta(boleta);
        return detalles.stream()
                .map(BoletaDetalle::getIgv)
                .filter(igv -> igv != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}