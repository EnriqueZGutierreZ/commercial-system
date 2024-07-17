package com.elolympus.views.Administracion;

import com.elolympus.data.Administracion.Rol;
import com.elolympus.services.services.RolService;
import com.elolympus.views.MainLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;

import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.List;
import java.util.Optional;


@PageTitle("Roles")
@Route(value = "rol/:RolID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class RolesView extends Div implements BeforeEnterObserver {
    private final RolService rolService;
    private BeanValidationBinder<Rol> binder;
    private Rol rol;

    public final String ROL_ID = "RolID";
    private final TextField area = new TextField("Área");
    private final TextField cargo = new TextField("Cargo");
    private final TextField descripcion = new TextField("Descripción");
    private final Checkbox canCreate = new Checkbox("Puede Crear");
    private final Checkbox canRead = new Checkbox("Puede Leer");
    private final Checkbox canUpdate = new Checkbox("Puede Actualizar");
    private final Checkbox canDelete = new Checkbox("Puede Eliminar");

    private final Button save = new Button("Guardar");
    private final Button cancel = new Button("Cancelar");
    private final Button delete = new Button("Eliminar", VaadinIcon.TRASH.create());

    private final FormLayout formLayout = new FormLayout();

    private final TextField areaField= new TextField("Área","Buscar por Área");
    private final TextField cargoField = new TextField("Cargo","Buscar por Cargo");
    private final TextField descripcionField = new TextField("Descripción","Buscar por Descripción");
    private Grid<Rol> grid = new Grid<>(Rol.class);
    @Autowired
    public RolesView(RolService rolService) {
        this.rolService = rolService;
        try {
            // Configure Form
            binder = new BeanValidationBinder<>(Rol.class);
            // Bind fields. This is where you'd define e.g. validation rules
            binder.bindInstanceFields(this);
        }catch (Exception e){
            System.out.println("ERRORRRR: " + e.getMessage());
        }
        addClassNames("roles-view");
        setSizeFull();
        configureGrid();
        SplitLayout splitLayout = new SplitLayout(createGridLayout(), createEditorLayout());
        splitLayout.setSizeFull();
        add(splitLayout);
        updateList();
        areaField.addValueChangeListener(e -> busqueda(areaField.getValue(), cargoField.getValue(), descripcionField.getValue()));
        cargoField.addValueChangeListener(e -> busqueda(areaField.getValue(), cargoField.getValue(), descripcionField.getValue()));
        descripcionField.addValueChangeListener(e -> busqueda(areaField.getValue(), cargoField.getValue(), descripcionField.getValue()));
    }
    private void configureGrid() {

        grid = new Grid<>();
        grid.setClassName("grilla");
        grid.setHeight("86%");
        grid.addColumn(Rol::getArea).setHeader("Área");
        grid.addColumn(Rol::getCargo).setHeader("Cargo");
        grid.addColumn(Rol::getDescripcion).setHeader("Descripción");
        grid.addColumn(role -> role.getCanCreate() ? "Sí" : "No").setHeader("Puede Crear");
        grid.addColumn(role -> role.getCanRead() ? "Sí" : "No").setHeader("Puede Leer");
        grid.addColumn(role -> role.getCanUpdate() ? "Sí" : "No").setHeader("Puede Actualizar");
        grid.addColumn(role -> role.getCanDelete() ? "Sí" : "No").setHeader("Puede Eliminar");
        grid.asSingleSelect().addValueChangeListener(event -> editRole(event.getValue()));
    }
    private Component createEditorLayout() {
        Div editorDiv = new Div();
        editorDiv.setHeightFull();
        editorDiv.setClassName("editor-layout");
        Div div = new Div();
        div.setClassName("editor");
        editorDiv.add(div);
        formLayout.add(area, cargo, descripcion, canCreate, canRead, canUpdate, canDelete);

        save.addClickListener(event -> save());
        cancel.addClickListener(event -> clearForm());
        delete.addClickListener(event -> delete());

        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        div.add(formLayout);
        createButtonLayout(editorDiv);
        return editorDiv;
    }
    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        buttonLayout.add(save, cancel,delete);
        editorLayoutDiv.add(buttonLayout);
    }

    private Component createGridLayout() {
        HorizontalLayout busquedaDiv = new HorizontalLayout();
        busquedaDiv.addClassName("tophl");
        busquedaDiv.add(areaField, cargoField, descripcionField);
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        wrapper.setSizeFull();
        wrapper.setHeightFull();
        wrapper.add(busquedaDiv,grid);
        return wrapper;
    }

    private void updateList() {
        List<Rol> roles = rolService.findAll(); // Asumiendo que existe un método findAll en RolService
        grid.setItems(roles);
    }
    private void editRole(Rol rol) {
        if (rol == null) {
            clearForm();
        } else {
            this.rol = rol;
            binder.readBean(this.rol);
            save.setText("Actualizar"); // Cambiar el texto del botón a "Actualizar"
        }
    }
    private void save() {
        try {
            if (this.rol == null) {
                this.rol = new Rol();
            }
            binder.writeBean(this.rol);
            rolService.update(this.rol);
            clearForm();
            refreshGrid();
            Notification.show("Datos actualizados");
            UI.getCurrent().navigate(RolesView.class);
        } catch (ObjectOptimisticLockingFailureException exception) {
            Notification n = Notification.show(
                    "Error al actualizar los datos. Alguien más actualizó el registro mientras usted hacía cambios.");
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (ValidationException validationException) {
            Notification.show("No se pudieron actualizar los datos. Compruebe nuevamente que todos los valores sean válidos.");
        }
    }

    private void delete() {
        if (this.rol != null) {
            rolService.delete(this.rol); // Asumiendo que existe un método delete en RolService
            updateList();
            clearForm();
            Notification.show("Rol eliminado");
        }
    }

    private void clearForm() {
        this.rol = null;
        binder.readBean(new Rol());
        save.setText("Guardar"); // Cambiar el texto del botón de vuelta a "Guardar" cuando se limpia el formulario
    }
    private void populateForm(Rol rol) {
        // Establece los valores de los campos del formulario con las propiedades del rol
        this.rol = rol; // Asegúrate de que la variable 'rol' esté declarada en tu clase
        binder.setBean(rol);

        if (rol != null) {
            area.setValue(rol.getArea() != null ? rol.getArea() : "");
            cargo.setValue(rol.getCargo() != null ? rol.getCargo() : "");
            descripcion.setValue(rol.getDescripcion() != null ? rol.getDescripcion() : "");
            canCreate.setValue(rol.getCanCreate() != null && rol.getCanCreate());
            canRead.setValue(rol.getCanRead() != null && rol.getCanRead());
            canUpdate.setValue(rol.getCanUpdate() != null && rol.getCanUpdate());
            canDelete.setValue(rol.getCanDelete() != null && rol.getCanDelete());
        } else {
            clearForm();
        }
    }
    private void refreshGrid() {
        List<Rol> roles = rolService.findAll(); // Utiliza el método 'findAll' de tu RolService para obtener todos los roles
        grid.setItems(roles); // Actualiza los ítems del grid con la lista de roles
    }

    private void busqueda(String area,String cargo,String descripcion){
         area = areaField.getValue();
         cargo = cargoField.getValue();
         descripcion = descripcionField.getValue();

        List<Rol> roles = rolService.findRolesByAreaContainingAndCargoContainingAndDescriptionContaining(area, cargo, descripcion);

        grid.setItems(roles);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> rolId = event.getRouteParameters().get(this.ROL_ID).map(Long::parseLong);
        if (rolId.isPresent()) {
            Optional<Rol> rolFromBackend = rolService.findById(rolId.get());
            if (rolFromBackend.isPresent()) {
                populateForm(rolFromBackend.get());
            } else {
                Notification.show(String.format("El rol solicitado no fue encontrado, ID = %s", rolId.get()), 3000, Notification.Position.BOTTOM_START);
                // Cuando se selecciona una fila pero los datos ya no están disponibles,
                // refrescar la lista y redirigir, si es necesario.
                refreshGrid();
                event.forwardTo(RolesView.class);
            }
        } else {
            // No hay un ID de Rol en la URL, asegúrate de que la vista esté en un estado limpio
            clearForm();
        }
    }
}
    


