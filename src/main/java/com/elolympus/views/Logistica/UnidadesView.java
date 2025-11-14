package com.elolympus.views.Logistica;

import com.elolympus.data.Logistica.Unidad;
import com.elolympus.services.services.AbstractCrudService;
import com.elolympus.services.services.UnidadService;
import com.elolympus.views.AbstractCrudView;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Unidades")
@Route(value = "unidades/:UnidadID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class UnidadesView extends AbstractCrudView<Unidad> {

    private final UnidadService unidadService;

    // Form fields
    private TextField nombre;
    private TextField abreviatura;
    private TextField descripcion;
    private TextField creador;
    
    // Filter fields
    private TextField txtNombre;
    private TextField txtAbreviatura;

    @Autowired
    public UnidadesView(UnidadService unidadService) {
        super();
        this.unidadService = unidadService;
        initialize();
    }

    @Override
    protected Class<Unidad> getEntityClass() {
        return Unidad.class;
    }

    @Override
    protected AbstractCrudService<Unidad, ?> getService() {
        return unidadService;
    }

    @Override
    protected String getViewClassName() {
        return "unidades-view";
    }

    @Override
    protected Class<? extends Component> getViewClass() {
        return UnidadesView.class;
    }

    @Override
    protected String getEntityIdParam() {
        return "UnidadID";
    }

    @Override
    protected String getEditRouteTemplate() {
        return "unidades/%s/edit";
    }

    @Override
    protected String getEntityName() {
        return "Unidad";
    }

    @Override
    protected Unidad createNewEntity() {
        return new Unidad();
    }

    @Override
    protected void configureBinder() {
        // El binder ya está inicializado en la clase base
    }

    @Override
    protected void configureGrid(Grid<Unidad> grid) {
        // Agregar columna de estado activo
        grid.addColumn(createActivoRenderer())
                .setHeader("Activo")
                .setAutoWidth(true)
                .setFlexGrow(0);
                
        grid.addColumn(Unidad::getNombre)
                .setHeader("Nombre")
                .setAutoWidth(true)
                .setFlexGrow(1);
        grid.addColumn(Unidad::getAbreviatura)
                .setHeader("Abreviatura")
                .setAutoWidth(true)
                .setFlexGrow(0);
        grid.addColumn(Unidad::getDescripcion)
                .setHeader("Descripción")
                .setAutoWidth(true)
                .setFlexGrow(1);
    }

    @Override
    protected void configureFormLayout(FormLayout formLayout) {
        nombre = new TextField("Nombre", "");
        abreviatura = new TextField("Abreviatura", "");
        descripcion = new TextField("Descripción", "");
        creador = new TextField("Creador", "");
        creador.setReadOnly(true);

        binder.forField(nombre).bind(Unidad::getNombre, Unidad::setNombre);
        binder.forField(abreviatura).bind(Unidad::getAbreviatura, Unidad::setAbreviatura);
        binder.forField(descripcion).bind(Unidad::getDescripcion, Unidad::setDescripcion);
        binder.forField(creador).bind(Unidad::getCreador, null);

        formLayout.add(nombre, abreviatura, descripcion, creador);
    }

    @Override
    protected boolean hasFilters() {
        return true;
    }

    @Override
    protected HorizontalLayout createFilterLayout() {
        txtNombre = new TextField("Nombre", "", "Buscar por nombre");
        txtAbreviatura = new TextField("Abreviatura", "", "Buscar por abreviatura");
        Button btnFiltrar = new Button("BUSCAR", new Icon(VaadinIcon.FILTER));
        
        btnFiltrar.addClickListener(e -> onRefresh());
        
        HorizontalLayout layout = new HorizontalLayout(txtNombre, txtAbreviatura, btnFiltrar);
        return layout;
    }

    @Override
    protected void setupFilterListeners() {
        txtNombre.addValueChangeListener(e -> onRefresh());
        txtAbreviatura.addValueChangeListener(e -> onRefresh());
    }

    @Override
    protected void clearFilters() {
        if (txtNombre != null) {
            txtNombre.setValue("");
        }
        if (txtAbreviatura != null) {
            txtAbreviatura.setValue("");
        }
    }

    @Override
    protected List<Unidad> applyFilters(List<Unidad> items) {
        List<Unidad> filteredItems = new ArrayList<>(items);
        
        String nombre = txtNombre != null ? txtNombre.getValue() : "";
        String abreviatura = txtAbreviatura != null ? txtAbreviatura.getValue() : "";

        if (!nombre.isEmpty()) {
            filteredItems.removeIf(u -> !u.getNombre().toLowerCase().contains(nombre.toLowerCase()));
        }
        if (!abreviatura.isEmpty()) {
            filteredItems.removeIf(u -> !u.getAbreviatura().toLowerCase().contains(abreviatura.toLowerCase()));
        }

        return filteredItems;
    }
}