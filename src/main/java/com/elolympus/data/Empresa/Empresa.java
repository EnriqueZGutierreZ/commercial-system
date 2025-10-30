package com.elolympus.data.Empresa;

import com.elolympus.data.AbstractEntity;
//import com.elolympus.data.Auxiliar.CCA;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "empresa", schema = "empresa")
public class Empresa extends AbstractEntity {
//    @Column(name = "sucursal", nullable = false)
//    private Integer sucursal;
    @Column(name = "direccion")
    private Integer direccion;
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sucursal> sucursales;

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

}
