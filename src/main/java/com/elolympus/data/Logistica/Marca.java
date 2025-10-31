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
@Table(name = "marca", schema = "logistica")
public class Marca extends AbstractEntity {

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "descripcion", length = 250)
    private String descripcion;

    @Column(name = "codigo", length = 20, unique = true)
    private String codigo;
}