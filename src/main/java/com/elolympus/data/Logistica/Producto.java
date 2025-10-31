package com.elolympus.data.Logistica;

import com.elolympus.data.AbstractEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "producto", schema = "logistica")
public class Producto extends AbstractEntity {

    @Column(name = "codigo", length = 50, nullable = false, unique = true)
    private String codigo;

    @Column(name = "nombre", length = 200, nullable = false)
    private String nombre;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "marca_id", nullable = false)
    private Marca marca;

    @ManyToOne
    @JoinColumn(name = "linea_id", nullable = false)
    private Linea linea;

    @ManyToOne
    @JoinColumn(name = "unidad_id", nullable = false)
    private Unidad unidad;

    @Column(name = "precio_costo", precision = 10, scale = 2)
    private BigDecimal precioCosto;

    @Column(name = "precio_venta", precision = 10, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "stock_minimo", precision = 10, scale = 2)
    private BigDecimal stockMinimo;

    @Column(name = "stock_maximo", precision = 10, scale = 2)
    private BigDecimal stockMaximo;

    @Column(name = "peso", precision = 10, scale = 3)
    private BigDecimal peso;

    @Column(name = "volumen", precision = 10, scale = 3)
    private BigDecimal volumen;

    // Explicit getter methods for compatibility with Vaadin Grid
    // Uses the 'activo' field inherited from AbstractEntity
    public Boolean isEsActivo() {
        return activo;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}