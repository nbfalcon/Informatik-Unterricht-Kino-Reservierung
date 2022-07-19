package org.informatikEHM.kinoReservierung.ui;

import org.informatikEHM.kinoReservierung.util.DBTable;
import org.informatikEHM.kinoReservierung.util.DBTableModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class MainWindow extends JTabbedPane {
    public MainWindow(Connection db) throws Exception {
        super(TOP, SCROLL_TAB_LAYOUT);
        addDBTabs(db);
    }

    private void addDBTabs(Connection db) throws Exception {
        addDBTab(db, "Filme", "film", new DBTableModel.ColumnInfo[]{
                new DBTableModel.ColumnInfo("Film Nummer", "idFilm"),
                new DBTableModel.ColumnInfo("Titel", "titel"),
                new DBTableModel.ColumnInfo("Dauer", "dauer"),
                new DBTableModel.ColumnInfo("FSK-Freigabe", "fsk")
        });
        addDBTab(db, "Kinosäle", "kinosaal", new DBTableModel.ColumnInfo[]{
                new DBTableModel.ColumnInfo("Nummer", "idKinosaal"),
                new DBTableModel.ColumnInfo("Wie viele Plätze?", "anzahlPlaetze")
        });
        addDBTab(db, "Kunden", "kunde", new DBTableModel.ColumnInfo[]{
                new DBTableModel.ColumnInfo("Kunden NR", "idKunde"),
                new DBTableModel.ColumnInfo("Nachname", "nachname"),
                new DBTableModel.ColumnInfo("Vorname", "vorname"),
                new DBTableModel.ColumnInfo("Geburtsdatum", "gebDatum")
        });
    }

    private void addDBTab(Connection db, @NotNull String title, @NotNull String table, DBTableModel.ColumnInfo[] columns) throws Exception {
        final DBTableModel model = new DBTableModel(db, String.format("SELECT * FROM %s", table), columns, 0, table);
        final DBTable tableWidget = new DBTable(model);
        final Component tab = new JScrollPane(tableWidget);

        addTab(title, tab);
    }
}
