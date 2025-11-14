package com.elolympus.views.Administracion;

import com.elolympus.data.Administracion.Persona;
import com.elolympus.data.Administracion.Rol;
import com.elolympus.data.Administracion.Usuario;
import com.elolympus.security.PasswordUtils;
import com.elolympus.services.services.PersonaService;
import com.elolympus.services.services.RolService;
import com.elolympus.services.services.UsuarioService;
import com.elolympus.services.services.AbstractCrudService;
import com.elolympus.views.AbstractCrudView;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
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
public class UsuariosView extends AbstractCrudView<Usuario> {

    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final PersonaService personaService;
    private final PasswordUtils passwordUtils;

    // Componentes UI adicionales
    private final TextField usuarioField = new TextField("Usuario");
    private final PasswordField passwordField = new PasswordField("Contraseña");
    private final Checkbox activoCheckbox = new Checkbox("Activo");
    private final ComboBox<Rol> rolComboBox = new ComboBox<>("Rol");
    private final ComboBox<Persona> personaComboBox = new ComboBox<>("Persona");

    // Componentes de búsqueda
    private final TextField usuarioBusqueda = new TextField("Usuario");
    private final ComboBox<Rol> rolBusqueda = new ComboBox<>("Rol");
    private final ComboBox<Persona> personaBusqueda = new ComboBox<>("Persona");

    private String savedPassword;

    @Autowired
    public UsuariosView(UsuarioService usuarioService, RolService rolService, PersonaService personaService, PasswordUtils passwordUtils) {
        super();
        this.usuarioService = usuarioService;
        this.rolService = rolService;
        this.personaService = personaService;
        this.passwordUtils = passwordUtils;
        initialize();
    }

    @Override
    protected Class<Usuario> getEntityClass() {
        return Usuario.class;
    }

    @Override
    protected AbstractCrudService<Usuario, ?> getService() {
        return usuarioService;
    }

    @Override
    protected String getViewClassName() {
        return "usuario-view";
    }

    @Override
    protected Class<? extends Component> getViewClass() {
        return UsuariosView.class;
    }

    @Override
    protected String getEntityIdParam() {
        return "UsuarioID";
    }

    @Override
    protected String getEditRouteTemplate() {
        return "usuario/%s/edit";
    }

    @Override
    protected String getEntityName() {
        return "Usuario";
    }

    @Override
    protected Usuario createNewEntity() {
        return new Usuario();
    }

    @Override
    protected void configureBinder() {
        // Configurar binding explícito para cada campo
        binder.forField(usuarioField)
                .withValidator(new StringLengthValidator(
                        "El nombre de usuario debe contener al menos 3 caracteres", 3, null))
                .bind(Usuario::getUsuario, Usuario::setUsuario);
        
        binder.forField(rolComboBox)
                .bind(Usuario::getRol, Usuario::setRol);
        
        binder.forField(personaComboBox)
                .bind(Usuario::getPersona, Usuario::setPersona);
        
        binder.forField(activoCheckbox)
                .bind(Usuario::isActivo, Usuario::setActivo);
        
        // Nota: passwordField se maneja especialmente en beforeSave()
    }

    @Override
    protected void configureGrid(Grid<Usuario> grid) {
        grid.addColumn(createActivoRenderer()).setHeader("Activo").setAutoWidth(true);
        grid.addColumn(Usuario::getUsuario).setHeader("Usuario").setAutoWidth(true);
        grid.addColumn(usuario -> usuario.getRol() != null ? usuario.getRol().getCargo() : "").setHeader("Rol").setAutoWidth(true);
        grid.addColumn(usuario -> usuario.getPersona() != null ? usuario.getPersona().getNombreCompleto() : "").setHeader("Persona").setAutoWidth(true);
    }

    @Override
    protected void configureFormLayout(FormLayout formLayout) {
        // Configurar items de ComboBoxes
        rolComboBox.setItems(rolService.findAll());
        rolComboBox.setItemLabelGenerator(Rol::getCargo);
        
        personaComboBox.setItems(personaService.findAll());
        personaComboBox.setItemLabelGenerator(Persona::getNombreCompleto);
        
        // Binding se configura en configureBinder() para mantener consistencia

        formLayout.add(rolComboBox, personaComboBox, usuarioField, passwordField, activoCheckbox);
    }

    @Override
    protected boolean hasFilters() {
        return true;
    }

    @Override
    protected HorizontalLayout createFilterLayout() {
        usuarioBusqueda.setPlaceholder("Buscar por usuario");
        usuarioBusqueda.addValueChangeListener(e -> applyFilter());

        rolBusqueda.setPlaceholder("Buscar por rol");
        rolBusqueda.setItemLabelGenerator(Rol::getCargo);
        rolBusqueda.setItems(rolService.findAll());
        rolBusqueda.addValueChangeListener(e -> applyFilter());

        personaBusqueda.setPlaceholder("Buscar por persona");
        personaBusqueda.setItemLabelGenerator(Persona::getNombreCompleto);
        personaBusqueda.setItems(personaService.findAll());
        personaBusqueda.addValueChangeListener(e -> applyFilter());

        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.add(usuarioBusqueda, rolBusqueda, personaBusqueda);
        return searchLayout;
    }

    private void applyFilter() {
        String usernameValue = usuarioBusqueda.getValue();
        Rol rolValue = rolBusqueda.getValue();
        Persona personaValue = personaBusqueda.getValue();

        List<Usuario> filteredUsers = usuarioService.findByUsernameRolAndPersona(usernameValue, rolValue, personaValue);
        grid.setItems(filteredUsers);
    }

    @Override
    protected void setupFilterListeners() {
        // Ya configurado en createFilterLayout
    }

    @Override
    protected void beforeSave(Usuario entity) {
        // Lógica especial de contraseñas
        if (usuarioField.isEmpty() && passwordField.isEmpty()) {
            throw new IllegalStateException("No se puede guardar un usuario vacío.");
        }

        if (entity.getId() == null) {
            // Nuevo usuario
            if (!passwordField.isEmpty()) {
                entity.setPassword(passwordUtils.encryptPassword(passwordField.getValue()));
            }
        } else {
            // Actualización de usuario existente
            if (!passwordField.isEmpty()) {
                entity.setPassword(passwordUtils.encryptPassword(passwordField.getValue()));
            } else {
                // Mantener contraseña anterior si no se cambió
                entity.setPassword(savedPassword);
            }
        }
    }

    @Override
    protected void afterSave(Usuario entity) {
        passwordField.clear();
    }

    @Override
    protected void populateForm(Usuario entity) {
        super.populateForm(entity);
        if (entity != null) {
            savedPassword = entity.getPassword();
            passwordField.clear();
        }
    }

    @Override
    protected void clearFilters() {
        if (usuarioBusqueda != null) usuarioBusqueda.clear();
        if (rolBusqueda != null) rolBusqueda.clear();
        if (personaBusqueda != null) personaBusqueda.clear();
    }

}