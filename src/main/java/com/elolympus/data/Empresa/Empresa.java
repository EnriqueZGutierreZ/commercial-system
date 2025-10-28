package com.elolympus.data.Empresa;

import com.elolympus.data.AbstractEntity;
import com.elolympus.data.Auxiliar.CCA;
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
//    @Column(name = "direccion")
//    private Integer direccion;
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

    // Getters y setters manuales por si Lombok no funciona correctamente
    public String getFolderTemps() {
        return folderTemps;
    }

    public void setFolderTemps(String folderTemps) {
        this.folderTemps = folderTemps;
    }

    public String getFolderReports() {
        return folderReports;
    }

    public void setFolderReports(String folderReports) {
        this.folderReports = folderReports;
    }

    public Boolean getAllowBuyWithoutStock() {
        return allowBuyWithoutStock;
    }

    public void setAllowBuyWithoutStock(Boolean allowBuyWithoutStock) {
        this.allowBuyWithoutStock = allowBuyWithoutStock;
    }

    public Boolean getRequireSalesPin() {
        return requireSalesPin;
    }

    public void setRequireSalesPin(Boolean requireSalesPin) {
        this.requireSalesPin = requireSalesPin;
    }

    public Integer getDocumentoTipoXdefecto() {
        return documentoTipoXdefecto;
    }

    public void setDocumentoTipoXdefecto(Integer documentoTipoXdefecto) {
        this.documentoTipoXdefecto = documentoTipoXdefecto;
    }

    public String getLogoEnterprise() {
        return logoEnterprise;
    }

    public void setLogoEnterprise(String logoEnterprise) {
        this.logoEnterprise = logoEnterprise;
    }

    public String getLogoWidth() {
        return logoWidth;
    }

    public void setLogoWidth(String logoWidth) {
        this.logoWidth = logoWidth;
    }

    public String getCommercialName() {
        return commercialName;
    }

    public void setCommercialName(String commercialName) {
        this.commercialName = commercialName;
    }

    public List<Sucursal> getSucursales() {
        return sucursales;
    }

    public void setSucursales(List<Sucursal> sucursales) {
        this.sucursales = sucursales;
    }
}
