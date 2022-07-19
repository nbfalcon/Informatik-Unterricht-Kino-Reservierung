package org.informatikEHM.kinoReservierung.util.dbTable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBTableModel implements TableModel {
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
        return columns[i].type.clazz;
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
                System.err.println("Ändern der Spalte scheiterte: " + e.getLocalizedMessage());
            }
        }
    }

    public void deleteRow(int row) throws SQLException {
        try (PreparedStatement statement = db.prepareStatement(String.format("DELETE FROM `%s` WHERE `%s` = ?", table, columns[primaryKey].resultSetName))) {
            statement.setString(1, cachedRows.get(row)[primaryKey].toString());
            statement.execute();
            cachedRows.remove(row);
            for (TableModelListener listener : listeners) {
                listener.tableChanged(new TableModelEvent(this, row, row, -1, TableModelEvent.DELETE));
            }
        }
    }

    public void refresh() throws SQLException {
        try (Statement statement = db.createStatement()) {
            List<Object[]> newRows;
            try (ResultSet results = statement.executeQuery(query)) {
                newRows = new ArrayList<>();
                while (results.next()) {
                    final Object[] row = new Object[columns.length];
                    for (int i = 0; i < columns.length; i++) {
                        row[i] = columns[i].type.getFromDB(results, columns[i].resultSetName);
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

    public @Nullable String getTable() {
        return table;
    }

    public enum ColumnType {
        STRING(String.class) {
            @Override
            public Object getFromDB(ResultSet result, String columnName) throws SQLException {
                return result.getString(columnName);
            }
        },
        INT(Integer.class) {
            @Override
            public Object getFromDB(ResultSet result, String columnName) throws SQLException {
                return result.getInt(columnName);
            }
        },
        DATE(Date.class) {
            @Override
            public Object getFromDB(ResultSet result, String columnName) throws SQLException {
                return result.getDate(columnName);
            }
        };

        public final Class<?> clazz;

        ColumnType(Class<?> clazz) {
            this.clazz = clazz;
        }

        public abstract Object getFromDB(ResultSet result, String columnName) throws SQLException;
    }

    public static class ColumnInfo {
        public final String name;
        public final String resultSetName;
        public final ColumnType type;

        public ColumnInfo(String name, String resultSetName, ColumnType type) {
            this.name = name;
            this.type = type;
            this.resultSetName = resultSetName == null ? name : resultSetName;
        }
    }
}
