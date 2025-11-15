package com.elolympus.data.Administracion;

import com.elolympus.data.AbstractEntity;
import com.elolympus.data.Auxiliar.CCA;
import com.elolympus.security.SecurityUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, exclude = {"persona", "rol"})
@Entity
@Table(name = "usuario", schema = "administracion")
public class Usuario extends AbstractEntity {
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El usuario debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "El usuario solo puede contener letras, números, puntos, guiones y guiones bajos")
    @Column(name = "usuario", length = 50, nullable = false)
    private String usuario;
    
    @JsonIgnore
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 150, message = "La contraseña debe tener al menos 8 caracteres")
    @Column(name = "password", length = 150, nullable = false)
    private String password;

    @NotNull(message = "La persona es obligatoria")
    @OneToOne(fetch = FetchType.LAZY)
    private Persona persona;

    @NotNull(message = "El rol es obligatorio")
    @OneToOne(fetch = FetchType.LAZY)
    private Rol rol;
    
    // Métodos necesarios para las vistas
    public Long getId() {
        return this.id;
    }
    
    public boolean isActivo() {
        return this.activo;
    }
}
