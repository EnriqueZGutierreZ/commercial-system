package com.elolympus.data.Administracion;

import com.elolympus.data.Auxiliar.CCA;
import com.elolympus.data.Auxiliar.Ubigeo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "direccion", schema = "administracion")
public class Direccion {

    //public AuthenticatedUser authenticatedUser;
    //++++++++++++++++++++++++++++ICCA+++++++++++++++++++++++++++++
    @Id
    @SequenceGenerator(
            name            =   "direccion_sequence",
            sequenceName    =   "direccion_sequence",
            allocationSize  =   1,
            initialValue    =   1
    )
    @GeneratedValue(
            strategy        =   GenerationType.SEQUENCE,
            generator       =   "direccion_sequence"
    )
    private Long id;
    @Column(name = "creado", nullable = false)
    private LocalDateTime creado;
    @Column(name = "creador", length = 200, nullable = false)
    private String creador;
    @Column(name = "activo", nullable = false)
    private boolean activo;
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    @Column(name = "descripcion", length = 250, nullable = false)
    public String descripcion;
    @Column(name = "referencia", length = 200, nullable = false)
    public String referencia;

    @OneToOne
    public Ubigeo ubigeo;

    @PrePersist
    public void prePersist() {
        CCA cca     = new CCA();
        this.creado = cca.getCreado();
        this.creador= cca.getCreador();
        this.activo = cca.getActivo();
    }
}
