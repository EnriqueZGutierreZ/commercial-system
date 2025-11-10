package com.elolympus.data.Logistica;

import com.elolympus.data.AbstractEntity;
import com.elolympus.data.Almacen.Almacen;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orden_compra_det",schema = "logistica")
public class OrdenCompraDet extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_compra", referencedColumnName = "id")
    private OrdenCompra ordenCompra;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "cantidad")
    private BigDecimal cantidad;

    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;

    @Column(name = "total_det")
    private BigDecimal totalDet;

    @Column(name = "descuento")
    private BigDecimal descuento;

    @ManyToOne
    @JoinColumn(name = "almacen_id")
    private Almacen almacen;

    @Column(name = "cantidad_tg")
    private BigDecimal cantidadTg;

    @Column(name = "lote")
    private String lote;

    @Column(name = "fecha_vencimiento")
    private Date fechaVencimiento;

    @Column(name = "cantidad_usada")
    private BigDecimal cantidadUsada;

    @Column(name = "cantidad_fraccion")
    private BigDecimal cantidadFraccion;
    
}
