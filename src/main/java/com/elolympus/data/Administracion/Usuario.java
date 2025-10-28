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
    
    // Getters y setters manuales por si Lombok no funciona
    public String getUsuario() {
        return usuario;
    }
    
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Persona getPersona() {
        return persona;
    }
    
    public void setPersona(Persona persona) {
        this.persona = persona;
    }
    
    public Rol getRol() {
        return rol;
    }
    
    public void setRol(Rol rol) {
        this.rol = rol;
    }
    
    // Método getId() necesario para las vistas
    public Long getId() {
        return super.getId();
    }
    
    // Método isActivo() necesario para las vistas
    public boolean isActivo() {
        return super.isActivo();
    }
}
