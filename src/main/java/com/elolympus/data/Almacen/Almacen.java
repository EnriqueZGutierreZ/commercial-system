package com.elolympus.data.Almacen;

import com.elolympus.data.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


//Constructor Vacio - get - set - equals - toString
@Data
@AllArgsConstructor
@Entity
@Table(name = "almacen", schema = "almacen")
public class Almacen extends AbstractEntity {

    //++++++++++++++++++++++++++++ICCA+++++++++++++++++++++++++++++
    @Id
    @SequenceGenerator(
            name            =   "almacen_sequence",
            sequenceName    =   "almacen_sequence",
            allocationSize  =   1,
            initialValue    =   1
    )
    @GeneratedValue(
            strategy        =   GenerationType.SEQUENCE,
            generator       =   "almacen_sequence"
    )
    private Long id;
    @Column(name = "creado", nullable = false)
    private LocalDateTime creado;
    @Column(name = "creador", length = 200, nullable = false)
    private String creador;
    @Column(name = "activo", nullable = false)
    private Boolean activo;
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Column(name = "sucursal", nullable = false)
    private Integer sucursal;
    @Column(name = "codigo", nullable = false)
    private Integer Codigo; //1 2 3
    @Column(name = "descripcion", nullable = false)
    private String Descripcion;
}
