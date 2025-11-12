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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "cuenta_por_cobrar", schema = "ventas")
public class CuentaPorCobrar extends AbstractEntity {

    @NotBlank(message = "El número de documento es requerido")
    @Column(name = "numero_documento", nullable = false)
    private String numeroDocumento;

    @NotBlank(message = "El tipo de documento es requerido")
    @Column(name = "tipo_documento", nullable = false)
    private String tipoDocumento; // FACTURA, BOLETA, NOTA_CREDITO

    @NotNull(message = "La referencia al documento es requerida")
    @Column(name = "documento_referencia_id", nullable = false)
    private Long documentoReferenciaId; // ID de Factura, Boleta, etc.

    @NotNull(message = "El cliente es requerido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Persona cliente;

    @NotNull(message = "La empresa es requerida")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @NotNull(message = "La fecha de emisión es requerida")
    @Column(name = "fecha_emision", nullable = false)
    private Date fechaEmision;

    @Column(name = "fecha_vencimiento")
    private Date fechaVencimiento;

    @DecimalMin(value = "0.0", message = "El monto original debe ser mayor a 0")
    @NotNull(message = "El monto original es requerido")
    @Column(name = "monto_original", precision = 12, scale = 2, nullable = false)
    private BigDecimal montoOriginal;

    @DecimalMin(value = "0.0", message = "El monto pagado debe ser mayor o igual a 0")
    @Column(name = "monto_pagado", precision = 12, scale = 2)
    private BigDecimal montoPagado = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "El saldo pendiente debe ser mayor o igual a 0")
    @Column(name = "saldo_pendiente", precision = 12, scale = 2)
    private BigDecimal saldoPendiente;

    @NotBlank(message = "El estado es requerido")
    @Column(name = "estado", nullable = false)
    private String estado; // PENDIENTE, PAGADO_PARCIAL, PAGADO_TOTAL, VENCIDO, ANULADO

    @Column(name = "prioridad")
    private String prioridad; // ALTA, MEDIA, BAJA

    @Column(name = "dias_vencimiento")
    private Integer diasVencimiento;

    @Column(name = "rango_vencimiento")
    private String rangoVencimiento; // NO_VENCIDO, 1-30, 31-60, 61-90, MAS_90

    @Column(name = "forma_pago_acordada")
    private String formaPagoAcordada; // CONTADO, CREDITO_15, CREDITO_30, CREDITO_60, etc.

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "contacto_realizado")
    private Boolean contactoRealizado = false;

    @Column(name = "fecha_ultimo_contacto")
    private Date fechaUltimoContacto;

    @Column(name = "resultado_ultimo_contacto", length = 500)
    private String resultadoUltimoContacto;

    @Column(name = "fecha_proximo_seguimiento")
    private Date fechaProximoSeguimiento;

    // Constructor por defecto
    public CuentaPorCobrar() {
        this.estado = "PENDIENTE";
        this.montoPagado = BigDecimal.ZERO;
        this.contactoRealizado = false;
        this.creado = java.time.LocalDateTime.now();
        this.creador = "SISTEMA";
        this.activo = true;
    }

    // Constructor con documento de referencia
    public CuentaPorCobrar(String numeroDocumento, String tipoDocumento, Long documentoReferenciaId, 
                           Persona cliente, Empresa empresa, Date fechaEmision, BigDecimal montoOriginal) {
        this();
        this.numeroDocumento = numeroDocumento;
        this.tipoDocumento = tipoDocumento;
        this.documentoReferenciaId = documentoReferenciaId;
        this.cliente = cliente;
        this.empresa = empresa;
        this.fechaEmision = fechaEmision;
        this.montoOriginal = montoOriginal;
        this.saldoPendiente = montoOriginal;
        calcularDiasVencimiento();
        actualizarRangoVencimiento();
    }

    // Métodos de cálculo automático
    @PreUpdate
    @PrePersist
    public void actualizarCalculos() {
        calcularSaldoPendiente();
        calcularDiasVencimiento();
        actualizarRangoVencimiento();
        actualizarEstado();
    }

    public void calcularSaldoPendiente() {
        if (montoOriginal != null && montoPagado != null) {
            this.saldoPendiente = montoOriginal.subtract(montoPagado);
        }
    }

    public void calcularDiasVencimiento() {
        if (fechaVencimiento != null) {
            LocalDate hoy = LocalDate.now();
            LocalDate vencimiento = fechaVencimiento.toLocalDate();
            this.diasVencimiento = (int) ChronoUnit.DAYS.between(vencimiento, hoy);
        }
    }

    public void actualizarRangoVencimiento() {
        if (diasVencimiento == null) {
            this.rangoVencimiento = "NO_VENCIDO";
            return;
        }

        if (diasVencimiento <= 0) {
            this.rangoVencimiento = "NO_VENCIDO";
        } else if (diasVencimiento <= 30) {
            this.rangoVencimiento = "1-30";
        } else if (diasVencimiento <= 60) {
            this.rangoVencimiento = "31-60";
        } else if (diasVencimiento <= 90) {
            this.rangoVencimiento = "61-90";
        } else {
            this.rangoVencimiento = "MAS_90";
        }
    }

    public void actualizarEstado() {
        if (saldoPendiente == null || montoOriginal == null) return;

        if (saldoPendiente.compareTo(BigDecimal.ZERO) == 0) {
            this.estado = "PAGADO_TOTAL";
        } else if (saldoPendiente.compareTo(montoOriginal) < 0) {
            this.estado = "PAGADO_PARCIAL";
        } else if (diasVencimiento != null && diasVencimiento > 0) {
            this.estado = "VENCIDO";
        } else {
            this.estado = "PENDIENTE";
        }
    }

    // Métodos de utilidad
    public BigDecimal getPorcentajePagado() {
        if (montoOriginal == null || montoOriginal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return montoPagado.multiply(BigDecimal.valueOf(100)).divide(montoOriginal, 2, BigDecimal.ROUND_HALF_UP);
    }

    public boolean isVencido() {
        return diasVencimiento != null && diasVencimiento > 0;
    }

    public boolean isPagadoTotal() {
        return "PAGADO_TOTAL".equals(estado);
    }

    public boolean isPagadoParcial() {
        return "PAGADO_PARCIAL".equals(estado);
    }

    public boolean isPendiente() {
        return "PENDIENTE".equals(estado) || "VENCIDO".equals(estado);
    }

    public void registrarPago(BigDecimal montoPago, String observacion) {
        if (montoPago != null && montoPago.compareTo(BigDecimal.ZERO) > 0) {
            this.montoPagado = this.montoPagado.add(montoPago);
            if (observacion != null && !observacion.trim().isEmpty()) {
                String nuevaObservacion = "Pago registrado: " + montoPago + " - " + observacion;
                this.observaciones = this.observaciones == null ? nuevaObservacion : 
                                   this.observaciones + "\n" + nuevaObservacion;
            }
            actualizarCalculos();
        }
    }

    public void registrarContacto(String resultado, Date fechaProximoSeguimiento) {
        this.contactoRealizado = true;
        this.fechaUltimoContacto = new Date(System.currentTimeMillis());
        this.resultadoUltimoContacto = resultado;
        this.fechaProximoSeguimiento = fechaProximoSeguimiento;
    }

    // Getters y Setters
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public Long getDocumentoReferenciaId() { return documentoReferenciaId; }
    public void setDocumentoReferenciaId(Long documentoReferenciaId) { this.documentoReferenciaId = documentoReferenciaId; }

    public Persona getCliente() { return cliente; }
    public void setCliente(Persona cliente) { this.cliente = cliente; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    public Date getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(Date fechaEmision) { this.fechaEmision = fechaEmision; }

    public Date getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(Date fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public BigDecimal getMontoOriginal() { return montoOriginal; }
    public void setMontoOriginal(BigDecimal montoOriginal) { this.montoOriginal = montoOriginal; }

    public BigDecimal getMontoPagado() { return montoPagado; }
    public void setMontoPagado(BigDecimal montoPagado) { this.montoPagado = montoPagado; }

    public BigDecimal getSaldoPendiente() { return saldoPendiente; }
    public void setSaldoPendiente(BigDecimal saldoPendiente) { this.saldoPendiente = saldoPendiente; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }

    public Integer getDiasVencimiento() { return diasVencimiento; }
    public void setDiasVencimiento(Integer diasVencimiento) { this.diasVencimiento = diasVencimiento; }

    public String getRangoVencimiento() { return rangoVencimiento; }
    public void setRangoVencimiento(String rangoVencimiento) { this.rangoVencimiento = rangoVencimiento; }

    public String getFormaPagoAcordada() { return formaPagoAcordada; }
    public void setFormaPagoAcordada(String formaPagoAcordada) { this.formaPagoAcordada = formaPagoAcordada; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Boolean getContactoRealizado() { return contactoRealizado; }
    public void setContactoRealizado(Boolean contactoRealizado) { this.contactoRealizado = contactoRealizado; }

    public Date getFechaUltimoContacto() { return fechaUltimoContacto; }
    public void setFechaUltimoContacto(Date fechaUltimoContacto) { this.fechaUltimoContacto = fechaUltimoContacto; }

    public String getResultadoUltimoContacto() { return resultadoUltimoContacto; }
    public void setResultadoUltimoContacto(String resultadoUltimoContacto) { this.resultadoUltimoContacto = resultadoUltimoContacto; }

    public Date getFechaProximoSeguimiento() { return fechaProximoSeguimiento; }
    public void setFechaProximoSeguimiento(Date fechaProximoSeguimiento) { this.fechaProximoSeguimiento = fechaProximoSeguimiento; }

    @Override
    public String toString() {
        return "CuentaPorCobrar{" +
                "numeroDocumento='" + numeroDocumento + '\'' +
                ", tipoDocumento='" + tipoDocumento + '\'' +
                ", cliente=" + (cliente != null ? cliente.getNombres() + " " + cliente.getApellidos() : "null") +
                ", montoOriginal=" + montoOriginal +
                ", saldoPendiente=" + saldoPendiente +
                ", estado='" + estado + '\'' +
                ", diasVencimiento=" + diasVencimiento +
                '}';
    }
}