package com.elolympus.data.Ventas;

import com.elolympus.data.AbstractEntity;
import com.elolympus.data.Administracion.Persona;
import com.elolympus.data.Empresa.Empresa;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "factura", schema = "ventas")
@EqualsAndHashCode(callSuper = false, exclude = {"cliente", "empresa"})
public class Factura extends AbstractEntity {

    @NotBlank(message = "El número de factura es requerido")
    @Column(name = "numero_factura", unique = true)
    private String numeroFactura;

    @NotBlank(message = "La serie es requerida")
    @Column(name = "serie")
    private String serie;

    @NotNull(message = "La fecha de emisión es requerida")
    @Column(name = "fecha_emision")
    private Date fechaEmision;

    @Column(name = "fecha_vencimiento")
    private Date fechaVencimiento;

    @NotNull(message = "El cliente es requerido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Persona cliente;

    @NotNull(message = "La empresa es requerida")
    @ManyToOne(fetch = FetchType.LAZY)
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
    private String estado = "EMITIDA"; // EMITIDA, PAGADA, ANULADA, VENCIDA

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Column(name = "forma_pago")
    private String formaPago; // CONTADO, CREDITO

    @Column(name = "fecha_pago")
    private Date fechaPago;

    @Column(name = "fecha_creacion")
    private Timestamp fechaCreacion;

    @Column(name = "tipo_documento")
    private String tipoDocumento = "FACTURA"; // FACTURA, FACTURA_ELECTRONICA

    @Column(name = "moneda")
    private String moneda = "PEN"; // PEN, USD

    @DecimalMin(value = "0.0", inclusive = true, message = "El descuento debe ser mayor o igual a 0")
    @Column(name = "descuento", precision = 12, scale = 2)
    private BigDecimal descuento;

    @Column(name = "numero_orden_compra")
    private String numeroOrdenCompra;

    // Constructor por defecto
    public Factura() {
        this.fechaCreacion = new Timestamp(System.currentTimeMillis());
        this.estado = "EMITIDA";
        this.tipoDocumento = "FACTURA";
        this.moneda = "PEN";
    }

    // Constructor con parámetros principales
    public Factura(String numeroFactura, String serie, Date fechaEmision, Persona cliente, Empresa empresa) {
        this();
        this.numeroFactura = numeroFactura;
        this.serie = serie;
        this.fechaEmision = fechaEmision;
        this.cliente = cliente;
        this.empresa = empresa;
    }

    // Getters y Setters
    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
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

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
        calculateTotal();
    }

    public String getNumeroOrdenCompra() {
        return numeroOrdenCompra;
    }

    public void setNumeroOrdenCompra(String numeroOrdenCompra) {
        this.numeroOrdenCompra = numeroOrdenCompra;
    }

    // Método para calcular total automáticamente
    private void calculateTotal() {
        if (subtotal != null && igv != null) {
            BigDecimal totalSinDescuento = subtotal.add(igv);
            if (descuento != null) {
                this.total = totalSinDescuento.subtract(descuento);
            } else {
                this.total = totalSinDescuento;
            }
        }
    }

    // Método para calcular IGV automáticamente (18%)
    public void calculateIgvFromSubtotal() {
        if (subtotal != null) {
            this.igv = subtotal.multiply(new BigDecimal("0.18"));
            calculateTotal();
        }
    }

    // Método para obtener número completo (Serie + Número)
    public String getNumeroCompleto() {
        return (serie != null ? serie + "-" : "") + numeroFactura;
    }

    @Override
    public String toString() {
        return "Factura{" +
                "numeroFactura='" + getNumeroCompleto() + '\'' +
                ", fechaEmision=" + fechaEmision +
                ", cliente=" + (cliente != null ? cliente.getNombres() + " " + cliente.getApellidos() : "null") +
                ", total=" + total +
                ", estado='" + estado + '\'' +
                '}';
    }
}