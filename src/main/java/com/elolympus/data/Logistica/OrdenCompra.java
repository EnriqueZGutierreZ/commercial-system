package com.elolympus.data.Logistica;

import com.elolympus.data.AbstractEntity;
import com.elolympus.data.Auxiliar.CCA;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;



@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orden_compra", schema = "logistica")
public class OrdenCompra extends AbstractEntity {

    //++++++++++++++++++++++++++++ICCA+++++++++++++++++++++++++++++
    @Id
    @SequenceGenerator(
            name            =   "orden_compra_sequence",
            sequenceName    =   "orden_compra_sequence",
            allocationSize  =   1,
            initialValue    =   1
    )
    @GeneratedValue(
            strategy        =   GenerationType.SEQUENCE,
            generator       =   "orden_compra_sequence"
    )
    private Long id;
    @Column(name = "creado", nullable = false)
    private LocalDateTime creado;
    @Column(name = "creador", length = 200, nullable = false)
    private String creador;
    @Column(name = "activo", nullable = false)
    private Boolean activo;
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Column(name = "almacen_entrega")
    private Integer almacenEntrega;

    @Column(name = "numero_proveedor")
    private Integer numeroProveedor;

    @Column(name = "direccion_proveedor")
    private Integer direccionProveedor;

    @Column(name = "fecha")
    private Date fecha;

    @Column(name = "fecha_entrega")
    private Date fechaEntrega;

    @Column(name = "forma_pago")
    private Integer formaPago;

    @Column(name = "moneda")
    private Integer moneda;

    @Column(name = "impuesto")
    private Integer impuesto;

    @Column(name = "total")
    private BigDecimal total;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "total_cobrado")
    private BigDecimal totalCobrado;

    @Column(name = "tipo_cambio")
    private BigDecimal tipoCambio;

    @Column(name = "dias_credito")
    private Integer diasCredito;

    @Column(name = "sucursal")
    private Integer sucursal;

    @Column(name = "impuesto_incluido")
    private Boolean impuesto_incluido;
    @Column(name="documento_pago")
    private String documento_pago;

    //RELACION
    @OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdenCompraDet> detalles;

    @PrePersist
    public void prePersist() {
        CCA cca     = new CCA();
        this.creado = cca.getCreado();
        this.creador= cca.getCreador();
        this.activo = cca.getActivo();
    }
    
    // Getters y setters manuales por si Lombok no funciona
    public Integer getAlmacenEntrega() {
        return almacenEntrega;
    }
    
    public void setAlmacenEntrega(Integer almacenEntrega) {
        this.almacenEntrega = almacenEntrega;
    }
    
    public Integer getNumeroProveedor() {
        return numeroProveedor;
    }
    
    public void setNumeroProveedor(Integer numeroProveedor) {
        this.numeroProveedor = numeroProveedor;
    }
    
    public Integer getDireccionProveedor() {
        return direccionProveedor;
    }
    
    public void setDireccionProveedor(Integer direccionProveedor) {
        this.direccionProveedor = direccionProveedor;
    }
    
    public Date getFecha() {
        return fecha;
    }
    
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    
    public Date getFechaEntrega() {
        return fechaEntrega;
    }
    
    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }
    
    public Integer getFormaPago() {
        return formaPago;
    }
    
    public void setFormaPago(Integer formaPago) {
        this.formaPago = formaPago;
    }
    
    public Integer getMoneda() {
        return moneda;
    }
    
    public void setMoneda(Integer moneda) {
        this.moneda = moneda;
    }
    
    public Integer getImpuesto() {
        return impuesto;
    }
    
    public void setImpuesto(Integer impuesto) {
        this.impuesto = impuesto;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public BigDecimal getTotalCobrado() {
        return totalCobrado;
    }
    
    public void setTotalCobrado(BigDecimal totalCobrado) {
        this.totalCobrado = totalCobrado;
    }
    
    public BigDecimal getTipoCambio() {
        return tipoCambio;
    }
    
    public void setTipoCambio(BigDecimal tipoCambio) {
        this.tipoCambio = tipoCambio;
    }
    
    public Integer getDiasCredito() {
        return diasCredito;
    }
    
    public void setDiasCredito(Integer diasCredito) {
        this.diasCredito = diasCredito;
    }
    
    public Integer getSucursal() {
        return sucursal;
    }
    
    public void setSucursal(Integer sucursal) {
        this.sucursal = sucursal;
    }
    
    public Boolean getImpuesto_incluido() {
        return impuesto_incluido;
    }
    
    public void setImpuesto_incluido(Boolean impuesto_incluido) {
        this.impuesto_incluido = impuesto_incluido;
    }
    
    public String getDocumento_pago() {
        return documento_pago;
    }
    
    public void setDocumento_pago(String documento_pago) {
        this.documento_pago = documento_pago;
    }
    
    public List<OrdenCompraDet> getDetalles() {
        return detalles;
    }
    
    public void setDetalles(List<OrdenCompraDet> detalles) {
        this.detalles = detalles;
    }



}