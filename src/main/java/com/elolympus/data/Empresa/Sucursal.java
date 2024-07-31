package com.elolympus.data.Empresa;

import com.elolympus.data.AbstractEntity;
import com.elolympus.data.Administracion.Direccion;
import com.elolympus.data.Almacen.Almacen;
import com.elolympus.data.Auxiliar.Ubigeo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Sucursal {
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
    @Column(name = "principal", nullable = false)
    private boolean principal;
    @Column(name = "codigo", nullable = false)
    private Integer codigo; //1 2 3
    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Empresa empresa;

    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Almacen> almacenes;

    @OneToOne(cascade = CascadeType.ALL)
    private Direccion direccion;

    @Column(name = "serie", nullable = false)
    private Integer serie;




}
