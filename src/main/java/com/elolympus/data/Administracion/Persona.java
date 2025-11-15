package com.elolympus.data.Administracion;

import com.elolympus.data.AbstractEntity;
import com.elolympus.data.Auxiliar.CCA;
import com.elolympus.security.SecurityUtils;
import com.vaadin.flow.data.binder.Binder;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//Constructor Vacio - get - set - equals - toString

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, exclude = {"direccion"})
@Entity
@Table(name = "persona", schema = "administracion")
public class Persona extends AbstractEntity {

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 2, max = 250, message = "Los apellidos deben tener entre 2 y 250 caracteres")
    @Column(name = "apellidos", length = 250, nullable = false)
    private String apellidos;
    
    @NotBlank(message = "Los nombres son obligatorios")
    @Size(min = 2, max = 250, message = "Los nombres deben tener entre 2 y 250 caracteres")
    @Column(name = "nombres", length = 250, nullable = false)
    private String nombres;
    
    @NotBlank(message = "El sexo es obligatorio")
    @Pattern(regexp = "[MF]", message = "El sexo debe ser M (Masculino) o F (Femenino)")
    @Column(name = "sexo", length = 1, nullable = false)
    private String sexo;
    
    @NotNull(message = "El tipo de documento es obligatorio")
    @Min(value = 1, message = "Tipo de documento inválido")
    @Max(value = 4, message = "Tipo de documento inválido")
    @Column(name = "tipo_documento", length = 1, nullable = false)
    private Integer tipo_documento;
    
    @NotNull(message = "El número de documento es obligatorio")
    @Min(value = 10000000, message = "El documento debe tener al menos 8 dígitos")
    @Max(value = 99999999999L, message = "El documento no puede tener más de 11 dígitos")
    @Column(name = "num_documento", length = 30, nullable = false)
    private Integer num_documento;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 250, message = "El email no puede exceder 250 caracteres")
    @Column(name = "email", length = 250, nullable = false)
    private String email;
    
    @NotNull(message = "El celular es obligatorio")
    @Min(value = 900000000, message = "El celular debe tener 9 dígitos y empezar con 9")
    @Max(value = 999999999, message = "El celular debe tener 9 dígitos")
    @Column(name = "celular", length = 15, nullable = false)
    private Integer celular;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.LAZY)
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
