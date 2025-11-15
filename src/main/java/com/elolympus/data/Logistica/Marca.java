package com.elolympus.data.Logistica;

import com.elolympus.data.AbstractEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "marca", schema = "logistica")
public class Marca extends AbstractEntity {

    @NotBlank(message = "El nombre de la marca es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Size(max = 250, message = "La descripción no puede exceder 250 caracteres")
    @Column(name = "descripcion", length = 250)
    private String descripcion;

    @Size(min = 2, max = 20, message = "El código debe tener entre 2 y 20 caracteres")
    @Pattern(regexp = "^[A-Z0-9-_]*$", message = "El código solo puede contener letras mayúsculas, números, guiones y guiones bajos")
    @Column(name = "codigo", length = 20, unique = true)
    private String codigo;
}