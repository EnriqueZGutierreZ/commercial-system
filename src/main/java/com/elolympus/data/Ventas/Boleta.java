package com.elolympus.data.Ventas;

import com.elolympus.data.AbstractEntity;
import com.elolympus.data.Administracion.Persona;
import com.elolympus.data.Empresa.Empresa;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "boleta", schema = "ventas")
public class Boleta extends AbstractEntity {

    @NotBlank(message = "El número de boleta es requerido")
    @Column(name = "numero_boleta", unique = true)
    private String numeroBoleta;

    @NotNull(message = "La fecha de emisión es requerida")
    @Column(name = "fecha_emision")
    private Date fechaEmision;

    @Column(name = "fecha_vencimiento")
    private Date fechaVencimiento;

    @NotNull(message = "El cliente es requerido")
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Persona cliente;

    @NotNull(message = "La empresa es requerida")
    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @DecimalMin(value = "0.0", inclusive = true, message = "El subtotal debe ser mayor o igual a 0")
    @Column(name = "subtotal", precision = 12, scale = 2)
    private BigDecimal subtotal;

    @DecimalMin(value = "0.0", inclusive = true, message = "El IGV debe ser mayor o igual a 0")
    @Column(name = "igv", precision = 12, scale = 2)
    private BigDecimal igv;

    @DecimalMin(value = "0.0", inclusive = true, message = "El total debe ser mayor o igual a 0")
    @Column(name = "total", precision = 12, scale = 2)
    private BigDecimal total;

    @Column(name = "estado")
    private String estado = "PENDIENTE"; // PENDIENTE, PAGADA, ANULADA

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Column(name = "forma_pago")
    private String formaPago; // EFECTIVO, TARJETA, TRANSFERENCIA

    @Column(name = "fecha_pago")
    private Date fechaPago;

    @Column(name = "fecha_creacion")
    private Timestamp fechaCreacion;

    // Constructor por defecto
    public Boleta() {
        this.fechaCreacion = new Timestamp(System.currentTimeMillis());
        this.estado = "PENDIENTE";
    }

    // Constructor con parámetros principales
    public Boleta(String numeroBoleta, Date fechaEmision, Persona cliente, Empresa empresa) {
        this();
        this.numeroBoleta = numeroBoleta;
        this.fechaEmision = fechaEmision;
        this.cliente = cliente;
        this.empresa = empresa;
    }

    // Getters y Setters
    public String getNumeroBoleta() {
        return numeroBoleta;
    }

    public void setNumeroBoleta(String numeroBoleta) {
        this.numeroBoleta = numeroBoleta;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Persona getCliente() {
        return cliente;
    }

    public void setCliente(Persona cliente) {
        this.cliente = cliente;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
        calculateTotal();
    }

    public BigDecimal getIgv() {
        return igv;
    }

    public void setIgv(BigDecimal igv) {
        this.igv = igv;
        calculateTotal();
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    // Método para calcular total automáticamente
    private void calculateTotal() {
        if (subtotal != null && igv != null) {
            this.total = subtotal.add(igv);
        }
    }

    // Método para calcular IGV automáticamente (18%)
    public void calculateIgvFromSubtotal() {
        if (subtotal != null) {
            this.igv = subtotal.multiply(new BigDecimal("0.18"));
            calculateTotal();
        }
    }

    @Override
    public String toString() {
        return "Boleta{" +
                "numeroBoleta='" + numeroBoleta + '\'' +
                ", fechaEmision=" + fechaEmision +
                ", cliente=" + (cliente != null ? cliente.getNombres() + " " + cliente.getApellidos() : "null") +
                ", total=" + total +
                ", estado='" + estado + '\'' +
                '}';
    }
}