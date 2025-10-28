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
    
    // Getters y setters manuales por si Lombok no funciona
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

//    @OneToOne
//    private Usuario usuario;


}
