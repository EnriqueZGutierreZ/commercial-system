package com.elolympus.data.Administracion;

import com.elolympus.data.AbstractEntity;
import com.elolympus.security.SecurityUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "usuario", schema = "administracion")
public class Usuario extends AbstractEntity {
    //++++++++++++++++++++++++++++ICCA+++++++++++++++++++++++++++++
    @Column(name = "creado", nullable = false)
    private LocalDateTime creado;
    @Column(name = "creador", length = 200, nullable = false)
    private String creador;
    @Column(name = "activo", nullable = false)
    private Boolean activo;
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    @ManyToOne
    private Rol rol;
    @Column(name = "usuario", length = 50, nullable = false)
    private String usuario;
    @JsonIgnore
    @Column(name = "password", length = 150, nullable = false)
    private String password;
    @OneToOne
    private Persona persona;

    public Usuario() {
    }

    public Usuario(String creador, Rol rol, String usuario, String password, Persona persona) {
        this.creador = creador;
        this.rol = rol;
        this.usuario = usuario;
        this.password = password;
        this.persona = persona;
    }

    @PrePersist
    public void prePersist() {
        this.creado = LocalDateTime.now();
        try {
            this.creador = SecurityUtils.obtenerNombreUsuarioActual();
        }catch (Exception e){
            System.out.println("ERROR al obtner el usuario: " + e.toString());
        }
        this.activo = true;
    }
    public LocalDateTime getCreado() {
        return creado;
    }

    public void setCreado(LocalDateTime creado) {
        this.creado = creado;
    }

    public String getCreador() {
        return creador;
    }

    public void setCreador(String creador) {
        this.creador = creador;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

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

}
