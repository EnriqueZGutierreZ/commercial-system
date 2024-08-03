package com.elolympus.data.Administracion;

import com.elolympus.data.AbstractEntity;
import com.elolympus.data.Auxiliar.CCA;
import com.elolympus.security.SecurityUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rol", schema = "administracion")
public class Rol {
    //++++++++++++++++++++++++++++ICCA+++++++++++++++++++++++++++++
    @Id
    @SequenceGenerator(
            name            =   "rol_sequence",
            sequenceName    =   "rol_sequence",
            allocationSize  =   1,
            initialValue    =   1
    )
    @GeneratedValue(
            strategy        =   GenerationType.SEQUENCE,
            generator       =   "rol_sequence"
    )
    private Long id;
    @Column(name = "creado", nullable = false)
    private LocalDateTime creado;
    @Column(name = "creador", length = 200, nullable = false)
    private String creador;
    @Column(name = "activo", nullable = false)
    private boolean activo;
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Column(name = "area", length = 100, nullable = false)
    private String area;
    @Column(name = "cargo", length = 100, nullable = false)
    private String cargo;
    @Column(name = "descripcion", length = 250, nullable = false)
    private String descripcion;
    //CRUD
    @Column(name = "can_create", nullable = false)
    private Boolean canCreate;
    @Column(name = "can_read", nullable = false)
    private Boolean canRead;
    @Column(name = "can_update", nullable = false)
    private Boolean canUpdate;
    @Column(name = "can_delete", nullable = false)
    private Boolean canDelete;

//    @OneToOne
//    private Usuario usuario;

    @PrePersist
    public void prePersist() {
        CCA cca     = new CCA();
        this.creado = cca.getCreado();
        this.creador= cca.getCreador();
        this.activo = cca.getActivo();
    }

}
