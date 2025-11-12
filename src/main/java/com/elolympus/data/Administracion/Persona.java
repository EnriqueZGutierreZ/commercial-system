package com.elolympus.data.Administracion;

import com.elolympus.data.AbstractEntity;
import com.elolympus.data.Auxiliar.CCA;
import com.elolympus.security.SecurityUtils;
import com.vaadin.flow.data.binder.Binder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//Constructor Vacio - get - set - equals - toString

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "persona", schema = "administracion")
public class Persona extends AbstractEntity {

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

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.EAGER)
    private Direccion direccion;

//    @OneToOne
//    private Usuario usuario;


    // Método para obtener el nombre completo
    public String getNombreCompleto() {
        return nombres + " " + apellidos;

    }
    
    // Método getId() necesario para las vistas
    public Long getId() {
        return this.id;
    }
    
    public boolean isActivo() {
        return this.activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

}
