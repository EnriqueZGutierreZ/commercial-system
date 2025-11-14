package com.elolympus.views.Empresa;

import com.elolympus.data.Empresa.Empresa;
import com.elolympus.services.services.AbstractCrudService;
import com.elolympus.services.services.EmpresaService;
import com.elolympus.views.AbstractCrudView;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Empresa")
@Route(value = "empresa/:EmpresaID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class EmpresaView extends AbstractCrudView<Empresa> {

    private final EmpresaService empresaService;

    // Form fields
    private IntegerField direccion;
    private TextField folderTemps;
    private TextField folderReports;
    private Checkbox allowBuyWithoutStock;
    private Checkbox requireSalesPin;
    private IntegerField documentoTipoXdefecto;
    private TextField logoEnterprise;
    private TextField logoWidth;
    private TextField commercialName;

    public EmpresaView(EmpresaService empresaService) {
        super();
        this.empresaService = empresaService;
        initialize();
    }

    @Override
    protected Class<Empresa> getEntityClass() {
        return Empresa.class;
    }

    @Override
    protected AbstractCrudService<Empresa, ?> getService() {
        return empresaService;
    }

    @Override
    protected String getViewClassName() {
        return "empresa-view";
    }

    @Override
    protected Class<? extends Component> getViewClass() {
        return EmpresaView.class;
    }

    @Override
    protected String getEntityIdParam() {
        return "EmpresaID";
    }

    @Override
    protected String getEditRouteTemplate() {
        return "empresa/%s/edit";
    }

    @Override
    protected String getEntityName() {
        return "Empresa";
    }

    @Override
    protected Empresa createNewEntity() {
        return new Empresa();
    }

    @Override
    protected void configureBinder() {
        // El binder ya está inicializado en la clase base
    }

    @Override
    protected void configureGrid(Grid<Empresa> grid) {
        // Agregar columna de estado activo
        grid.addColumn(createActivoRenderer())
                .setHeader("Activo")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(Empresa::getCommercialName)
                .setHeader("Nombre Comercial")
                .setAutoWidth(true)
                .setFlexGrow(1);
        grid.addColumn(Empresa::getFolderTemps)
                .setHeader("Carpeta Temporal")
                .setAutoWidth(true)
                .setFlexGrow(1);
        grid.addColumn(Empresa::getFolderReports)
                .setHeader("Carpeta Reportes")
                .setAutoWidth(true)
                .setFlexGrow(1);
        grid.addColumn(empresa -> empresa.getAllowBuyWithoutStock() ? "Sí" : "No")
                .setHeader("Permitir Compra Sin Stock")
                .setAutoWidth(true)
                .setFlexGrow(0);
        grid.addColumn(empresa -> empresa.getRequireSalesPin() ? "Sí" : "No")
                .setHeader("Requiere PIN Ventas")
                .setAutoWidth(true)
                .setFlexGrow(0);
    }

    @Override
    protected void configureFormLayout(FormLayout formLayout) {
        direccion = new IntegerField("Dirección");
        folderTemps = new TextField("Carpeta Temporal");
        folderReports = new TextField("Carpeta Reportes");
        allowBuyWithoutStock = new Checkbox("Permitir Compra Sin Stock");
        requireSalesPin = new Checkbox("Requiere PIN Ventas");
        documentoTipoXdefecto = new IntegerField("Tipo Documento por Defecto");
        logoEnterprise = new TextField("Logo Empresa");
        logoWidth = new TextField("Ancho del Logo");
        commercialName = new TextField("Nombre Comercial");

        binder.forField(direccion).bind(Empresa::getDireccion, Empresa::setDireccion);
        binder.forField(folderTemps).bind(Empresa::getFolderTemps, Empresa::setFolderTemps);
        binder.forField(folderReports).bind(Empresa::getFolderReports, Empresa::setFolderReports);
        binder.forField(allowBuyWithoutStock).bind(Empresa::getAllowBuyWithoutStock, Empresa::setAllowBuyWithoutStock);
        binder.forField(requireSalesPin).bind(Empresa::getRequireSalesPin, Empresa::setRequireSalesPin);
        binder.forField(documentoTipoXdefecto).bind(Empresa::getDocumentoTipoXdefecto, Empresa::setDocumentoTipoXdefecto);
        binder.forField(logoEnterprise).bind(Empresa::getLogoEnterprise, Empresa::setLogoEnterprise);
        binder.forField(logoWidth).bind(Empresa::getLogoWidth, Empresa::setLogoWidth);
        binder.forField(commercialName).bind(Empresa::getCommercialName, Empresa::setCommercialName);

        formLayout.add(direccion, folderTemps, folderReports, allowBuyWithoutStock, 
                      requireSalesPin, documentoTipoXdefecto, logoEnterprise, logoWidth, commercialName);
    }

    @Override
    protected boolean hasFilters() {
        return false;
    }

    @Override
    protected void clearFilters() {
        // No hay filtros que limpiar
    }
}
