package com.elolympus.data.Administracion;

import com.elolympus.data.AbstractEntity;
import com.elolympus.security.SecurityUtils;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "rol", schema = "administracion")
public class Rol extends AbstractEntity {
    //++++++++++++++++++++++++++++ICCA+++++++++++++++++++++++++++++
    @Column(name = "creado", nullable = false)
    private LocalDateTime creado;
    @Column(name = "creador", length = 200, nullable = false)
    private String creador;
    @Column(name = "activo", nullable = false)
    private Boolean activo;
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

    @OneToOne
    @JoinColumn(name="id")
    private Usuario usuario;

    public Rol() {
    }

    public Rol(LocalDateTime creado, String creador, Boolean activo, String area, String cargo, String descripcion, Boolean canCreate, Boolean canRead, Boolean canUpdate, Boolean canDelete) {
        this.creado = creado;
        this.creador = creador;
        this.activo = activo;
        this.area = area;
        this.cargo = cargo;
        this.descripcion = descripcion;
        this.canCreate = canCreate;
        this.canRead = canRead;
        this.canUpdate = canUpdate;
        this.canDelete = canDelete;
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
        return "Rol{" +
                "creado=" + creado +
                ", creador='" + creador + '\'' +
                ", activo=" + activo +
                ", area='" + area + '\'' +
                ", cargo='" + cargo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", canCreate=" + canCreate +
                ", canRead=" + canRead +
                ", canUpdate=" + canUpdate +
                ", canDelete=" + canDelete +
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getCanCreate() {
        return canCreate;
    }

    public void setCanCreate(Boolean canCreate) {
        this.canCreate = canCreate;
    }

    public Boolean getCanRead() {
        return canRead;
    }

    public void setCanRead(Boolean canRead) {
        this.canRead = canRead;
    }

    public Boolean getCanUpdate() {
        return canUpdate;
    }

    public void setCanUpdate(Boolean canUpdate) {
        this.canUpdate = canUpdate;
    }

    public Boolean getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(Boolean canDelete) {
        this.canDelete = canDelete;
    }
}
