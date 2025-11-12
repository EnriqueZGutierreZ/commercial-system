package com.elolympus.views;

import com.elolympus.data.Administracion.Usuario;
import com.elolympus.security.AuthenticatedUser;
import com.elolympus.views.Administracion.ConfiguracionSistemaView;
import com.elolympus.views.Administracion.PersonasView;
import com.elolympus.views.Administracion.RolesView;
import com.elolympus.views.Administracion.UsuariosView;
import com.elolympus.views.Empresa.EmpresaView;
import com.elolympus.views.Empresa.SucursalView;
import com.elolympus.views.Logistica.*;
import com.elolympus.views.Ventas.BoletasView;
import com.elolympus.views.Ventas.CuentasPorCobrarView;
import com.elolympus.views.Ventas.FacturasView;
import com.elolympus.views.Ventas.NotasCreditoView;
import com.elolympus.views.direccion.Direccion2View;
import com.elolympus.views.Bienvenida.BienvenidaView;
import com.elolympus.views.reportes.ReportesView;
import com.elolympus.views.sobrenosotros.SobreNosotrosView;
import com.elolympus.views.ubigeo.UbigeoView;
import com.elolympus.views.inventario.DashboardInventarioView;
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
        if (accessChecker.hasAccess(FacturasView.class) || accessChecker.hasAccess(BoletasView.class) || accessChecker.hasAccess(NotasCreditoView.class) || accessChecker.hasAccess(CuentasPorCobrarView.class)){
            SideNavItem ventas = new SideNavItem("Ventas");
            ventas.setPrefixComponent(VaadinIcon.SHOP.create());
            if (accessChecker.hasAccess(FacturasView.class)) {
                ventas.addItem(new SideNavItem("Facturas", FacturasView.class, VaadinIcon.CLIPBOARD_CHECK.create()));
            }
            if (accessChecker.hasAccess(BoletasView.class)) {
                ventas.addItem(new SideNavItem("Boletas", BoletasView.class, VaadinIcon.CLIPBOARD_TEXT.create()));
            }
            if (accessChecker.hasAccess(NotasCreditoView.class)) {
                ventas.addItem(new SideNavItem("Notas Credito", NotasCreditoView.class, VaadinIcon.CLIPBOARD.create()));
            }
            if (accessChecker.hasAccess(CuentasPorCobrarView.class)) {
                ventas.addItem(new SideNavItem("Cuentas por Cobrar", CuentasPorCobrarView.class, VaadinIcon.MONEY.create()));
            }
            nav.addItem(ventas);
        }

        //LOGUISTICA
        if (accessChecker.hasAccess(AlmacenView.class) || accessChecker.hasAccess(KardexView.class) || accessChecker.hasAccess(DashboardInventarioView.class) || accessChecker.hasAccess(OrdenRegularizacionView.class) || accessChecker.hasAccess(ListOrdenCompraView.class) || accessChecker.hasAccess(ProductosView.class) || accessChecker.hasAccess(MarcasView.class) || accessChecker.hasAccess(LineasView.class) || accessChecker.hasAccess(UnidadesView.class)){
            SideNavItem ventas = new SideNavItem("Logistica");
            ventas.setPrefixComponent(VaadinIcon.CALC_BOOK.create());
            if (accessChecker.hasAccess(AlmacenView.class)) {
                ventas.addItem(new SideNavItem("Almacen", AlmacenView.class, VaadinIcon.PACKAGE.create()));
            }
            if (accessChecker.hasAccess(KardexView.class)) {
                ventas.addItem(new SideNavItem("Kardex", KardexView.class, VaadinIcon.PIN_POST.create()));
            }
            if (accessChecker.hasAccess(DashboardInventarioView.class)) {
                ventas.addItem(new SideNavItem("Dashboard Inventario", DashboardInventarioView.class, VaadinIcon.DASHBOARD.create()));
            }
            if (accessChecker.hasAccess(OrdenRegularizacionView.class)) {
                ventas.addItem(new SideNavItem("Orden Regularizacion", OrdenRegularizacionView.class, VaadinIcon.CALC_BOOK.create()));
            }
            if (accessChecker.hasAccess(ListOrdenCompraView.class)) {
                ventas.addItem(new SideNavItem("Orden de Compra", ListOrdenCompraView.class, VaadinIcon.CALC_BOOK.create()));
            }
            if (accessChecker.hasAccess(ProductosView.class)) {
                ventas.addItem(new SideNavItem("Productos", ProductosView.class, VaadinIcon.CART_O.create()));
            }
            
            // Submenú para gestión de catálogos
            SideNavItem catalogos = new SideNavItem("Catálogos");
            catalogos.setPrefixComponent(VaadinIcon.LIST.create());
            if (accessChecker.hasAccess(MarcasView.class)) {
                catalogos.addItem(new SideNavItem("Marcas", MarcasView.class, VaadinIcon.TAG.create()));
            }
            if (accessChecker.hasAccess(LineasView.class)) {
                catalogos.addItem(new SideNavItem("Líneas", LineasView.class, VaadinIcon.LINES.create()));
            }
            if (accessChecker.hasAccess(UnidadesView.class)) {
                catalogos.addItem(new SideNavItem("Unidades", UnidadesView.class, VaadinIcon.SCALE.create()));
            }
            if (catalogos.getChildren().findAny().isPresent()) {
                ventas.addItem(catalogos);
            }
            nav.addItem(ventas);
        }
        //ADMINISTRACION
        if (accessChecker.hasAccess(PersonasView.class) || accessChecker.hasAccess(RolesView.class) || accessChecker.hasAccess(UsuariosView.class) || accessChecker.hasAccess(ConfiguracionSistemaView.class)){
            SideNavItem ventas = new SideNavItem("Administración");
            ventas.setPrefixComponent(VaadinIcon.COG.create());
            if (accessChecker.hasAccess(PersonasView.class)) {
                ventas.addItem(new SideNavItem("Personas", PersonasView.class, VaadinIcon.USER_CHECK.create()));
            }
            if (accessChecker.hasAccess(RolesView.class)) {
                ventas.addItem(new SideNavItem("Roles", RolesView.class, VaadinIcon.USERS.create()));
            }
            if (accessChecker.hasAccess(UsuariosView.class)) {
                ventas.addItem(new SideNavItem("Usuarios", UsuariosView.class, VaadinIcon.USER.create()));
            }
            if (accessChecker.hasAccess(ConfiguracionSistemaView.class)) {
                ventas.addItem(new SideNavItem("Configuración", ConfiguracionSistemaView.class, VaadinIcon.TOOLS.create()));
            }
            nav.addItem(ventas);
        }
        //EMPRESA
        if (accessChecker.hasAccess(EmpresaView.class) || accessChecker.hasAccess(SucursalView.class)) {
            SideNavItem empresa = new SideNavItem("Empresa");
            empresa.setPrefixComponent(VaadinIcon.BUILDING.create());
            if (accessChecker.hasAccess(EmpresaView.class)) {
                empresa.addItem(new SideNavItem("Empresa", EmpresaView.class, VaadinIcon.BUILDING.create()));
            }
            if (accessChecker.hasAccess(SucursalView.class)) {
                empresa.addItem(new SideNavItem("Sucursal", SucursalView.class, VaadinIcon.BUILDING_O.create()));
            }
            nav.addItem(empresa);
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
