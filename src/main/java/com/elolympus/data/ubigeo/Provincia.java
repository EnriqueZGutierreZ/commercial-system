package com.elolympus.data.ubigeo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Provincia {
    @Id
    private String id;
    private String nombre;
    private String departamentoId;
}
