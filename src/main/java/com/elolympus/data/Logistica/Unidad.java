package com.elolympus.data.Logistica;

import com.elolympus.data.AbstractEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "unidad", schema = "logistica")
public class Unidad extends AbstractEntity {

    @Column(name = "nombre", length = 50, nullable = false)
    private String nombre;

    @Column(name = "abreviatura", length = 10, nullable = false)
    private String abreviatura;

    @Column(name = "descripcion", length = 100)
    private String descripcion;
}