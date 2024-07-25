package com.elolympus.views.Administracion;

import com.elolympus.data.Administracion.Persona;
import com.elolympus.data.Administracion.Rol;
import com.elolympus.data.Administracion.Usuario;
import com.elolympus.security.PasswordUtils;
import com.elolympus.services.services.PersonaService;
import com.elolympus.services.services.RolService;
import com.elolympus.services.services.UsuarioService;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.List;

@PageTitle("Usuarios")
@Route(value = "usuario/:UsuarioID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class UsuariosView extends Div {

    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final PersonaService personaService;
    private final PasswordUtils passwordUtils;
    private BeanValidationBinder<Usuario> binder;
    private Usuario usuario;

    // Componentes UI
    private Grid<Usuario> gridUsuarios = new Grid<>(Usuario.class, false);
    private final TextField usuarioField = new TextField("Usuario");
    private final PasswordField passwordField = new PasswordField("Contraseña");
    private final Checkbox activoCheckbox = new Checkbox("Activo");
    private final ComboBox<Rol> rolComboBox = new ComboBox<>("Rol");
    private final ComboBox<Persona> personaComboBox = new ComboBox<>("Persona");
    private final Button save = new Button("Guardar");
    private final Button cancel = new Button("Cancelar");
    private final Button delete = new Button("Eliminar", VaadinIcon.TRASH.create());

    // Componentes de búsqueda
    private final TextField usuarioBusqueda = new TextField("Usuario");
    private final ComboBox<Rol> rolBusqueda = new ComboBox<>("Rol");
    private final ComboBox<Persona> personaBusqueda = new ComboBox<>("Persona");

    private final FormLayout formLayout = new FormLayout();

    private String Password;

    @Autowired
    public UsuariosView(UsuarioService usuarioService, RolService rolService, PersonaService personaService, PasswordUtils passwordUtils) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
        this.personaService = personaService;
        this.passwordUtils = passwordUtils;
        try {
            // Configure Form
            binder = new BeanValidationBinder<>(Usuario.class);
            // Bind fields. This is where you'd define e.g. validation rules
            binder.bindInstanceFields(this);
        }catch (Exception e){
            System.out.println("ERRORRRR: " + e.getMessage());
        }
        addClassNames("usuario-view");
        setSizeFull();
        // Configura la búsqueda después de inicializar los componentes
        setupSearch();
        setupGrid();
        setupForm();
        usuarioBusqueda.addValueChangeListener(e -> applyFilter());
        rolBusqueda.setItemLabelGenerator(Rol::getCargo); // Asumiendo que Rol tiene un método getCargo
        rolBusqueda.setItems(rolService.findAll());
        rolBusqueda.addValueChangeListener(e -> applyFilter());
        personaBusqueda.setItemLabelGenerator(Persona::getNombreCompleto); // Asumiendo que Persona tiene un método getNombreCompleto
        personaBusqueda.setItems(personaService.findAll());
        personaBusqueda.addValueChangeListener(e -> applyFilter());

        SplitLayout layout = new SplitLayout(createGridLayout(), createEditorLayout());
        layout.setSizeFull();
        add(layout);
        refreshGrid();
    }

    private final SerializableBiConsumer<Span, Usuario> EstadoComponenteActivo = (
            span, usuario) -> {
        String theme = String.format("badge %s",
                usuario.isActivo() ? "success" : "error");
        span.getElement().setAttribute("theme", theme);
        span.setText(usuario.isActivo()?"Activo":"Desactivado");
    };
    private ComponentRenderer<Span, Usuario> CrearComponmenteActivoRenderer() {
        return new ComponentRenderer<>(Span::new, EstadoComponenteActivo);
    }

    private void setupGrid() {
        gridUsuarios = new Grid<>();
        gridUsuarios.setClassName("grilla");
        gridUsuarios.setHeight("86%");
        gridUsuarios.addColumn(Usuario::getUsuario).setHeader("Usuario");
        gridUsuarios.addColumn(usuario -> usuario.getRol() != null ? usuario.getRol().getCargo() : "").setHeader("Rol");
        gridUsuarios.addColumn(usuario -> usuario.getPersona() != null ? usuario.getPersona().getNombreCompleto() : "").setHeader("Persona");
        gridUsuarios.addColumn(CrearComponmenteActivoRenderer()).setHeader("Estado");

        gridUsuarios.asSingleSelect().addValueChangeListener(event -> editUsuario(event.getValue()));
    }

    private Component createEditorLayout() {
        Div editorDiv = new Div();
        editorDiv.setHeightFull();
        editorDiv.setClassName("editor-layout");
        Div div = new Div();
        div.setClassName("editor");
        editorDiv.add(div);
        formLayout.add(rolComboBox,personaComboBox,usuarioField, passwordField, activoCheckbox);
        save.addClickListener(event -> save());
        cancel.addClickListener(event -> clearForm());
        delete.addClickListener(event -> delete());

        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        binder.bindInstanceFields(this);
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
        // Agrega la búsqueda al layout
        HorizontalLayout searchDiv = new HorizontalLayout();
        searchDiv.addClassName("tophl");
        searchDiv.add(createSearchLayout());
        add(searchDiv);
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        wrapper.setSizeFull();
        wrapper.add(searchDiv,gridUsuarios);
        return wrapper;
    }

    private void refreshGrid() {
        // Actualización de los datos mostrados en el grid
        gridUsuarios.setItems(usuarioService.findAll());
    }

    private void setupSearch() {
        usuarioBusqueda.setPlaceholder("Buscar por usuario");
        rolBusqueda.setPlaceholder("Buscar por rol");
        rolBusqueda.setItemLabelGenerator(Rol::getCargo); // Asumiendo que Rol tiene un método getCargo
        rolBusqueda.setItems(rolService.findAll());
        personaBusqueda.setPlaceholder("Buscar por persona");
        personaBusqueda.setItemLabelGenerator(Persona::getNombreCompleto); // Asumiendo que Persona tiene un método getNombreCompleto
        personaBusqueda.setItems(personaService.findAll());

    }
    private void applyFilter() {
        String usernameValue = usuarioBusqueda.getValue();
        Rol rolValue = rolBusqueda.getValue();
        Persona personaValue = personaBusqueda.getValue();

        // Aquí deberías llamar a un método del servicio que permita filtrar por estos tres criterios.
        // Este es un ejemplo de cómo podría ser este método:
        List<Usuario> filteredUsers = usuarioService.findByUsernameRolAndPersona(usernameValue, rolValue, personaValue);
        gridUsuarios.setItems(filteredUsers);
    }
    private Component createSearchLayout() {
        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.add(usuarioBusqueda, rolBusqueda, personaBusqueda);
        return searchLayout;
    }

    private void setupForm() {
        // Configura los campos del formulario para usarlos con el Binder
        binder.forField(usuarioField)
                .withValidator(new StringLengthValidator(
                        "El nombre de usuario debe contener al menos 3 caracteres", 3, null))
                .bind(Usuario::getUsuario, Usuario::setUsuario);

         rolComboBox.setItems(rolService.findAll());
         rolComboBox.setItemLabelGenerator(Rol::getCargo); // Asumiendo que Rol tiene un método getCargo
         binder.forField(rolComboBox).bind(Usuario::getRol, Usuario::setRol);

         personaComboBox.setItems(personaService.findAll());
         personaComboBox.setItemLabelGenerator(Persona::getNombreCompleto); // Asumiendo que Persona tiene un método getNombreCompleto
         binder.forField(personaComboBox).bind(Usuario::getPersona, Usuario::setPersona);

        //activoCheckbox.setValue(personaService.);
        //activoCheckbox.setValue((Usuario::isActivo) ? Boolean.TRUE : Boolean.FALSE);
        //activoCheckbox.setValue(() ? Boolean.TRUE : Boolean.FALSE); // Asumiendo que Persona tiene un método getNombreCompleto
        binder.forField(activoCheckbox).bind(Usuario::isActivo, Usuario::setActivo);
    }

    private void save() {

        System.out.println("USUARIO: "+this.usuario );
        try {
            // Verifica si el usuario tiene información válida antes de intentar guardar
            if (usuarioField.isEmpty() && passwordField.isEmpty()) {
                Notification.show("No se puede guardar un usuario vacío.");
            }else{
                if (this.usuario == null) {
                    this.usuario = new Usuario();
                }
                binder.writeBean(this.usuario);
            }

            // Verifica si es un nuevo usuario o una actualización
            if (this.usuario.getId() == null) {
                // Nuevo usuario
                if (!passwordField.isEmpty()) {
                    this.usuario.setPassword(passwordUtils.encryptPassword(passwordField.getValue()));
                }
                // Asume que usuarioService.save() maneja tanto la creación como la actualización.
                usuarioService.save(this.usuario);
                Notification.show("Datos Agregados");
            } else {
                // Actualización de un usuario existente
                if (!passwordField.isEmpty()) {
                    this.usuario.setPassword(passwordUtils.encryptPassword(passwordField.getValue()));
                } else {
                    // Si el campo de contraseña está vacío, no actualices la contraseña
                    this.usuario.setPassword(Password);
                }
                usuarioService.update(this.usuario); // Asegúrate de que este método exista y haga lo que esperas
                Notification.show("Datos actualizados");
            }

            clearForm();
            refreshGrid();
            UI.getCurrent().navigate(UsuariosView.class);
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
        if (this.usuario != null) {
            usuarioService.delete(this.usuario);
            refreshGrid();
            clearForm();
            Notification.show("Usuario eliminado");
        }
    }

    private void clearForm() {
        this.usuario = null; // Limpiar la referencia al usuario actual
        this.usuario = new Usuario(); // Cambio clave aquí
        binder.readBean(this.usuario); // Ahora `usuario` nunca es null
        save.setText("Guardar"); // Cambiar el texto del botón de vuelta a "Guardar" cuando se limpia el formulario
    }

    private void editUsuario(Usuario usuario) {
        if (usuario == null) {
            clearForm();
        } else {
            this.usuario = usuario;
            binder.readBean(this.usuario);
            save.setText("Actualizar"); // Cambiar el texto del botón a "Actualizar"
            Password = this.usuario.getPassword();
            passwordField.clear(); // Limpiar el campo de contraseña
        }
    }
}