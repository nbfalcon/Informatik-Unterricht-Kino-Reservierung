package org.informatikEHM.kinoReservierung.util.dbTable;

import org.informatikEHM.kinoReservierung.util.Refreshable;
import org.informatikEHM.kinoReservierung.util.SwingUtilitiesX;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

public class DBTable extends JTable implements Refreshable {
    public DBTable(DBTableModel model) {
        super(model);

        setInheritsPopupMenu(true);
        JPopupMenu rightClick = new JPopupMenu("Tabelle " + model.getTable());
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
        getActionMap().put("refresh", new AbstractAction("refresh") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                refresh();
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "refresh");

        DefaultTableCellRenderer intRendererRHS = new DefaultTableCellRenderer();
        // Everything else is LHS, so mixing int + other columns would lead to awkward results
        intRendererRHS.setHorizontalAlignment(SwingConstants.LEFT);
        setDefaultRenderer(Integer.class, intRendererRHS);

        setAutoCreateRowSorter(true);

        model.setComponentForErrorDialog(this);

        refresh();
    }

    @Override
    public void refresh() {
        SwingUtilitiesX.exception2Dialog(this, "Laden der Tabelle", getModel1()::refresh);
    }

    private DBTableModel getModel1() {
        return (DBTableModel) getModel();
    }

    @Override
    public TableCellRenderer getDefaultRenderer(Class<?> columnClass) {
        return super.getDefaultRenderer(columnClass);
    }
}
