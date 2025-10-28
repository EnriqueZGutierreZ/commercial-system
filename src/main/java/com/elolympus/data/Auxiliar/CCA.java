package com.elolympus.data.Auxiliar;

import com.elolympus.security.SecurityUtils;
import jakarta.persistence.PrePersist;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Created by [EnriqueZGutierreZ]
 */
@Data
public class CCA {
    //++++++++++++++++++++++++++++CCA+++++++++++++++++++++++++++++
    private LocalDateTime creado;
    private String creador;
    private Boolean activo;
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    
    // Getters y setters manuales por si Lombok no funciona
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

    public CCA() {
        this.creado = LocalDateTime.now();
        try {
            this.creador = SecurityUtils.obtenerNombreUsuarioActual();
        }catch (Exception e){
            System.out.println("ERROR al obtener el usuario: " + e.toString());
        }
        this.activo = true;
    }
}
