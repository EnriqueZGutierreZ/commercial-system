package com.elolympus.data.Logistica;

import com.elolympus.data.AbstractEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "orden_compra_det",schema = "logistica")
public class OrdenCompraDet extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_compra", referencedColumnName = "id")
    private OrdenCompra ordenCompra;

    @Column(name = "producto")
    private Integer producto;

    @Column(name = "cantidad")
    private BigDecimal cantidad;

    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;

    @Column(name = "total_det")
    private BigDecimal totalDet;

    @Column(name = "descuento")
    private BigDecimal descuento;

    @Column(name = "almacen")
    private Integer almacen;

    @Column(name = "cantidad_tg")
    private BigDecimal cantidadTg;

    @Column(name = "lote")
    private String lote;

    @Column(name = "fecha_vencimiento")
    private Date fechaVencimiento;

    @Column(name = "cantidad_usada")
    private BigDecimal cantidadUsada;

    @Column(name = "cantidad_fraccion")
    private BigDecimal cantidadFraccion;

    // Getters and Setters

    public OrdenCompra getOrdenCompra() {
        return ordenCompra;
    }

    public void setOrdenCompra(OrdenCompra ordenCompra) {
        this.ordenCompra = ordenCompra;
    }

    public Integer getProducto() {
        return producto;
    }

    public void setProducto(Integer producto) {
        this.producto = producto;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getTotaldet() {
        return totalDet;
    }

    public void setTotaldet(BigDecimal totaldet) {
        this.totalDet = totalDet;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public Integer getAlmacen() {
        return almacen;
    }

    public void setAlmacen(Integer almacen) {
        this.almacen = almacen;
    }

    public BigDecimal getCantidadTg() {
        return cantidadTg;
    }

    public void setCantidadTg(BigDecimal cantidadTg) {
        this.cantidadTg = cantidadTg;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public BigDecimal getCantidadUsada() {
        return cantidadUsada;
    }

    public void setCantidadUsada(BigDecimal cantidadUsada) {
        this.cantidadUsada = cantidadUsada;
    }

    public BigDecimal getCantidadFraccion() {
        return cantidadFraccion;
    }

    public void setCantidadFraccion(BigDecimal cantidadFraccion) {
        this.cantidadFraccion = cantidadFraccion;
    }
}
