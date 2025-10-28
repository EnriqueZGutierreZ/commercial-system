package com.elolympus.data.Auxiliar;

import com.elolympus.data.AbstractEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Created by [EnriqueZGutierreZ]
 */
//Constructor Vacio - get - set - equals - toString

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ubigeo", schema = "auxiliar")
public class Ubigeo{

    @Id
    @SequenceGenerator(
            name            =   "ubigeo_sequence",
            sequenceName    =   "ubigeo_sequence",
            allocationSize  =   1,
            initialValue    =   10
    )
    @GeneratedValue(
            strategy        =   GenerationType.SEQUENCE,
            generator       =   "ubigeo_sequence"
    )
    private Long id;
    @Column(name = "codigo", nullable = false)
    private String codigo;
    @Column(name = "departamento", nullable = false)
    private String departamento;
    @Column(name = "provincia", nullable = false)
    private String provincia;
    @Column(name = "distrito", nullable = false)
    private String distrito;
    @Column(name = "descripcion", nullable = true)
    private String descripcion;
    
    // Getters y setters manuales por si Lombok no funciona
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getDepartamento() {
        return departamento;
    }
    
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
    
    public String getProvincia() {
        return provincia;
    }
    
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }
    
    public String getDistrito() {
        return distrito;
    }
    
    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}