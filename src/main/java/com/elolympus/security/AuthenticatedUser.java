package com.elolympus.security;

import com.elolympus.data.Administracion.Usuario;
import com.elolympus.services.repository.UsuarioRepository;
import com.elolympus.services.specifications.UsuarioSpecifications;
import com.vaadin.flow.spring.security.AuthenticationContext;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AuthenticatedUser {

    //private final UserRepository userRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuthenticationContext authenticationContext;

    public AuthenticatedUser(AuthenticationContext authenticationContext, UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.authenticationContext = authenticationContext;
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> get() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .flatMap(userDetails -> {
                    // Usar la query optimizada que carga persona y rol en una sola consulta
                    return usuarioRepository.findByUsuarioWithRelations(userDetails.getUsername());
                });
    }

    public void logout() {
        authenticationContext.logout();
    }

}
