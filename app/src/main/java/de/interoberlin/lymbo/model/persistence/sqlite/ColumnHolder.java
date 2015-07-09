package de.interoberlin.lymbo.model.persistence.sqlite;

import java.util.ArrayList;
import java.util.List;

public class ColumnHolder {
    private List<Column> columns = new ArrayList<>();

    // --------------------
    // Constructors
    // --------------------

    public ColumnHolder() {
    }

    // --------------------
    // Methods
    // --------------------

    public void add(Column column) {
        this.columns.add(column);
    }

    public String[] getColumnNames() {
        List<String> columnNames = new ArrayList<>();

        for (Column c : columns) {
            columnNames.add(c.getName());
        }

        String[] columnNamesArray = new String[columnNames.size()];
        columnNames.toArray(columnNamesArray);

        return columnNamesArray;
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }
}
