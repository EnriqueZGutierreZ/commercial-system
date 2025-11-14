package com.elolympus.views.Empresa;

import com.elolympus.data.Empresa.Empresa;
import com.elolympus.data.Empresa.Sucursal;
import com.elolympus.services.services.AbstractCrudService;
import com.elolympus.services.services.EmpresaService;
import com.elolympus.services.services.SucursalService;
import com.elolympus.views.AbstractCrudView;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Sucursal")
@Route(value = "sucursal/:SucursalID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class SucursalView extends AbstractCrudView<Sucursal> {

    private final SucursalService sucursalService;
    private final EmpresaService empresaService;

    // Form fields
    private Checkbox principal;
    private IntegerField codigo;
    private TextField descripcion;
    private ComboBox<Empresa> empresaComboBox;
    private IntegerField serie;

    public SucursalView(SucursalService sucursalService, EmpresaService empresaService) {
        super();
        this.sucursalService = sucursalService;
        this.empresaService = empresaService;
        initialize();
    }

    @Override
    protected Class<Sucursal> getEntityClass() {
        return Sucursal.class;
    }

    @Override
    protected AbstractCrudService<Sucursal, ?> getService() {
        return sucursalService;
    }

    @Override
    protected String getViewClassName() {
        return "sucursal-view";
    }

    @Override
    protected Class<? extends Component> getViewClass() {
        return SucursalView.class;
    }

    @Override
    protected String getEntityIdParam() {
        return "SucursalID";
    }

    @Override
    protected String getEditRouteTemplate() {
        return "sucursal/%s/edit";
    }

    @Override
    protected String getEntityName() {
        return "Sucursal";
    }

    @Override
    protected Sucursal createNewEntity() {
        return new Sucursal();
    }

    @Override
    protected void configureBinder() {
        // El binder ya está inicializado en la clase base
    }

    @Override
    protected void configureGrid(Grid<Sucursal> grid) {
        // Agregar columna de estado activo
        grid.addColumn(createActivoRenderer())
                .setHeader("Activo")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(sucursal -> sucursal.isPrincipal() ? "Sí" : "No")
                .setHeader("Principal")
                .setAutoWidth(true)
                .setFlexGrow(0);
        grid.addColumn(Sucursal::getCodigo)
                .setHeader("Código")
                .setAutoWidth(true)
                .setFlexGrow(0);
        grid.addColumn(Sucursal::getDescripcion)
                .setHeader("Descripción")
                .setAutoWidth(true)
                .setFlexGrow(1);
        grid.addColumn(sucursal -> sucursal.getEmpresa() != null ? sucursal.getEmpresa().getCommercialName() : "")
                .setHeader("Empresa")
                .setAutoWidth(true)
                .setFlexGrow(1);
        grid.addColumn(Sucursal::getSerie)
                .setHeader("Serie")
                .setAutoWidth(true)
                .setFlexGrow(0);
    }

    @Override
    protected void configureFormLayout(FormLayout formLayout) {
        principal = new Checkbox("Principal");
        codigo = new IntegerField("Código");
        descripcion = new TextField("Descripción");
        empresaComboBox = new ComboBox<>("Empresa");
        serie = new IntegerField("Serie");

        // La configuración del ComboBox se hará en initialize()
        empresaComboBox.setItemLabelGenerator(Empresa::getCommercialName);

        binder.forField(principal).bind(Sucursal::isPrincipal, Sucursal::setPrincipal);
        binder.forField(codigo).bind(Sucursal::getCodigo, Sucursal::setCodigo);
        binder.forField(descripcion).bind(Sucursal::getDescripcion, Sucursal::setDescripcion);
        binder.forField(empresaComboBox).bind(Sucursal::getEmpresa, Sucursal::setEmpresa);
        binder.forField(serie).bind(Sucursal::getSerie, Sucursal::setSerie);

        formLayout.add(principal, codigo, descripcion, empresaComboBox, serie);
    }

    @Override
    protected void initialize() {
        // Primero llamar al método padre para crear todos los campos
        super.initialize();
        
        // Después configurar los items del ComboBox cuando ya está creado
        if (empresaComboBox != null) {
            empresaComboBox.setItems(empresaService.findAll());
        }
    }

    @Override
    protected boolean hasFilters() {
        return false;
    }

    @Override
    protected void beforeSave(Sucursal entity) {
        // Asegurar que la empresa esté establecida
        if (empresaComboBox.getValue() != null) {
            entity.setEmpresa(empresaComboBox.getValue());
        }
    }

    @Override
    protected void clearFilters() {
        // No hay filtros que limpiar
        if (empresaComboBox != null) {
            empresaComboBox.clear();
        }
    }
}