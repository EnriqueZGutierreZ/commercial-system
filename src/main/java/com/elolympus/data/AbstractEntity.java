package com.elolympus.data;

import com.elolympus.data.Auxiliar.CCA;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@MappedSuperclass
public abstract class AbstractEntity {

    //++++++++++++++++++++++++++++ICCA+++++++++++++++++++++++++++++
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "idgenerator")
    // The initial value is to account for data.sql demo data ids
    @SequenceGenerator(
            name = "idgenerator",
            initialValue = 100)
    protected Long id;

    @Column(name = "creado", nullable = false)
    protected LocalDateTime creado;
    @Column(name = "creador", length = 200, nullable = false)
    protected String creador;
    @Column(name = "activo", nullable = false)
    protected boolean activo;
    @Version
    protected Long version;
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    @PrePersist
    public void prePersist() {
        CCA cca     = new CCA();
        this.creado = cca.getCreado();
        this.creador= cca.getCreador();
        this.activo = cca.getActivo();
    }
    
}
