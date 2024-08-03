package com.elolympus.views.ubigeo;

import com.elolympus.data.ubigeo.Distrito;
import com.elolympus.data.ubigeo.Provincia;
import com.elolympus.data.ubigeo.Departamento;
import com.elolympus.services.services.UbigeoService;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.html.Label; // Ensure this import is used for Label
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import  com.vaadin.flow.component.textfield.TextField;

@PageTitle("Ubigeo")
@Route(value = "ubigeo", layout = MainLayout.class)
@AnonymousAllowed
public class UbigeoView extends VerticalLayout {
    private ComboBox<Departamento> departamentoComboBox = new ComboBox<>("Departamento");
    private ComboBox<Provincia> provinciaComboBox = new ComboBox<>("Provincia");
    private ComboBox<Distrito> distritoComboBox = new ComboBox<>("Distrito");
    private TextField numeroUbigeoLabel = new TextField("Número de Ubigeo: ");

    @Autowired
    public UbigeoView(UbigeoService ubigeoService) {
        departamentoComboBox.setItemLabelGenerator(Departamento::getNombre);
        provinciaComboBox.setItemLabelGenerator(Provincia::getNombre);
        distritoComboBox.setItemLabelGenerator(Distrito::getNombre);

        departamentoComboBox.setItems(ubigeoService.getAllRegiones());
        departamentoComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                provinciaComboBox.setItems(ubigeoService.getProvinciasByRegion(event.getValue().getId()));
                numeroUbigeoLabel.setValue(ubigeoService.getNumeroUbigeo(event.getValue().getId(), "region"));
            }
        });

        provinciaComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                distritoComboBox.setItems(ubigeoService.getDistritosByProvincia(event.getValue().getId()));
                numeroUbigeoLabel.setValue(ubigeoService.getNumeroUbigeo(event.getValue().getId(), "provincia"));
            }
        });

        distritoComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                numeroUbigeoLabel.setValue(ubigeoService.getNumeroUbigeo(event.getValue().getId(), "distrito"));
            }
        });

        add(departamentoComboBox, provinciaComboBox, distritoComboBox, numeroUbigeoLabel);
    }
}
