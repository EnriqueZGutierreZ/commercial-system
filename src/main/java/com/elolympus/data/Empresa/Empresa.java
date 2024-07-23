package com.elolympus.data.Empresa;

import com.elolympus.data.AbstractEntity;
import com.elolympus.data.Auxiliar.CCA;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "empresa", schema = "empresa")
public class Empresa extends AbstractEntity{

    //++++++++++++++++++++++++++++ICCA+++++++++++++++++++++++++++++
    @Id
    @SequenceGenerator(
            name            =   "empresa_sequence",
            sequenceName    =   "empresa_sequence",
            allocationSize  =   1,
            initialValue    =   1
    )
    @GeneratedValue(
            strategy        =   GenerationType.SEQUENCE,
            generator       =   "empresa_sequence"
    )
    private Long id;
    @Column(name = "creado", nullable = false)
    private LocalDateTime creado;
    @Column(name = "creador", length = 200, nullable = false)
    private String creador;
    @Column(name = "activo", nullable = false)
    private Boolean activo;
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Column(name = "sucursal", nullable = false)
    private Integer sucursal;

    @Column(name = "direccion")
    private Integer direccion;

    @Column(name = "folder_temps")
    private String folderTemps;

    @Column(name = "folder_reports")
    private String folderReports;

    @Column(name = "allow_buy_without_stock")
    private Boolean allowBuyWithoutStock;

    @Column(name = "require_sales_pin")
    private Boolean requireSalesPin;

    @Column(name = "documento_tipo_xdefecto")
    private Integer documentoTipoXdefecto;

    @Column(name = "logo_enterprise")
    private String logoEnterprise;

    @Column(name = "logo_width")
    private String logoWidth;

    @Column(name = "commercial_name")
    private String commercialName;

    @PrePersist
    public void prePersist() {
        CCA cca     = new CCA();
        this.creado = cca.getCreado();
        this.creador= cca.getCreador();
        this.activo = cca.getActivo();
    }

}
