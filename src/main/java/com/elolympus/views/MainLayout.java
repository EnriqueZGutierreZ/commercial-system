package com.elolympus.views;

import com.elolympus.data.Administracion.Usuario;
import com.elolympus.security.AuthenticatedUser;
import com.elolympus.views.Administracion.PersonasView2;
import com.elolympus.views.Administracion.RolesView;
import com.elolympus.views.Administracion.UsuariosView;
import com.elolympus.views.Empresa.EmpresaView;
import com.elolympus.views.Logistica.*;
import com.elolympus.views.Ventas.BoletasView;
import com.elolympus.views.Ventas.FacturasView;
import com.elolympus.views.Ventas.NotasCreditoView;
import com.elolympus.views.Chat.ChatEView;
import com.elolympus.views.colaboracion.ColaboracionView;
import com.elolympus.views.direccion.Direccion2View;
import com.elolympus.views.editartabla.EditarTablaView;
import com.elolympus.views.formcreditcard.FormCreditCardView;
import com.elolympus.views.formpersona.FormPersonaView;
import com.elolympus.views.galeriaimagenes.GaleriaImagenesView;
import com.elolympus.views.grillaporsifunciona.GrillaporsifuncionaView;
import com.elolympus.views.Bienvenida.BienvenidaView;
import com.elolympus.views.reportes.ReportesView;
import com.elolympus.views.sobrenosotros.SobreNosotrosView;
import com.elolympus.views.ubigeo.UbigeoView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.util.Optional;

import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H2 viewTitle;

    private final AuthenticatedUser authenticatedUser;
    private final AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();

    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        H1 appName = new H1("Sistema Comercial");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {

        SideNav nav = new SideNav();

        //VENTAS
        if (accessChecker.hasAccess(BienvenidaView.class)){
            SideNavItem ventas = new SideNavItem("Ventas");
            ventas.setPrefixComponent(VaadinIcon.SHOP.create());
            ventas.addItem(new SideNavItem("Facturas", FacturasView.class, VaadinIcon.CLIPBOARD_CHECK.create()));
            ventas.addItem(new SideNavItem("Boletas", BoletasView.class, VaadinIcon.CLIPBOARD_TEXT.create()));
            ventas.addItem(new SideNavItem("Notas Credito", NotasCreditoView.class, VaadinIcon.CLIPBOARD.create()));
            nav.addItem(ventas);
        }

        //LOGUISTICA
        if (accessChecker.hasAccess(BienvenidaView.class)){
            SideNavItem ventas = new SideNavItem("Logistica");
            ventas.setPrefixComponent(VaadinIcon.CALC_BOOK.create());
            ventas.addItem(new SideNavItem("Almacen", AlmacenView.class, VaadinIcon.PACKAGE.create()));
            ventas.addItem(new SideNavItem("Kardex", KardexView.class, VaadinIcon.PIN_POST.create()));
            ventas.addItem(new SideNavItem("Orden Regularizacion", OrdenRegularizacionView.class, VaadinIcon.CALC_BOOK.create()));
            ventas.addItem(new SideNavItem("Orden de Compra", ListOrdenCompraView.class, VaadinIcon.CALC_BOOK.create()));
            ventas.addItem(new SideNavItem("Productos", ProductosView.class, VaadinIcon.CART_O.create()));
            
            // Submenú para gestión de catálogos
            SideNavItem catalogos = new SideNavItem("Catálogos");
            catalogos.setPrefixComponent(VaadinIcon.LIST.create());
            catalogos.addItem(new SideNavItem("Marcas", MarcasView.class, VaadinIcon.TAG.create()));
            catalogos.addItem(new SideNavItem("Líneas", LineasView.class, VaadinIcon.LINES.create()));
            catalogos.addItem(new SideNavItem("Unidades", UnidadesView.class, VaadinIcon.SCALE.create()));
            ventas.addItem(catalogos);
            
            nav.addItem(ventas);
        }
        //ADMINISTRACION
        if (accessChecker.hasAccess(BienvenidaView.class)){
            SideNavItem ventas = new SideNavItem("Administración");
            ventas.setPrefixComponent(VaadinIcon.COG.create());
            ventas.addItem(new SideNavItem("Personas", PersonasView2.class, VaadinIcon.USER_CHECK.create()));
            ventas.addItem(new SideNavItem("Roles", RolesView.class, VaadinIcon.USERS.create()));
            ventas.addItem(new SideNavItem("Usuarios", UsuariosView.class, VaadinIcon.USER.create()));
            nav.addItem(ventas);
        }
        //EMPRESA
        if (accessChecker.hasAccess(BienvenidaView.class)) {
            SideNavItem ventas = new SideNavItem("Empresa");
            ventas.setPrefixComponent(VaadinIcon.BUILDING.create());
            ventas.addItem(new SideNavItem("Empresa", EmpresaView.class, VaadinIcon.BUILDING.create()));
            nav.addItem(ventas);
        }

//        if (accessChecker.hasAccess(HelloWorldView.class)) {
//            SideNavItem navItem = new SideNavItem("Hello World", HelloWorldView.class, LineAwesomeIcon.GLOBE_SOLID.create());
//            SideNavItem subNavItem = new SideNavItem("Inbox", HelloWorldView.class, VaadinIcon.INBOX.create());
//            navItem.addItem(subNavItem);
//            nav.addItem(navItem);
//
//
//            // nav.addItem(new SideNavItem("Hello World", HelloWorldView.class, LineAwesomeIcon.GLOBE_SOLID.create()));
//
//            //SideNavItem messagesLink = new SideNavItem("Messages", MessagesView.class, VaadinIcon.ENVELOPE.create());
//            //messagesLink.addItem(new SideNavItem("Inbox", InboxView.class, VaadinIcon.INBOX.create()));
//            //messagesLink.addItem(new SideNavItem("Sent", SentView.class, VaadinIcon.PAPERPLANE.create()));
//            //messagesLink.addItem(new SideNavItem("Trash", TrashView.class, VaadinIcon.TRASH.create()));
//        }

        if (accessChecker.hasAccess(SobreNosotrosView.class)) {
            nav.addItem(new SideNavItem("Sobre Nosotros", SobreNosotrosView.class, LineAwesomeIcon.FILE.create()));

        }
        if (accessChecker.hasAccess(GrillaporsifuncionaView.class)) {
            nav.addItem(new SideNavItem("Grilla por si funciona", GrillaporsifuncionaView.class,
                    LineAwesomeIcon.TH_SOLID.create()));

        }
        if (accessChecker.hasAccess(ReportesView.class)) {
            nav.addItem(new SideNavItem("Reportes", ReportesView.class, LineAwesomeIcon.FILTER_SOLID.create()));

        }
        if (accessChecker.hasAccess(EditarTablaView.class)) {
            nav.addItem(new SideNavItem("Editar Tabla", EditarTablaView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));

        }
        if (accessChecker.hasAccess(FormPersonaView.class)) {
            nav.addItem(new SideNavItem("Form Persona", FormPersonaView.class, LineAwesomeIcon.USER.create()));

        }
        if (accessChecker.hasAccess(ColaboracionView.class)) {
            nav.addItem(
                    new SideNavItem("Colaboracion", ColaboracionView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));

        }
        if (accessChecker.hasAccess(Direccion2View.class)) {
            nav.addItem(new SideNavItem("Direccion", Direccion2View.class, LineAwesomeIcon.MAP_MARKER_SOLID.create()));

        }
        if (accessChecker.hasAccess(FormCreditCardView.class)) {
            nav.addItem(new SideNavItem("Form Credit Card", FormCreditCardView.class,
                    LineAwesomeIcon.CREDIT_CARD.create()));

        }
        if (accessChecker.hasAccess(ChatEView.class)) {
            nav.addItem(new SideNavItem("Chat E", ChatEView.class, LineAwesomeIcon.COMMENTS.create()));

        }
        if (accessChecker.hasAccess(GaleriaImagenesView.class)) {
            nav.addItem(new SideNavItem("Galeria Imagenes", GaleriaImagenesView.class,
                    LineAwesomeIcon.TH_LIST_SOLID.create()));

        }
        //agregar vista ubigeo
        if (accessChecker.hasAccess(BienvenidaView.class)) {
            nav.addItem(new SideNavItem("Ubigeo", UbigeoView.class, LineAwesomeIcon.GLOBE_SOLID.create()));
        }

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Optional<Usuario> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            Usuario user = maybeUser.get();

            Avatar avatar = new Avatar(user.getPersona().getNombres());
//            StreamResource resource = new StreamResource("profile-pic",
//                    () -> new ByteArrayInputStream(user.getProfilePicture()));
//            avatar.setImageResource(resource);
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getPersona().getNombres());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            userName.getSubMenu().addItem("Perfil de Usuario", e -> {
                Long userId = user.getId(); // Asumiendo que tienes un método getId() en tu entidad Usuario
                UI.getCurrent().navigate("perfil-usuario/" + userId);
            });

            userName.getSubMenu().addItem("Cerrar Sesion", e -> {
                authenticatedUser.logout();
            });

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }
        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
