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
@Table(name = "nota_credito", schema = "ventas")
public class NotaCredito extends AbstractEntity {

    @NotBlank(message = "El número de nota de crédito es requerido")
    @Column(name = "numero_nota", unique = true)
    private String numeroNota;

    @NotBlank(message = "La serie es requerida")
    @Column(name = "serie")
    private String serie;

    @NotNull(message = "La fecha de emisión es requerida")
    @Column(name = "fecha_emision")
    private Date fechaEmision;

    @NotNull(message = "El cliente es requerido")
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Persona cliente;

    @NotNull(message = "La empresa es requerida")
    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    // Documento de referencia (Factura o Boleta que se está creditando)
    @Column(name = "documento_referencia_tipo")
    private String documentoReferenciaTipo; // FACTURA, BOLETA

    @Column(name = "documento_referencia_serie")
    private String documentoReferenciaSerie;

    @Column(name = "documento_referencia_numero")
    private String documentoReferenciaNumero;

    @Column(name = "documento_referencia_fecha")
    private Date documentoReferenciaFecha;

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
    private String estado = "EMITIDA"; // EMITIDA, APLICADA, ANULADA

    @NotBlank(message = "El motivo es requerido")
    @Column(name = "motivo", length = 500)
    private String motivo;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Column(name = "fecha_creacion")
    private Timestamp fechaCreacion;

    @Column(name = "tipo_nota")
    private String tipoNota = "DEVOLUCION"; // DEVOLUCION, DESCUENTO, ANULACION, ERROR_FACTURACION

    @Column(name = "moneda")
    private String moneda = "PEN"; // PEN, USD

    @Column(name = "tipo_documento")
    private String tipoDocumento = "NOTA_CREDITO"; // NOTA_CREDITO, NOTA_CREDITO_ELECTRONICA

    // Referencias a los documentos originales si son del sistema
    @ManyToOne
    @JoinColumn(name = "factura_referencia_id")
    private Factura facturaReferencia;

    @ManyToOne
    @JoinColumn(name = "boleta_referencia_id")
    private Boleta boletaReferencia;

    // Constructor por defecto
    public NotaCredito() {
        this.fechaCreacion = new Timestamp(System.currentTimeMillis());
        this.estado = "EMITIDA";
        this.tipoNota = "DEVOLUCION";
        this.moneda = "PEN";
        this.tipoDocumento = "NOTA_CREDITO";
    }

    // Constructor con parámetros principales
    public NotaCredito(String numeroNota, String serie, Date fechaEmision, Persona cliente, Empresa empresa, String motivo) {
        this();
        this.numeroNota = numeroNota;
        this.serie = serie;
        this.fechaEmision = fechaEmision;
        this.cliente = cliente;
        this.empresa = empresa;
        this.motivo = motivo;
    }

    // Getters y Setters
    public String getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(String numeroNota) {
        this.numeroNota = numeroNota;
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

    public String getDocumentoReferenciaTipo() {
        return documentoReferenciaTipo;
    }

    public void setDocumentoReferenciaTipo(String documentoReferenciaTipo) {
        this.documentoReferenciaTipo = documentoReferenciaTipo;
    }

    public String getDocumentoReferenciaSerie() {
        return documentoReferenciaSerie;
    }

    public void setDocumentoReferenciaSerie(String documentoReferenciaSerie) {
        this.documentoReferenciaSerie = documentoReferenciaSerie;
    }

    public String getDocumentoReferenciaNumero() {
        return documentoReferenciaNumero;
    }

    public void setDocumentoReferenciaNumero(String documentoReferenciaNumero) {
        this.documentoReferenciaNumero = documentoReferenciaNumero;
    }

    public Date getDocumentoReferenciaFecha() {
        return documentoReferenciaFecha;
    }

    public void setDocumentoReferenciaFecha(Date documentoReferenciaFecha) {
        this.documentoReferenciaFecha = documentoReferenciaFecha;
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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getTipoNota() {
        return tipoNota;
    }

    public void setTipoNota(String tipoNota) {
        this.tipoNota = tipoNota;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public Factura getFacturaReferencia() {
        return facturaReferencia;
    }

    public void setFacturaReferencia(Factura facturaReferencia) {
        this.facturaReferencia = facturaReferencia;
        if (facturaReferencia != null) {
            this.documentoReferenciaTipo = "FACTURA";
            this.documentoReferenciaSerie = facturaReferencia.getSerie();
            this.documentoReferenciaNumero = facturaReferencia.getNumeroFactura();
            this.documentoReferenciaFecha = facturaReferencia.getFechaEmision();
        }
    }

    public Boleta getBoletaReferencia() {
        return boletaReferencia;
    }

    public void setBoletaReferencia(Boleta boletaReferencia) {
        this.boletaReferencia = boletaReferencia;
        if (boletaReferencia != null) {
            this.documentoReferenciaTipo = "BOLETA";
            this.documentoReferenciaSerie = ""; // Las boletas no tienen serie
            this.documentoReferenciaNumero = boletaReferencia.getNumeroBoleta();
            this.documentoReferenciaFecha = boletaReferencia.getFechaEmision();
        }
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

    // Método para obtener número completo (Serie + Número)
    public String getNumeroCompleto() {
        return (serie != null ? serie + "-" : "") + numeroNota;
    }

    // Método para obtener la referencia completa del documento
    public String getReferenciaCompleta() {
        StringBuilder ref = new StringBuilder();
        if (documentoReferenciaTipo != null) {
            ref.append(documentoReferenciaTipo);
            if (documentoReferenciaSerie != null && !documentoReferenciaSerie.isEmpty()) {
                ref.append(" ").append(documentoReferenciaSerie).append("-");
            } else {
                ref.append(" ");
            }
            if (documentoReferenciaNumero != null) {
                ref.append(documentoReferenciaNumero);
            }
        }
        return ref.toString();
    }

    @Override
    public String toString() {
        return "NotaCredito{" +
                "numeroNota='" + getNumeroCompleto() + '\'' +
                ", fechaEmision=" + fechaEmision +
                ", cliente=" + (cliente != null ? cliente.getNombres() + " " + cliente.getApellidos() : "null") +
                ", total=" + total +
                ", estado='" + estado + '\'' +
                ", motivo='" + motivo + '\'' +
                ", referencia='" + getReferenciaCompleta() + '\'' +
                '}';
    }
}