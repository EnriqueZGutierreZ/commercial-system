package com.elolympus.views.Persona;

import com.elolympus.data.Administracion.Usuario;
import com.elolympus.security.PasswordUtils;
import com.elolympus.services.services.UsuarioService;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;

import java.util.Optional;

@PageTitle("Perfil de Usuario")
@Route(value = "perfil-usuario",layout = MainLayout.class)
@PermitAll
public class PerfilUsuarioView extends VerticalLayout implements HasUrlParameter<String> {

    private final UsuarioService usuarioService;
    private final PasswordUtils passwordUtils;
    private PasswordField password = new PasswordField("Nueva Contraseña");
    private PasswordField confirmPassword = new PasswordField("Confirmar Nueva Contraseña");
    private TextField nombre = new TextField("Nombre");
    private TextField apellidos = new TextField("Apellidos");
    // Añade aquí otros campos relevantes de Persona
    private Button save = new Button("Guardar Cambios");

    private String userId;

    public PerfilUsuarioView( UsuarioService usuarioService, PasswordUtils passwordUtils) {
        this.usuarioService = usuarioService;
        this.passwordUtils = passwordUtils;
        addClassName("perfil-usuario-view");
        setSizeFull();

        FormLayout formLayout = new FormLayout();
        formLayout.add(nombre, apellidos, password, confirmPassword);
        add(formLayout, createButtonsLayout());

        save.addClickListener(e -> {
            if(validatePassword()){
                actualizarUsuario();
            } else {
                Notification.show("La confirmación de contraseña no coincide.");
            }
        });
    }
    private void actualizarUsuario() {
        try {
            // Usar el método optimizado que carga las relaciones
            Optional<Usuario> optionalUsuario = usuarioService.getWithRelations(Long.parseLong(userId));
            if (optionalUsuario.isPresent()) {
                Usuario usuario = optionalUsuario.get();
                
                // Encriptar la nueva contraseña si se proporcionó
                if (password.getValue() != null && !password.getValue().trim().isEmpty()) {
                    String encryptedPassword = passwordUtils.encryptPassword(password.getValue());
                    usuario.setPassword(encryptedPassword);
                }

                // Actualizar datos de persona si existen
                if (usuario.getPersona() != null) {
                    usuario.getPersona().setNombres(nombre.getValue());
                    usuario.getPersona().setApellidos(apellidos.getValue());
                }

                // Guardar el usuario actualizado
                usuarioService.save(usuario);
                Notification.show("Perfil actualizado correctamente.");
                
                // Limpiar campos de contraseña
                password.clear();
                confirmPassword.clear();
            } else {
                Notification.show("Usuario no encontrado.");
            }
        } catch (NumberFormatException e) {
            Notification.show("ID de usuario inválido.");
        } catch (Exception e) {
            Notification.show("Error al actualizar el perfil: " + e.getMessage());
        }
    }

    private Div createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Div buttonLayout = new Div(save);
        buttonLayout.addClassName("button-layout");
        return buttonLayout;
    }

    private boolean validatePassword() {
        return password.getValue().equals(confirmPassword.getValue());
    }

    private void cargarDatosUsuario() {
        if (userId != null) {
            try {
                // Usar el método optimizado que carga las relaciones
                Optional<Usuario> optionalUsuario = usuarioService.getWithRelations(Long.parseLong(userId));
                if (optionalUsuario.isPresent()) {
                    Usuario usuario = optionalUsuario.get();
                    // Verificar que persona existe antes de acceder a sus propiedades
                    if (usuario.getPersona() != null) {
                        nombre.setValue(usuario.getPersona().getNombres() != null ? 
                                      usuario.getPersona().getNombres() : "");
                        apellidos.setValue(usuario.getPersona().getApellidos() != null ? 
                                         usuario.getPersona().getApellidos() : "");
                    }
                } else {
                    Notification.show("Usuario no encontrado");
                }
            } catch (NumberFormatException e) {
                Notification.show("ID de usuario inválido");
            }
        }
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String parameter) {
        this.userId = parameter;
        cargarDatosUsuario();
    }
}