package org.informatikEHM.kinoReservierung.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBTableModel implements TableModel, Refreshable {
    private final @NotNull String query;
    private final @NotNull ColumnInfo[] columns;
    private final @Nullable String table;
    private final int primaryKey;
    private final List<TableModelListener> listeners = new ArrayList<>();
    private final @NotNull Connection db;
    private List<Object[]> cachedRows = null;

    public DBTableModel(@NotNull Connection db, @NotNull String query, @NotNull ColumnInfo[] columns,
                        int primaryKey, @Nullable String table) {
        this.db = db;
        this.query = query;
        this.columns = columns;
        this.primaryKey = primaryKey;
        this.table = table;
    }

    @Override
    public int getRowCount() {
        return cachedRows == null ? 0 : cachedRows.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int i) {
        return columns[i].name;
    }

    @Override
    public Class<?> getColumnClass(int i) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int i, int i1) {
        return table != null && primaryKey != -1;
    }

    @Override
    public Object getValueAt(int i, int i1) {
        assert cachedRows != null;
        return cachedRows.get(i)[i1];
    }

    @Override
    public void setValueAt(Object o, int row, int col) {
        assert cachedRows != null;
        Object[] thisRow = cachedRows.get(row);
        thisRow[col] = o;
        if (table != null && primaryKey != -1) {
            try (PreparedStatement statement = db.prepareStatement(
                    String.format("UPDATE `%s` SET `%s`= ? WHERE `%s`= ?",
                            table, columns[col].resultSetName,
                            columns[primaryKey].resultSetName))) {
                statement.setString(1, o.toString()); // new value
                statement.setString(2, thisRow[primaryKey].toString()); // primary key
                statement.execute();
            } catch (SQLException e) {
                System.err.println("Update schlug fehl: " + e.getLocalizedMessage());
            }
        }
    }

    public void deleteRow(int row) throws SQLException {
        try (PreparedStatement statement = db.prepareStatement(String.format("DELETE FROM `%s` WHERE `%s` = ?", table, columns[primaryKey].resultSetName))){
            statement.setString(1, cachedRows.get(row)[primaryKey].toString());
            statement.execute();
            cachedRows.remove(row);
            for (TableModelListener listener : listeners) {
                listener.tableChanged(new TableModelEvent(this, row, row, -1, TableModelEvent.DELETE));
            }
        }
    }

    @Override
    public void refresh() throws SQLException {
        try (Statement statement = db.createStatement()) {
            List<Object[]> newRows;
            try (ResultSet results = statement.executeQuery(query)) {
                newRows = new ArrayList<>();
                while (results.next()) {
                    final Object[] row = new Object[columns.length];
                    for (int i = 0; i < columns.length; i++) {
                        row[i] = results.getString(columns[i].resultSetName);
                    }
                    newRows.add(row);
                }
            }
            this.cachedRows = newRows;
            for (TableModelListener listener : listeners) {
                listener.tableChanged(new TableModelEvent(this));
            }
        }
    }

    @Override
    public void addTableModelListener(TableModelListener tableModelListener) {
        listeners.add(tableModelListener);
    }

    @Override
    public void removeTableModelListener(TableModelListener tableModelListener) {
        listeners.remove(tableModelListener);
    }

    public static class ColumnInfo {
        public final String name;
        public final String resultSetName;

        public ColumnInfo(String name, String resultSetName) {
            this.name = name;
            this.resultSetName = resultSetName == null ? name : resultSetName;
        }
    }
}
