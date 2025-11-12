package com.elolympus.data.Almacen;

import com.elolympus.data.AbstractEntity;
import com.elolympus.data.Logistica.Producto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

@Entity
@Table(name = "stock", schema = "almacen", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"producto_id", "almacen_id"}))
public class Stock extends AbstractEntity {

    @NotNull(message = "El producto es requerido")
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @NotNull(message = "El almacén es requerido")
    @ManyToOne
    @JoinColumn(name = "almacen_id", nullable = false)
    private Almacen almacen;

    @DecimalMin(value = "0.0", inclusive = true, message = "El stock actual debe ser mayor o igual a 0")
    @Column(name = "stock_actual", precision = 12, scale = 4, nullable = false)
    private BigDecimal stockActual = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "El stock reservado debe ser mayor o igual a 0")
    @Column(name = "stock_reservado", precision = 12, scale = 4)
    private BigDecimal stockReservado = BigDecimal.ZERO;

    @Column(name = "stock_disponible", precision = 12, scale = 4)
    private BigDecimal stockDisponible = BigDecimal.ZERO;

    @Column(name = "costo_promedio", precision = 12, scale = 4)
    private BigDecimal costoPromedio = BigDecimal.ZERO;

    @Column(name = "ultima_actualizacion")
    private Timestamp ultimaActualizacion;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    // Constructor por defecto
    public Stock() {
        this.ultimaActualizacion = new Timestamp(System.currentTimeMillis());
    }

    // Constructor con parámetros principales
    public Stock(Producto producto, Almacen almacen, BigDecimal stockActual) {
        this();
        this.producto = producto;
        this.almacen = almacen;
        this.stockActual = stockActual != null ? stockActual : BigDecimal.ZERO;
        calculateStockDisponible();
    }

    // Getters y Setters
    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Almacen getAlmacen() {
        return almacen;
    }

    public void setAlmacen(Almacen almacen) {
        this.almacen = almacen;
    }

    public BigDecimal getStockActual() {
        return stockActual;
    }

    public void setStockActual(BigDecimal stockActual) {
        this.stockActual = stockActual != null ? stockActual : BigDecimal.ZERO;
        calculateStockDisponible();
        this.ultimaActualizacion = new Timestamp(System.currentTimeMillis());
    }

    public BigDecimal getStockReservado() {
        return stockReservado;
    }

    public void setStockReservado(BigDecimal stockReservado) {
        this.stockReservado = stockReservado != null ? stockReservado : BigDecimal.ZERO;
        calculateStockDisponible();
        this.ultimaActualizacion = new Timestamp(System.currentTimeMillis());
    }

    public BigDecimal getStockDisponible() {
        return stockDisponible;
    }

    public void setStockDisponible(BigDecimal stockDisponible) {
        this.stockDisponible = stockDisponible;
    }

    public BigDecimal getCostoPromedio() {
        return costoPromedio;
    }

    public void setCostoPromedio(BigDecimal costoPromedio) {
        this.costoPromedio = costoPromedio;
    }

    public Timestamp getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(Timestamp ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    // Métodos de negocio
    private void calculateStockDisponible() {
        BigDecimal actual = this.stockActual != null ? this.stockActual : BigDecimal.ZERO;
        BigDecimal reservado = this.stockReservado != null ? this.stockReservado : BigDecimal.ZERO;
        this.stockDisponible = actual.subtract(reservado);
    }

    // Método para agregar stock (compras, ingresos)
    public void agregarStock(BigDecimal cantidad, BigDecimal costoUnitario) {
        if (cantidad != null && cantidad.compareTo(BigDecimal.ZERO) > 0) {
            // Calcular nuevo costo promedio ponderado
            if (costoUnitario != null && costoUnitario.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal stockAnterior = this.stockActual;
                BigDecimal costoAnterior = this.costoPromedio != null ? this.costoPromedio : BigDecimal.ZERO;
                BigDecimal valorAnterior = stockAnterior.multiply(costoAnterior);
                BigDecimal valorNuevo = cantidad.multiply(costoUnitario);
                BigDecimal stockTotal = stockAnterior.add(cantidad);
                
                if (stockTotal.compareTo(BigDecimal.ZERO) > 0) {
                    this.costoPromedio = valorAnterior.add(valorNuevo).divide(stockTotal, 4, RoundingMode.HALF_UP);
                }
            }
            
            this.stockActual = this.stockActual.add(cantidad);
            calculateStockDisponible();
            this.ultimaActualizacion = new Timestamp(System.currentTimeMillis());
        }
    }

    // Método para restar stock (ventas, salidas)
    public boolean restarStock(BigDecimal cantidad) {
        if (cantidad != null && cantidad.compareTo(BigDecimal.ZERO) > 0) {
            if (this.stockDisponible.compareTo(cantidad) >= 0) {
                this.stockActual = this.stockActual.subtract(cantidad);
                calculateStockDisponible();
                this.ultimaActualizacion = new Timestamp(System.currentTimeMillis());
                return true;
            }
        }
        return false; // No hay suficiente stock
    }

    // Método para reservar stock
    public boolean reservarStock(BigDecimal cantidad) {
        if (cantidad != null && cantidad.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal disponibleParaReservar = this.stockActual.subtract(this.stockReservado);
            if (disponibleParaReservar.compareTo(cantidad) >= 0) {
                this.stockReservado = this.stockReservado.add(cantidad);
                calculateStockDisponible();
                this.ultimaActualizacion = new Timestamp(System.currentTimeMillis());
                return true;
            }
        }
        return false;
    }

    // Método para liberar stock reservado
    public void liberarStockReservado(BigDecimal cantidad) {
        if (cantidad != null && cantidad.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal cantidadALiberar = cantidad;
            if (cantidadALiberar.compareTo(this.stockReservado) > 0) {
                cantidadALiberar = this.stockReservado;
            }
            this.stockReservado = this.stockReservado.subtract(cantidadALiberar);
            calculateStockDisponible();
            this.ultimaActualizacion = new Timestamp(System.currentTimeMillis());
        }
    }

    // Verificaciones de negocio
    public boolean tieneStockDisponible(BigDecimal cantidadRequerida) {
        return cantidadRequerida != null && 
               this.stockDisponible.compareTo(cantidadRequerida) >= 0;
    }

    public boolean estaBajoMinimo() {
        return producto != null && producto.getStockMinimo() != null &&
               this.stockActual.compareTo(producto.getStockMinimo()) < 0;
    }

    public boolean estaEnMaximo() {
        return producto != null && producto.getStockMaximo() != null &&
               this.stockActual.compareTo(producto.getStockMaximo()) >= 0;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "producto=" + (producto != null ? producto.getNombre() : "null") +
                ", almacen=" + (almacen != null ? almacen.getDescripcion() : "null") +
                ", stockActual=" + stockActual +
                ", stockDisponible=" + stockDisponible +
                ", costoPromedio=" + costoPromedio +
                '}';
    }
}