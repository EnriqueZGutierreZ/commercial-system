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
@Table(name = "pago_cliente", schema = "ventas")
public class PagoCliente extends AbstractEntity {

    @NotNull(message = "La cuenta por cobrar es requerida")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_por_cobrar_id", nullable = false)
    private CuentaPorCobrar cuentaPorCobrar;

    @NotNull(message = "El cliente es requerido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Persona cliente;

    @NotNull(message = "La empresa es requerida")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @NotBlank(message = "El número de recibo es requerido")
    @Column(name = "numero_recibo", unique = true, nullable = false)
    private String numeroRecibo;

    @NotNull(message = "La fecha de pago es requerida")
    @Column(name = "fecha_pago", nullable = false)
    private Date fechaPago;

    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    @NotNull(message = "El monto es requerido")
    @Column(name = "monto", precision = 12, scale = 2, nullable = false)
    private BigDecimal monto;

    @NotBlank(message = "La forma de pago es requerida")
    @Column(name = "forma_pago", nullable = false)
    private String formaPago; // EFECTIVO, TRANSFERENCIA, CHEQUE, TARJETA

    @Column(name = "numero_operacion", length = 100)
    private String numeroOperacion; // Para transferencias, cheques, etc.

    @Column(name = "banco", length = 100)
    private String banco; // Banco del cheque o transferencia

    @Column(name = "fecha_deposito")
    private Date fechaDeposito; // Para cheques diferidos

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @NotBlank(message = "El estado es requerido")
    @Column(name = "estado", nullable = false)
    private String estado; // REGISTRADO, VERIFICADO, RECHAZADO

    @Column(name = "motivo_rechazo", length = 500)
    private String motivoRechazo;

    @Column(name = "fecha_verificacion")
    private Date fechaVerificacion;

    @Column(name = "usuario_verificacion", length = 100)
    private String usuarioVerificacion;

    @Column(name = "fecha_creacion")
    private Timestamp fechaCreacion;

    @Column(name = "usuario_registro", length = 100)
    private String usuarioRegistro;

    @Column(name = "tipo_pago")
    private String tipoPago; // PARCIAL, TOTAL

    // Campos para conciliación bancaria
    @Column(name = "conciliado")
    private Boolean conciliado = false;

    @Column(name = "fecha_conciliacion")
    private Date fechaConciliacion;

    @Column(name = "usuario_conciliacion", length = 100)
    private String usuarioConciliacion;

    // Constructor por defecto
    public PagoCliente() {
        this.fechaCreacion = new Timestamp(System.currentTimeMillis());
        this.estado = "REGISTRADO";
        this.conciliado = false;
        this.creado = java.time.LocalDateTime.now();
        this.creador = "SISTEMA";
        this.activo = true;
    }

    // Constructor con parámetros principales
    public PagoCliente(CuentaPorCobrar cuentaPorCobrar, BigDecimal monto, String formaPago, String numeroRecibo) {
        this();
        this.cuentaPorCobrar = cuentaPorCobrar;
        this.cliente = cuentaPorCobrar.getCliente();
        this.empresa = cuentaPorCobrar.getEmpresa();
        this.monto = monto;
        this.formaPago = formaPago;
        this.numeroRecibo = numeroRecibo;
        this.fechaPago = new Date(System.currentTimeMillis());
        determinarTipoPago();
    }

    // Métodos de lógica de negocio
    @PrePersist
    @PreUpdate
    public void actualizarCalculos() {
        determinarTipoPago();
    }

    private void determinarTipoPago() {
        if (cuentaPorCobrar != null && monto != null) {
            BigDecimal saldoPendiente = cuentaPorCobrar.getSaldoPendiente();
            if (saldoPendiente != null) {
                if (monto.compareTo(saldoPendiente) >= 0) {
                    this.tipoPago = "TOTAL";
                } else {
                    this.tipoPago = "PARCIAL";
                }
            }
        }
    }

    public void verificarPago(String usuario, String observacion) {
        this.estado = "VERIFICADO";
        this.fechaVerificacion = new Date(System.currentTimeMillis());
        this.usuarioVerificacion = usuario;
        if (observacion != null && !observacion.trim().isEmpty()) {
            this.observaciones = this.observaciones == null ? observacion : 
                               this.observaciones + "\nVerificación: " + observacion;
        }
    }

    public void rechazarPago(String usuario, String motivo) {
        this.estado = "RECHAZADO";
        this.fechaVerificacion = new Date(System.currentTimeMillis());
        this.usuarioVerificacion = usuario;
        this.motivoRechazo = motivo;
    }

    public void conciliar(String usuario) {
        this.conciliado = true;
        this.fechaConciliacion = new Date(System.currentTimeMillis());
        this.usuarioConciliacion = usuario;
    }

    public boolean isVerificado() {
        return "VERIFICADO".equals(estado);
    }

    public boolean isRechazado() {
        return "RECHAZADO".equals(estado);
    }

    public boolean isPendienteVerificacion() {
        return "REGISTRADO".equals(estado);
    }

    public boolean requiresAccountNumber() {
        return "TRANSFERENCIA".equals(formaPago) || "CHEQUE".equals(formaPago);
    }

    public boolean isCheque() {
        return "CHEQUE".equals(formaPago);
    }

    public boolean isEfectivo() {
        return "EFECTIVO".equals(formaPago);
    }

    // Getters y Setters
    public CuentaPorCobrar getCuentaPorCobrar() { return cuentaPorCobrar; }
    public void setCuentaPorCobrar(CuentaPorCobrar cuentaPorCobrar) { this.cuentaPorCobrar = cuentaPorCobrar; }

    public Persona getCliente() { return cliente; }
    public void setCliente(Persona cliente) { this.cliente = cliente; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    public String getNumeroRecibo() { return numeroRecibo; }
    public void setNumeroRecibo(String numeroRecibo) { this.numeroRecibo = numeroRecibo; }

    public Date getFechaPago() { return fechaPago; }
    public void setFechaPago(Date fechaPago) { this.fechaPago = fechaPago; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getFormaPago() { return formaPago; }
    public void setFormaPago(String formaPago) { this.formaPago = formaPago; }

    public String getNumeroOperacion() { return numeroOperacion; }
    public void setNumeroOperacion(String numeroOperacion) { this.numeroOperacion = numeroOperacion; }

    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }

    public Date getFechaDeposito() { return fechaDeposito; }
    public void setFechaDeposito(Date fechaDeposito) { this.fechaDeposito = fechaDeposito; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }

    public Date getFechaVerificacion() { return fechaVerificacion; }
    public void setFechaVerificacion(Date fechaVerificacion) { this.fechaVerificacion = fechaVerificacion; }

    public String getUsuarioVerificacion() { return usuarioVerificacion; }
    public void setUsuarioVerificacion(String usuarioVerificacion) { this.usuarioVerificacion = usuarioVerificacion; }

    public Timestamp getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getUsuarioRegistro() { return usuarioRegistro; }
    public void setUsuarioRegistro(String usuarioRegistro) { this.usuarioRegistro = usuarioRegistro; }

    public String getTipoPago() { return tipoPago; }
    public void setTipoPago(String tipoPago) { this.tipoPago = tipoPago; }

    public Boolean getConciliado() { return conciliado; }
    public void setConciliado(Boolean conciliado) { this.conciliado = conciliado; }

    public Date getFechaConciliacion() { return fechaConciliacion; }
    public void setFechaConciliacion(Date fechaConciliacion) { this.fechaConciliacion = fechaConciliacion; }

    public String getUsuarioConciliacion() { return usuarioConciliacion; }
    public void setUsuarioConciliacion(String usuarioConciliacion) { this.usuarioConciliacion = usuarioConciliacion; }

    @Override
    public String toString() {
        return "PagoCliente{" +
                "numeroRecibo='" + numeroRecibo + '\'' +
                ", fechaPago=" + fechaPago +
                ", monto=" + monto +
                ", formaPago='" + formaPago + '\'' +
                ", estado='" + estado + '\'' +
                ", tipoPago='" + tipoPago + '\'' +
                ", cliente=" + (cliente != null ? cliente.getNombres() + " " + cliente.getApellidos() : "null") +
                '}';
    }
}