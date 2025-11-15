package com.elolympus.data.Administracion;

import com.elolympus.data.AbstractEntity;
import com.elolympus.data.Auxiliar.CCA;
import com.elolympus.security.SecurityUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    @NotBlank(message = "El área es obligatoria")
    @Size(min = 2, max = 100, message = "El área debe tener entre 2 y 100 caracteres")
    @Column(name = "area", length = 100, nullable = false)
    private String area;
    
    @NotBlank(message = "El cargo es obligatorio")
    @Size(min = 2, max = 100, message = "El cargo debe tener entre 2 y 100 caracteres")
    @Column(name = "cargo", length = 100, nullable = false)
    private String cargo;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 5, max = 250, message = "La descripción debe tener entre 5 y 250 caracteres")
    @Column(name = "descripcion", length = 250, nullable = false)
    private String descripcion;
    
    @NotNull(message = "El permiso de creación es obligatorio")
    @Column(name = "can_create", nullable = false)
    private Boolean canCreate;
    
    @NotNull(message = "El permiso de lectura es obligatorio")
    @Column(name = "can_read", nullable = false)
    private Boolean canRead;
    
    @NotNull(message = "El permiso de actualización es obligatorio")
    @Column(name = "can_update", nullable = false)
    private Boolean canUpdate;
    
    @NotNull(message = "El permiso de eliminación es obligatorio")
    @Column(name = "can_delete", nullable = false)
    private Boolean canDelete;
    
}
