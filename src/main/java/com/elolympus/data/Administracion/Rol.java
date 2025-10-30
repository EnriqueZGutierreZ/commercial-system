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
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "rol", schema = "administracion")
public class Rol extends AbstractEntity {
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
    
}
