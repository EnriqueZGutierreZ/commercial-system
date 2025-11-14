package com.elolympus.views.Administracion;

import com.elolympus.data.Administracion.Rol;
import com.elolympus.services.services.AbstractCrudService;
import com.elolympus.services.services.RolService;
import com.elolympus.views.AbstractCrudView;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
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

import java.util.List;

@PageTitle("Roles")
@Route(value = "rol/:RolID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class RolesView extends AbstractCrudView<Rol> {

    private final RolService rolService;

    // Form fields
    private TextField area;
    private TextField cargo;
    private TextField descripcion;
    private Checkbox canCreate;
    private Checkbox canRead;
    private Checkbox canUpdate;
    private Checkbox canDelete;

    // Filter fields
    private TextField areaField;
    private TextField cargoField;
    private TextField descripcionField;
    private Button btnBuscar;

    @Autowired
    public RolesView(RolService rolService) {
        super();
        this.rolService = rolService;
        initialize();
    }

    @Override
    protected Class<Rol> getEntityClass() {
        return Rol.class;
    }

    @Override
    protected AbstractCrudService<Rol, ?> getService() {
        return rolService;
    }

    @Override
    protected String getViewClassName() {
        return "roles-view";
    }

    @Override
    protected Class<? extends Component> getViewClass() {
        return RolesView.class;
    }

    @Override
    protected String getEntityIdParam() {
        return "RolID";
    }

    @Override
    protected String getEditRouteTemplate() {
        return "rol/%s/edit";
    }

    @Override
    protected String getEntityName() {
        return "Rol";
    }

    @Override
    protected Rol createNewEntity() {
        return new Rol();
    }

    @Override
    protected void configureBinder() {
        // Configurar binding explícito para cada campo
        binder.forField(area)
                .bind(Rol::getArea, Rol::setArea);
        
        binder.forField(cargo)
                .bind(Rol::getCargo, Rol::setCargo);
        
        binder.forField(descripcion)
                .bind(Rol::getDescripcion, Rol::setDescripcion);
        
        binder.forField(canCreate)
                .bind(Rol::getCanCreate, Rol::setCanCreate);
        
        binder.forField(canRead)
                .bind(Rol::getCanRead, Rol::setCanRead);
        
        binder.forField(canUpdate)
                .bind(Rol::getCanUpdate, Rol::setCanUpdate);
        
        binder.forField(canDelete)
                .bind(Rol::getCanDelete, Rol::setCanDelete);
    }

    @Override
    protected void configureGrid(Grid<Rol> grid) {
        // Agregar columna de estado activo
        grid.addColumn(createActivoRenderer())
                .setHeader("Activo")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(Rol::getArea)
                .setHeader("Área")
                .setAutoWidth(true)
                .setFlexGrow(1);
        grid.addColumn(Rol::getCargo)
                .setHeader("Cargo")
                .setAutoWidth(true)
                .setFlexGrow(1);
        grid.addColumn(Rol::getDescripcion)
                .setHeader("Descripción")
                .setAutoWidth(true)
                .setFlexGrow(1);
        grid.addColumn(role -> role.getCanCreate() ? "Sí" : "No")
                .setHeader("Puede Crear")
                .setAutoWidth(true)
                .setFlexGrow(0);
        grid.addColumn(role -> role.getCanRead() ? "Sí" : "No")
                .setHeader("Puede Leer")
                .setAutoWidth(true)
                .setFlexGrow(0);
        grid.addColumn(role -> role.getCanUpdate() ? "Sí" : "No")
                .setHeader("Puede Actualizar")
                .setAutoWidth(true)
                .setFlexGrow(0);
        grid.addColumn(role -> role.getCanDelete() ? "Sí" : "No")
                .setHeader("Puede Eliminar")
                .setAutoWidth(true)
                .setFlexGrow(0);
    }

    @Override
    protected void configureFormLayout(FormLayout formLayout) {
        area = new TextField("Área");
        cargo = new TextField("Cargo");
        descripcion = new TextField("Descripción");
        canCreate = new Checkbox("Puede Crear");
        canRead = new Checkbox("Puede Leer");
        canUpdate = new Checkbox("Puede Actualizar");
        canDelete = new Checkbox("Puede Eliminar");

        // Binding se configura en configureBinder() para mantener consistencia

        formLayout.add(area, cargo, descripcion, canCreate, canRead, canUpdate, canDelete);
    }

    @Override
    protected boolean hasFilters() {
        return true;
    }

    @Override
    protected HorizontalLayout createFilterLayout() {
        areaField = new TextField("Área", "Buscar por Área");
        cargoField = new TextField("Cargo", "Buscar por Cargo");
        descripcionField = new TextField("Descripción", "Buscar por Descripción");
        btnBuscar = new Button("BUSCAR", new Icon(VaadinIcon.SEARCH));

        // Add listeners para filtrado automático
        areaField.addValueChangeListener(e -> applyCustomFilters());
        cargoField.addValueChangeListener(e -> applyCustomFilters());
        descripcionField.addValueChangeListener(e -> applyCustomFilters());
        btnBuscar.addClickListener(e -> applyCustomFilters());

        HorizontalLayout filterLayout = new HorizontalLayout(areaField, cargoField, descripcionField, btnBuscar);
        filterLayout.setClassName("tophl");
        return filterLayout;
    }

    private void applyCustomFilters() {
        String area = areaField.getValue() != null ? areaField.getValue() : "";
        String cargo = cargoField.getValue() != null ? cargoField.getValue() : "";
        String descripcion = descripcionField.getValue() != null ? descripcionField.getValue() : "";

        List<Rol> roles = rolService.findRolesByAreaContainingAndCargoContainingAndDescriptionContaining(area, cargo, descripcion);
        grid.setItems(roles);
    }

    @Override
    protected void clearFilters() {
        if (areaField != null) areaField.clear();
        if (cargoField != null) cargoField.clear();
        if (descripcionField != null) descripcionField.clear();
    }
}
