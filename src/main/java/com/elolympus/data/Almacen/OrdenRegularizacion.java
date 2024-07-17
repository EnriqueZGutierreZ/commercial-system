package com.elolympus.data.Almacen;

import com.elolympus.data.AbstractEntity;
import jakarta.persistence.*;

import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "orden_regularizacion", schema = "almacen")
public class OrdenRegularizacion extends AbstractEntity {
    @Column(name = "numero")
    private Integer numero;

    @Column(name = "fecha")
    private Date fecha;

    @Column(name = "almacen")
    private Integer almacen;

    @Column(name = "movimiento")
    private String movimiento;

    @Column(name = "observaciones")
    private String observaciones;

    @OneToMany(mappedBy = "ordenRegularizacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrdenRegularizacionDet> detalles;

    //Constructor
    public OrdenRegularizacion() {
    }

    //Constructor con parametros
    public OrdenRegularizacion(Integer numero, Date fecha, Integer almacen, String movimiento, String observaciones, List<OrdenRegularizacionDet> detalles) {
        this.numero = numero;
        this.fecha = fecha;
        this.almacen = almacen;
        this.movimiento = movimiento;
        this.observaciones = observaciones;
        this.detalles = detalles;
    }

    //Getters y Setters
    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Integer getAlmacen() {
        return almacen;
    }

    public void setAlmacen(Integer almacen) {
        this.almacen = almacen;
    }

    public String getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(String movimiento) {
        this.movimiento = movimiento;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<OrdenRegularizacionDet> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<OrdenRegularizacionDet> detalles) {
        this.detalles = detalles;
    }

    //Metodo toString
    @Override
    public String toString() {
        return "OrdenRegularizacion{" +
                "numero=" + numero +
                ", fecha=" + fecha +
                ", almacen=" + almacen +
                ", movimiento='" + movimiento + '\'' +
                ", observaciones='" + observaciones + '\'' +
                ", detalles=" + detalles +
                '}';
    }
}
