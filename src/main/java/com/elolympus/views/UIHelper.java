package com.elolympus.views;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.function.ValueProvider;

import java.util.List;

/**
 * Created by [EnriqueZGutierreZ]
 */
public class UIHelper {

    public static Button createButton(String text, VaadinIcon icon, ComponentEventListener<ClickEvent<Button>> listener) {
        Button button = new Button(text, icon.create());
        button.addClickListener(listener);
        return button;
    }

    public static FormLayout createFormLayout(Component... components) {
        FormLayout formLayout = new FormLayout();
        formLayout.add(components);
        return formLayout;
    }

    public static <T> void configureGrid(Grid<T> grid, List<Grid.Column<T>> columns) {
//        grid.setHeight("86%");
//        for (Grid.Column<T> column : columns) {
//            grid.addColumn(ValueProvider <T, ?>);
//        }
    }
}
