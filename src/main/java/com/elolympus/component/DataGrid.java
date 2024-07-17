package com.elolympus.component;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.function.ValueProvider;

import java.util.ArrayList;
import java.util.List;

public class DataGrid<T> extends Grid<T> {

    private List<T> list = new ArrayList<>();
    private boolean isNumbered = false;
    private boolean isMultiSelect = false;

    public DataGrid() {
        this(false, false);
    }

    public DataGrid(boolean isMultiSelect) {
        this(false, isMultiSelect);
    }

    public DataGrid(boolean isNumbered, boolean isMultiSelect) {
        super();
        this.isNumbered = isNumbered;
        this.isMultiSelect = isMultiSelect;
        configureGrid();
    }

    private void configureGrid() {
        if (this.isMultiSelect) {
            this.setSelectionMode(Grid.SelectionMode.MULTI);
        }
        setList(this.list);
        if (this.isNumbered) {
            addNumberedColumn();
        }
        this.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);
    }

    private void addNumberedColumn() {
        this.addColumn(item -> {
            int index = list.indexOf(item) + 1;
            return index <= 0 ? "" : String.valueOf(index);
        }).setHeader("#").setAutoWidth(true).setSortable(false);
    }

    public void setList(List<T> newList) {
        this.list.clear();
        this.list.addAll(newList);
        this.setItems(newList);
    }

    public void clearList() {
        this.list.clear();
        setList(new ArrayList<>()); // Reset the list and update the grid
    }

    public Grid.Column<T> addColumn(ValueProvider<T, ?> valueProvider, String header) {
        Grid.Column<T> column = super.addColumn(valueProvider);
        column.setHeader(header);
        column.setAutoWidth(true).setSortable(true).setResizable(true);
        return column;
    }

    public List<T> getList() {
        return new ArrayList<>(this.list);
    }

    public T getSelectedValue() {
        return this.asSingleSelect().getValue();
    }

    // Additional methods to support your grid functionality can be added here
}
