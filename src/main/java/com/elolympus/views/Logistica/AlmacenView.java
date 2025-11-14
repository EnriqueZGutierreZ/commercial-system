package com.elolympus.views.Logistica;

import com.elolympus.data.Almacen.Almacen;
import com.elolympus.data.Empresa.Sucursal;
import com.elolympus.services.services.AbstractCrudService;
import com.elolympus.services.services.AlmacenService;
import com.elolympus.services.services.SucursalService;
import com.elolympus.views.AbstractCrudView;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PageTitle("Almacen")
@Route(value = "almacen/:AlmacenID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class AlmacenView extends AbstractCrudView<Almacen> {

    private final AlmacenService almacenService;
    private final SucursalService sucursalService;

    // Form fields
    private IntegerField codigo;
    private TextField descripcion;
    private ComboBox<Sucursal> sucursalComboBox;
    private TextField creador;

    @Autowired
    public AlmacenView(AlmacenService almacenService, SucursalService sucursalService) {
        super();
        this.almacenService = almacenService;
        this.sucursalService = sucursalService;
        initialize();
    }

    @Override
    protected Class<Almacen> getEntityClass() {
        return Almacen.class;
    }

    @Override
    protected AbstractCrudService<Almacen, ?> getService() {
        return almacenService;
    }

    @Override
    protected String getViewClassName() {
        return "almacen-view";
    }

    @Override
    protected Class<? extends Component> getViewClass() {
        return AlmacenView.class;
    }

    @Override
    protected String getEntityIdParam() {
        return "AlmacenID";
    }

    @Override
    protected String getEditRouteTemplate() {
        return "almacen/%s/edit";
    }

    @Override
    protected String getEntityName() {
        return "Almacén";
    }

    @Override
    protected Almacen createNewEntity() {
        return new Almacen();
    }

    @Override
    protected void configureBinder() {
        // El binder ya está inicializado en la clase base
    }

    @Override
    protected void configureGrid(Grid<Almacen> grid) {
        // Agregar columna de estado activo
        grid.addColumn(createActivoRenderer())
                .setHeader("Activo")
                .setAutoWidth(true)
                .setFlexGrow(0);
                
        grid.addColumn(Almacen::getCodigo)
                .setHeader("Código")
                .setAutoWidth(true)
                .setFlexGrow(0);
        grid.addColumn(Almacen::getDescripcion)
                .setHeader("Descripción")
                .setAutoWidth(true)
                .setFlexGrow(1);
        grid.addColumn(almacen -> almacen.getSucursal() != null ? almacen.getSucursal().getDescripcion() : "")
                .setHeader("Sucursal")
                .setAutoWidth(true)
                .setFlexGrow(1);
    }

    @Override
    protected void configureFormLayout(FormLayout formLayout) {
        codigo = new IntegerField("Código");
        descripcion = new TextField("Descripción");
        sucursalComboBox = new ComboBox<>("Sucursal");
        creador = new TextField("Creador");
        creador.setReadOnly(true);

        // La configuración del ComboBox se hará en initialize()
        sucursalComboBox.setItemLabelGenerator(Sucursal::getDescripcion);

        binder.forField(codigo).bind(Almacen::getCodigo, Almacen::setCodigo);
        binder.forField(descripcion).bind(Almacen::getDescripcion, Almacen::setDescripcion);
        binder.forField(sucursalComboBox).bind(Almacen::getSucursal, Almacen::setSucursal);
        binder.forField(creador).bind(Almacen::getCreador, null);

        formLayout.add(codigo, descripcion, sucursalComboBox, creador);
    }
    
    @Override
    protected void initialize() {
        // Primero llamar al método padre para crear todos los campos
        super.initialize();
        
        // Después configurar los items del ComboBox cuando ya está creado
        if (sucursalComboBox != null) {
            sucursalComboBox.setItems(sucursalService.findAll());
        }
    }

    @Override
    protected boolean hasFilters() {
        return false; // No tiene filtros por ahora
    }

    @Override
    protected void beforeSave(Almacen entity) {
        // Asegurar que la sucursal esté establecida
        if (sucursalComboBox.getValue() != null) {
            entity.setSucursal(sucursalComboBox.getValue());
        }
    }

    @Override
    protected void clearFilters() {
        // No hay filtros que limpiar
        if (sucursalComboBox != null) {
            sucursalComboBox.clear();
        }
    }
}