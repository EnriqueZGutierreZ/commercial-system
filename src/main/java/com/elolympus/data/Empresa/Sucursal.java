package com.elolympus.data.Empresa;

import com.elolympus.data.AbstractEntity;
import com.elolympus.data.Administracion.Direccion;
import com.elolympus.data.Almacen.Almacen;
import com.elolympus.data.Auxiliar.Ubigeo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Sucursal extends AbstractEntity {
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
