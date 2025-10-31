package com.elolympus.data.ubigeo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import jakarta.persistence.Id;

@Entity
@Data
public class Departamento {
    @Id
    private String id;
    private String nombre;
}
