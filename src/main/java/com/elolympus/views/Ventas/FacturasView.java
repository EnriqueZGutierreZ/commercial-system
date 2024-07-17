package com.elolympus.views.Ventas;

import com.elolympus.component.DataGrid;
import com.elolympus.component.DataTable;
import com.elolympus.data.Administracion.Persona;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;

import javax.xml.crypto.Data;

@PageTitle("Facturas")
@Route(value = "facturas", layout = MainLayout.class)
@PermitAll
public class FacturasView extends HorizontalLayout {

    private TextField name;
    private Button sayHello;

    public DataTable<Persona> grilla = new DataTable<>();

    public DataGrid<Persona> dataGrid = new DataGrid<>(true,true);

    public FacturasView() {
        name = new TextField("Facturas");
        sayHello = new Button("Say hello");
        sayHello.addClickListener(e -> {
            Notification.show("Hello " + name.getValue());
        });
        sayHello.addClickShortcut(Key.ENTER);
        setMargin(true);
        setVerticalComponentAlignment(Alignment.END, name, sayHello);

        grilla.addColumn(Persona::getNombres).setHeader("Nombres")       .setAutoWidth(true);

        dataGrid.addColumn(Persona::getApellidos,"APELLIDOS");
        add(name, sayHello, dataGrid);
    }

}
