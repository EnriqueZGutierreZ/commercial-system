package com.elolympus.data.Ventas;

import com.elolympus.data.AbstractEntity;
import com.elolympus.data.Logistica.Producto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Entity
@Table(name = "boleta_detalle", schema = "ventas")
@EqualsAndHashCode(callSuper = false, exclude = {"boleta", "producto"})
public class BoletaDetalle extends AbstractEntity {

    @NotNull(message = "La boleta es requerida")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boleta_id", nullable = false)
    private Boleta boleta;

    @NotNull(message = "El producto es requerido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @NotNull(message = "La cantidad es requerida")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    @Column(name = "cantidad", precision = 12, scale = 4)
    private BigDecimal cantidad;

    @NotNull(message = "El precio unitario es requerido")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio unitario debe ser mayor o igual a 0")
    @Column(name = "precio_unitario", precision = 12, scale = 4)
    private BigDecimal precioUnitario;

    @DecimalMin(value = "0.0", inclusive = true, message = "El descuento debe ser mayor o igual a 0")
    @Column(name = "descuento", precision = 12, scale = 4)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(name = "subtotal", precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "igv", precision = 12, scale = 2)
    private BigDecimal igv;

    @Column(name = "total", precision = 12, scale = 2)
    private BigDecimal total;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    // Constructor por defecto
    public BoletaDetalle() {
    }

    // Constructor con parámetros principales
    public BoletaDetalle(Boleta boleta, Producto producto, BigDecimal cantidad, BigDecimal precioUnitario) {
        this.boleta = boleta;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.descuento = BigDecimal.ZERO;
        calculateTotals();
    }

    // Getters y Setters
    public Boleta getBoleta() {
        return boleta;
    }

    public void setBoleta(Boleta boleta) {
        this.boleta = boleta;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
        calculateTotals();
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        calculateTotals();
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento != null ? descuento : BigDecimal.ZERO;
        calculateTotals();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getIgv() {
        return igv;
    }

    public void setIgv(BigDecimal igv) {
        this.igv = igv;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // Método para calcular totales automáticamente
    public void calculateTotals() {
        if (cantidad != null && precioUnitario != null) {
            // Subtotal = cantidad * precio unitario - descuento
            BigDecimal subtotalSinDescuento = cantidad.multiply(precioUnitario);
            BigDecimal descuentoTotal = descuento != null ? descuento : BigDecimal.ZERO;
            this.subtotal = subtotalSinDescuento.subtract(descuentoTotal);
            
            // IGV = subtotal * 0.18
            this.igv = this.subtotal.multiply(new BigDecimal("0.18"));
            
            // Total = subtotal + IGV
            this.total = this.subtotal.add(this.igv);
        }
    }

    // Método para obtener el importe sin IGV
    public BigDecimal getImporteSinIgv() {
        return subtotal != null ? subtotal : BigDecimal.ZERO;
    }

    // Método para validar stock disponible
    public boolean validarStock(BigDecimal stockDisponible) {
        return stockDisponible != null && cantidad != null && 
               stockDisponible.compareTo(cantidad) >= 0;
    }

    @Override
    public String toString() {
        return "BoletaDetalle{" +
                "producto=" + (producto != null ? producto.getNombre() : "null") +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", total=" + total +
                '}';
    }
}