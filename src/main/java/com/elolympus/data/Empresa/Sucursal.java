package com.elolympus.data.Empresa;

import com.elolympus.data.AbstractEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public class Sucursal extends AbstractEntity {
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
    @Column(name = "codigo", nullable = false)
    private Integer codigo; //1 2 3
    @Column(name = "descripcion", nullable = false)
    private String descripcion;
    @Column(name = "direccion", nullable = false)
    private Integer direccion;
    @Column(name = "empresa", nullable = false)
    private Empresa empresa;
    @Column(name = "serie", nullable = false)
    private Integer serie;




}
