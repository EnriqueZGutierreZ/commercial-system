package com.elolympus.data.Administracion;

import com.elolympus.data.AbstractEntity;
import com.elolympus.security.AuthenticatedUser;
import com.elolympus.security.SecurityUtils;
import jakarta.persistence.*;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Table(name = "persona", schema = "administracion")
public class Persona extends AbstractEntity {

    //public AuthenticatedUser authenticatedUser;
    @Column(name = "creado", nullable = false)
    private LocalDateTime creado;
    @Column(name = "creador", length = 200, nullable = false)
    private String creador;
    @Column(name = "activo", nullable = false)
    private Boolean activo;
    @Column(name = "apellidos", length = 250, nullable = false)
    private String apellidos;
    @Column(name = "nombres", length = 250, nullable = false)
    private String nombres;
    @Column(name = "sexo", length = 1, nullable = false)
    private String sexo;

    //int 1-DNI 2-RUC 3-CARNET-EXTREANJERA
    @Column(name = "tipo_documento", length = 1, nullable = false)
    private Integer tipo_documento;
    @Column(name = "num_documento", length = 30, nullable = false)
    private Integer num_documento;
    @Column(name = "email", length = 250, nullable = false)
    private String email;
    @Column(name = "celular", length = 15, nullable = false)
    private Integer celular;

    @OneToOne
    @JoinColumn(name = "persona_id") // Ajusta el nombre de la columna según tu esquema
    private Usuario usuario;

    public Persona() {
    }

    public Persona(String apellidos, String nombres, String sexo, Integer tipo_documento, Integer num_documento, String email, Integer celular) {
        this.apellidos = apellidos;
        this.nombres = nombres;
        this.sexo = sexo;
        this.tipo_documento = tipo_documento;
        this.num_documento = num_documento;
        this.email = email;
        this.celular = celular;

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

    @Override
    public String toString() {
        return "Persona{" +
                "apellidos='" + apellidos + '\'' +
                ", nombres='" + nombres + '\'' +
                ", tipo_documento=" + tipo_documento +
                ", num_documento=" + num_documento +
                '}';
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

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public Integer getTipo_documento() {
        return tipo_documento;
    }

    public void setTipo_documento(Integer tipo_documento) {
        this.tipo_documento = tipo_documento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getNum_documento() {
        return num_documento;
    }

    public void setNum_documento(Integer num_documento) {
        this.num_documento = num_documento;
    }

    public Integer getCelular() {
        return celular;
    }

    public void setCelular(Integer celular) {
        this.celular = celular;
    }

    // Método para obtener el nombre completo
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

}
