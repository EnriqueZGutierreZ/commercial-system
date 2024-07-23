package com.elolympus.data.ubigeo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Distrito {
    @Id
    private String id;
    private String nombre;
    private String provinciaId;
    private String departamentoId;

}
