package com.elolympus.data.Almacen;

import com.elolympus.data.AbstractEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "orden_regularizacion_det", schema = "almacen")
public class OrdenRegularizacionDet extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_regularizacion", referencedColumnName = "id")
    private OrdenRegularizacion ordenRegularizacion;

    @Column(name = "producto")
    private Integer producto;

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
    public OrdenRegularizacionDet(OrdenRegularizacion ordenRegularizacion, Integer producto, BigDecimal cantidad, BigDecimal cantidadFraccion, Date fechaVencimiento) {
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
                ", producto=" + producto +
                ", cantidad=" + cantidad +
                ", cantidadFraccion=" + cantidadFraccion +
                ", fechaVencimiento=" + fechaVencimiento +
                '}';
    }
}
