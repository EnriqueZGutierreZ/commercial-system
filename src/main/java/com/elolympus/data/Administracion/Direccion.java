package com.elolympus.data.Administracion;

import com.elolympus.data.AbstractEntity;
import com.elolympus.data.Auxiliar.CCA;
import com.elolympus.data.Auxiliar.Ubigeo;
import com.elolympus.data.Empresa.Sucursal;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "direccion", schema = "administracion")
public class Direccion extends AbstractEntity {

    @Column(name = "descripcion", length = 250, nullable = false)
    public String descripcion;
    @Column(name = "referencia", length = 200, nullable = false)
    public String referencia;

//    @OneToOne(mappedBy = "direccion")
//    private Sucursal sucursal;
//
//    @OneToOne(mappedBy = "direccion")
//    private Persona persona;

    @ManyToOne
    @JoinColumn(name = "ubigeo_id")
    public Ubigeo ubigeo;
    
    // Getters y setters manuales por si Lombok no funciona
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getReferencia() {
        return referencia;
    }
    
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
    
    public Ubigeo getUbigeo() {
        return ubigeo;
    }
    
    public void setUbigeo(Ubigeo ubigeo) {
        this.ubigeo = ubigeo;
    }

}
