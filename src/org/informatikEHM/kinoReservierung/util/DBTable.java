package org.informatikEHM.kinoReservierung.util;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class DBTable extends JTable implements Refreshable {
    public DBTable(DBTableModel model) throws Exception {
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

        DefaultTableCellRenderer intRendererRHS = new DefaultTableCellRenderer();
        // Everything else is LHS, so mixing int + other columns would lead to awkward results
        intRendererRHS.setHorizontalAlignment(SwingConstants.LEFT);
        setDefaultRenderer(Integer.class, intRendererRHS);

        setAutoCreateRowSorter(true);

        refresh();
    }

    @Override
    public void refresh() throws Exception {
        getModel1().refresh();
    }

    private DBTableModel getModel1() {
        return (DBTableModel) getModel();
    }

    @Override
    public TableCellRenderer getDefaultRenderer(Class<?> columnClass) {
        return super.getDefaultRenderer(columnClass);
    }
}
