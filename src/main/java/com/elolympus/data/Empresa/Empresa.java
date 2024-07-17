package com.elolympus.data.Empresa;

import com.elolympus.data.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "empresa", schema = "empresa")
public class Empresa extends AbstractEntity{

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

    //Constructor
    public Empresa() {
    }
    //Constructor con parametros
    public Empresa(Integer direccion, String folderTemps, String folderReports, Boolean allowBuyWithoutStock, Boolean requireSalesPin, Integer documentoTipoXdefecto, String logoEnterprise, String logoWidth, String commercialName) {
        this.direccion = direccion;
        this.folderTemps = folderTemps;
        this.folderReports = folderReports;
        this.allowBuyWithoutStock = allowBuyWithoutStock;
        this.requireSalesPin = requireSalesPin;
        this.documentoTipoXdefecto = documentoTipoXdefecto;
        this.logoEnterprise = logoEnterprise;
        this.logoWidth = logoWidth;
        this.commercialName = commercialName;
    }

    // Getters and Setters
    public Integer getDireccion() {
        return direccion;
    }

    public void setDireccion(Integer direccion) {
        this.direccion = direccion;
    }

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

    // toString
    @Override
    public String toString() {
        return "Empresa{" +
                "direccion=" + direccion +
                ", folderTemps='" + folderTemps + '\'' +
                ", folderReports='" + folderReports + '\'' +
                ", allowBuyWithoutStock=" + allowBuyWithoutStock +
                ", requireSalesPin=" + requireSalesPin +
                ", documentoTipoXdefecto=" + documentoTipoXdefecto +
                ", logoEnterprise='" + logoEnterprise + '\'' +
                ", logoWidth='" + logoWidth + '\'' +
                ", commercialName='" + commercialName + '\'' +
                '}';
    }

}
