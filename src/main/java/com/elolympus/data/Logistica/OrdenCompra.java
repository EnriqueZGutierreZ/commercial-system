package com.elolympus.data.Logistica;

import com.elolympus.data.AbstractEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
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
    private final LocalDateTime creado;
    @Column(name = "creador", length = 200, nullable = false)
    private final String creador;
    @Column(name = "activo", nullable = false)
    private final Boolean activo;
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
    private final List<OrdenCompraDet> detalles;



}