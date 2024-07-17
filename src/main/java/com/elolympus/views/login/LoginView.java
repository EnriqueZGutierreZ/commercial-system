package com.elolympus.views.login;

import com.elolympus.security.AuthenticatedUser;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login")
public class LoginView extends Div implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;
    private final LoginForm lf;

    public LoginView(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
//        setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));
        addClassName("login-rich-content");
        LoginI18n i18n = LoginI18n.createDefault();
        LoginI18n.Form i18nForm = i18n.getForm();
        i18nForm.setTitle("Login");
        i18nForm.setUsername("Usuario");
        i18nForm.setPassword("Contraseña");
        i18nForm.setSubmit("Aceptar");
        i18nForm.setForgotPassword("Olvidaste tu contraseña?");
        i18n.setForm(i18nForm);


        LoginI18n.ErrorMessage i18nErrorMessage = i18n.getErrorMessage();
        i18nErrorMessage.setTitle("Nombre de usuario o contraseña incorrectos");
        i18nErrorMessage.setMessage(
                "Comprueba que el nombre de usuario y la contraseña son correctos y vuelve a intentarlo..");
        i18n.setErrorMessage(i18nErrorMessage);
        this.lf = new LoginForm();
        lf.getElement().getThemeList().add("dark");
        lf.setI18n(i18n);
        lf.setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));
        add(lf);
        lf.getElement().setAttribute("no-autofocus", "");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
//            Already logged in
//            setOpened(false);
            event.forwardTo("");
        }
        lf.setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
}
