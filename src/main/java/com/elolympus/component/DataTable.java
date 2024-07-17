package com.elolympus.component;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.function.ValueProvider;

import java.util.ArrayList;
import java.util.List;

public class DataTable<T> extends Grid<T> {

    private List<T> list = new ArrayList<>();
    private boolean isNumbered = false;
    private boolean isMultiSelect = false;

    public DataTable() {
        this(false,false);
    }
    public DataTable(boolean isMultiSelect) {
        this(false,isMultiSelect);
    }

    public DataTable(boolean isNumbered, boolean isMultiSelect) {
        super();
        this.isNumbered = isNumbered;
        this.isMultiSelect = isMultiSelect;
        if (this.isMultiSelect) {
            this.setSelectionMode(Grid.SelectionMode.MULTI);
        }
        this.setList(list);
        if(this.isNumbered){
            this.addCol(this::getIndexOfValue, "#");
        }
        this.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        this.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);

    }

    public List<T> getList() {
        return list;
    }

    public int getIndexOfValue(T value) {
        return list.indexOf(value)+1;
    };

    public void GetIsNumbered(){
        this.addCol(this::getIndexOfValue, "#").setWidth("25px");
    }

    public void SetRemoveAllColumns(){
        this.removeAllColumns();
    }

    public T getValue() {
        if(this.getSelectionModel().getFirstSelectedItem()==null){
            return null;
        }
        return this.getSelectionModel().getFirstSelectedItem().get();
    }

    public void setList(List<T> list) {
        System.out.println("TAMAÑO LISTA: " + list.size());
        this.list.clear();
        this.list.addAll(list);
        this.setItems(list);
    }

    public void ListClear() {
        this.list.clear();
        List<T> c = new ArrayList<>();
        this.setList(c);
    }

    public Grid.Column<T> addCol(ValueProvider<T, ?> valueProvider, String header) {
        Grid.Column<T> column = this.addColumn(valueProvider);
        column.setHeader(header);
        column.setAutoWidth(true);
        column.setSortable(true);
        column.setResizable(true);
        return column;
    }

}

