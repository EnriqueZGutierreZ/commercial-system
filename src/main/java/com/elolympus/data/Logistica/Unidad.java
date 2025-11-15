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
@Table(name = "unidad", schema = "logistica")
public class Unidad extends AbstractEntity {

    @NotBlank(message = "El nombre de la unidad es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Column(name = "nombre", length = 50, nullable = false)
    private String nombre;

    @NotBlank(message = "La abreviatura es obligatoria")
    @Size(min = 1, max = 10, message = "La abreviatura debe tener entre 1 y 10 caracteres")
    @Pattern(regexp = "^[A-Z]+$", message = "La abreviatura solo puede contener letras mayúsculas")
    @Column(name = "abreviatura", length = 10, nullable = false)
    private String abreviatura;

    @Size(max = 100, message = "La descripción no puede exceder 100 caracteres")
    @Column(name = "descripcion", length = 100)
    private String descripcion;
}