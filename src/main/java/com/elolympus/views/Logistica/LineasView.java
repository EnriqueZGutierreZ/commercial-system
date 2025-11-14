package com.elolympus.views.Logistica;

import com.elolympus.data.Logistica.Linea;
import com.elolympus.services.services.AbstractCrudService;
import com.elolympus.services.services.LineaService;
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

@PageTitle("Líneas")
@Route(value = "lineas/:LineaID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class LineasView extends AbstractCrudView<Linea> {

    private final LineaService lineaService;

    // Form fields
    private TextField nombre;
    private TextField descripcion;
    private TextField codigo;
    private TextField creador;
    
    // Filter fields
    private TextField txtCodigo;
    private TextField txtNombre;

    @Autowired
    public LineasView(LineaService lineaService) {
        super();
        this.lineaService = lineaService;
        initialize();
    }

    @Override
    protected Class<Linea> getEntityClass() {
        return Linea.class;
    }

    @Override
    protected AbstractCrudService<Linea, ?> getService() {
        return lineaService;
    }

    @Override
    protected String getViewClassName() {
        return "lineas-view";
    }

    @Override
    protected Class<? extends Component> getViewClass() {
        return LineasView.class;
    }

    @Override
    protected String getEntityIdParam() {
        return "LineaID";
    }

    @Override
    protected String getEditRouteTemplate() {
        return "lineas/%s/edit";
    }

    @Override
    protected String getEntityName() {
        return "Línea";
    }

    @Override
    protected Linea createNewEntity() {
        return new Linea();
    }

    @Override
    protected void configureBinder() {
        // El binder ya está inicializado en la clase base
    }

    @Override
    protected void configureGrid(Grid<Linea> grid) {
        // Agregar columna de estado activo
        grid.addColumn(createActivoRenderer())
                .setHeader("Activo")
                .setAutoWidth(true)
                .setFlexGrow(0);
                
        grid.addColumn(Linea::getCodigo)
                .setHeader("Código")
                .setAutoWidth(true)
                .setFlexGrow(0);
        grid.addColumn(Linea::getNombre)
                .setHeader("Nombre")
                .setAutoWidth(true)
                .setFlexGrow(1);
        grid.addColumn(Linea::getDescripcion)
                .setHeader("Descripción")
                .setAutoWidth(true)
                .setFlexGrow(1);
    }

    @Override
    protected void configureFormLayout(FormLayout formLayout) {
        nombre = new TextField("Nombre", "");
        descripcion = new TextField("Descripción", "");
        codigo = new TextField("Código", "");
        creador = new TextField("Creador", "");
        creador.setReadOnly(true);

        binder.forField(nombre).bind(Linea::getNombre, Linea::setNombre);
        binder.forField(descripcion).bind(Linea::getDescripcion, Linea::setDescripcion);
        binder.forField(codigo).bind(Linea::getCodigo, Linea::setCodigo);
        binder.forField(creador).bind(Linea::getCreador, null);

        formLayout.add(nombre, descripcion, codigo, creador);
    }

    @Override
    protected boolean hasFilters() {
        return true;
    }

    @Override
    protected HorizontalLayout createFilterLayout() {
        txtCodigo = new TextField("Código", "", "Buscar por código");
        txtNombre = new TextField("Nombre", "", "Buscar por nombre");
        Button btnFiltrar = new Button("BUSCAR", new Icon(VaadinIcon.FILTER));
        
        btnFiltrar.addClickListener(e -> onRefresh());
        
        HorizontalLayout layout = new HorizontalLayout(txtCodigo, txtNombre, btnFiltrar);
        return layout;
    }

    @Override
    protected void setupFilterListeners() {
        txtCodigo.addValueChangeListener(e -> onRefresh());
        txtNombre.addValueChangeListener(e -> onRefresh());
    }

    @Override
    protected void clearFilters() {
        if (txtCodigo != null) {
            txtCodigo.setValue("");
        }
        if (txtNombre != null) {
            txtNombre.setValue("");
        }
    }

    @Override
    protected List<Linea> applyFilters(List<Linea> items) {
        List<Linea> filteredItems = new ArrayList<>(items);
        
        String codigo = txtCodigo != null ? txtCodigo.getValue() : "";
        String nombre = txtNombre != null ? txtNombre.getValue() : "";

        if (!codigo.isEmpty()) {
            filteredItems.removeIf(l -> !l.getCodigo().toLowerCase().contains(codigo.toLowerCase()));
        }
        if (!nombre.isEmpty()) {
            filteredItems.removeIf(l -> !l.getNombre().toLowerCase().contains(nombre.toLowerCase()));
        }

        return filteredItems;
    }
}