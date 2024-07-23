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
