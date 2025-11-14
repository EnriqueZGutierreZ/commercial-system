package com.elolympus.views.Logistica;

import com.elolympus.data.Logistica.Marca;
import com.elolympus.services.services.AbstractCrudService;
import com.elolympus.services.services.MarcaService;
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

@PageTitle("Marcas")
@Route(value = "marcas/:MarcaID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class MarcasView extends AbstractCrudView<Marca> {

    private final MarcaService marcaService;

    // Form fields
    private TextField nombre;
    private TextField descripcion;
    private TextField codigo;
    private TextField creador;
    
    // Filter fields
    private TextField txtCodigo;
    private TextField txtNombre;

    @Autowired
    public MarcasView(MarcaService marcaService) {
        super();
        this.marcaService = marcaService;
        initialize(); // Llamar después de que el servicio esté inyectado
    }

    @Override
    protected Class<Marca> getEntityClass() {
        return Marca.class;
    }

    @Override
    protected AbstractCrudService<Marca, ?> getService() {
        return marcaService;
    }

    @Override
    protected String getViewClassName() {
        return "marcas-view";
    }

    @Override
    protected Class<? extends Component> getViewClass() {
        return MarcasView.class;
    }

    @Override
    protected String getEntityIdParam() {
        return "MarcaID";
    }

    @Override
    protected String getEditRouteTemplate() {
        return "marcas/%s/edit";
    }

    @Override
    protected String getEntityName() {
        return "Marca";
    }

    @Override
    protected Marca createNewEntity() {
        return new Marca();
    }

    @Override
    protected void configureBinder() {
        // El binder ya está inicializado en la clase base
        // Aquí solo agregamos los bindings específicos
    }

    @Override
    protected void configureGrid(Grid<Marca> grid) {
        // Agregar columna de estado activo
        grid.addColumn(createActivoRenderer())
                .setHeader("Activo")
                .setAutoWidth(true)
                .setFlexGrow(0);
                
        grid.addColumn(Marca::getCodigo)
                .setHeader("Código")
                .setAutoWidth(true)
                .setFlexGrow(0);
        grid.addColumn(Marca::getNombre)
                .setHeader("Nombre")
                .setAutoWidth(true)
                .setFlexGrow(1);
        grid.addColumn(Marca::getDescripcion)
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

        binder.forField(nombre).bind(Marca::getNombre, Marca::setNombre);
        binder.forField(descripcion).bind(Marca::getDescripcion, Marca::setDescripcion);
        binder.forField(codigo).bind(Marca::getCodigo, Marca::setCodigo);
        binder.forField(creador).bind(Marca::getCreador, null);

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
    protected List<Marca> applyFilters(List<Marca> items) {
        List<Marca> filteredItems = new ArrayList<>(items);
        
        String codigo = txtCodigo != null ? txtCodigo.getValue() : "";
        String nombre = txtNombre != null ? txtNombre.getValue() : "";

        if (!codigo.isEmpty()) {
            filteredItems.removeIf(m -> !m.getCodigo().toLowerCase().contains(codigo.toLowerCase()));
        }
        if (!nombre.isEmpty()) {
            filteredItems.removeIf(m -> !m.getNombre().toLowerCase().contains(nombre.toLowerCase()));
        }

        return filteredItems;
    }
}