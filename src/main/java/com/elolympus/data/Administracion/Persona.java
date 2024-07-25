package com.elolympus.data.Administracion;

import com.elolympus.data.AbstractEntity;
import com.elolympus.data.Auxiliar.CCA;
import com.elolympus.security.SecurityUtils;
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
@Entity
@Table(name = "persona", schema = "administracion")
public class Persona {

    //public AuthenticatedUser authenticatedUser;
    //++++++++++++++++++++++++++++ICCA+++++++++++++++++++++++++++++
    @Id
    @SequenceGenerator(
            name            =   "persona_sequence",
            sequenceName    =   "persona_sequence",
            allocationSize  =   1,
            initialValue    =   1
    )
    @GeneratedValue(
            strategy        =   GenerationType.SEQUENCE,
            generator       =   "persona_sequence"
    )
    private Long id;
    @Column(name = "creado", nullable = false)
    private LocalDateTime creado;
    @Column(name = "creador", length = 200, nullable = false)
    private String creador;
    @Column(name = "activo", nullable = false)
    private boolean activo;
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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

    @OneToOne
    //@JoinColumn(name = "persona_id") // Ajusta el nombre de la columna según tu esquema
    private Usuario usuario;

    @PrePersist
    public void prePersist() {
        CCA cca     = new CCA();
        this.creado = cca.getCreado();
        this.creador= cca.getCreador();
        this.activo = cca.getActivo();
    }

    // Método para obtener el nombre completo
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

}
