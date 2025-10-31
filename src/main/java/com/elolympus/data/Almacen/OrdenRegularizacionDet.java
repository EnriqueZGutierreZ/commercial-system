package com.elolympus.data.Almacen;

import com.elolympus.data.AbstractEntity;
import com.elolympus.data.Logistica.Producto;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "orden_regularizacion_det", schema = "almacen")
public class OrdenRegularizacionDet extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_regularizacion", referencedColumnName = "id")
    private OrdenRegularizacion ordenRegularizacion;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "cantidad")
    private BigDecimal cantidad;

    @Column(name = "cantidad_fraccion")
    private BigDecimal cantidadFraccion;

    @Column(name = "fecha_vencimiento")
    private Date fechaVencimiento;

    //Constructor
    public OrdenRegularizacionDet() {
    }

    //Constructor con parametros
    public OrdenRegularizacionDet(OrdenRegularizacion ordenRegularizacion, Producto producto, BigDecimal cantidad, BigDecimal cantidadFraccion, Date fechaVencimiento) {
        this.ordenRegularizacion = ordenRegularizacion;
        this.producto = producto;
        this.cantidad = cantidad;
        this.cantidadFraccion = cantidadFraccion;
        this.fechaVencimiento = fechaVencimiento;
    }

    //Getters y Setters

    public OrdenRegularizacion getOrdenRegularizacion() {
        return ordenRegularizacion;
    }

    public void setOrdenRegularizacion(OrdenRegularizacion ordenRegularizacion) {
        this.ordenRegularizacion = ordenRegularizacion;
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
    }

    public BigDecimal getCantidadFraccion() {
        return cantidadFraccion;
    }

    public void setCantidadFraccion(BigDecimal cantidadFraccion) {
        this.cantidadFraccion = cantidadFraccion;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    //toString
    @Override
    public String toString() {
        return "OrdenRegularizacionDet{" +
                "ordenRegularizacion=" + ordenRegularizacion +
                ", producto=" + (producto != null ? producto.getNombre() : "null") +
                ", cantidad=" + cantidad +
                ", cantidadFraccion=" + cantidadFraccion +
                ", fechaVencimiento=" + fechaVencimiento +
                '}';
    }
}
