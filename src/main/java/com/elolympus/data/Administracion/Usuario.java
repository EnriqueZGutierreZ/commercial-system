package com.elolympus.data.Administracion;

import com.elolympus.data.AbstractEntity;
import com.elolympus.data.Auxiliar.CCA;
import com.elolympus.security.SecurityUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
@Entity
@Table(name = "usuario", schema = "administracion")
public class Usuario extends AbstractEntity {
    @Column(name = "usuario", length = 50, nullable = false)
    private String usuario;
    @JsonIgnore
    @Column(name = "password", length = 150, nullable = false)
    private String password;

    @OneToOne
    private Persona persona;

    @OneToOne
    private Rol rol;
    
    // Métodos necesarios para las vistas
    @Override
    public Long getId() {
        return super.getId();
    }
    
    @Override
    public boolean isActivo() {
        return super.isActivo();
    }
}
