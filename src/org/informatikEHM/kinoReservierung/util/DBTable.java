package org.informatikEHM.kinoReservierung.util;

import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.SQLException;

public class DBTable extends JTable implements Refreshable {
    public DBTable(Connection db, String query, DBTableModel.ColumnInfo[] columns, int primaryKey, @Nullable String table) throws Exception {
        super(new DBTableModel(db, query, columns, primaryKey, table));

        setInheritsPopupMenu(true);
        JPopupMenu rightClick = new JPopupMenu("Tabelle " + table);
        rightClick.add(new JMenuItem(new AbstractAction("Zeile Löschen") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (isEnabled()) {
                    try {
                        for (int row : getSelectedRows()) {
                            getModel1().deleteRow(row);
                        }
                    } catch (SQLException e) {
                        System.err.println("Zeilen löschen scheiterte: " + e.getLocalizedMessage());
                    }
                }
            }
        }));
        setComponentPopupMenu(rightClick);

        refresh();
    }

    @Override
    public void refresh() throws Exception {
        getModel1().refresh();
    }

    private DBTableModel getModel1() {
        return (DBTableModel) getModel();
    }
}
