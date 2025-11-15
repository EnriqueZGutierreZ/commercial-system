package com.elolympus.data.Logistica;

import com.elolympus.data.AbstractEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, exclude = {"marca", "linea", "unidad"})
@Entity
@Table(name = "producto", schema = "logistica")
public class Producto extends AbstractEntity {

    @NotBlank(message = "El código del producto es obligatorio")
    @Size(min = 3, max = 50, message = "El código debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[A-Z0-9-_]+$", message = "El código solo puede contener letras mayúsculas, números, guiones y guiones bajos")
    @Column(name = "codigo", length = 50, nullable = false, unique = true)
    private String codigo;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 2, max = 200, message = "El nombre debe tener entre 2 y 200 caracteres")
    @Column(name = "nombre", length = 200, nullable = false)
    private String nombre;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @NotNull(message = "La marca es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marca_id", nullable = false)
    private Marca marca;

    @NotNull(message = "La línea es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linea_id", nullable = false)
    private Linea linea;

    @NotNull(message = "La unidad es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_id", nullable = false)
    private Unidad unidad;

    @DecimalMin(value = "0.0", inclusive = false, message = "El precio de costo debe ser mayor que 0")
    @Digits(integer = 8, fraction = 2, message = "El precio de costo debe tener máximo 8 enteros y 2 decimales")
    @Column(name = "precio_costo", precision = 10, scale = 2)
    private BigDecimal precioCosto;

    @DecimalMin(value = "0.0", inclusive = false, message = "El precio de venta debe ser mayor que 0")
    @Digits(integer = 8, fraction = 2, message = "El precio de venta debe tener máximo 8 enteros y 2 decimales")
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