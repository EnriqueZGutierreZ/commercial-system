package com.elolympus.data.ubigeo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Distrito {
    @Id
    private String id;
    private String nombre;
    private String provinciaId;
    private String departamentoId;
    
    // Getters y setters manuales por si Lombok no funciona
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getProvinciaId() {
        return provinciaId;
    }
    
    public void setProvinciaId(String provinciaId) {
        this.provinciaId = provinciaId;
    }
    
    public String getDepartamentoId() {
        return departamentoId;
    }
    
    public void setDepartamentoId(String departamentoId) {
        this.departamentoId = departamentoId;
    }
}
