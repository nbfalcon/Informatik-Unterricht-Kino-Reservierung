package org.informatikEHM.kinoReservierung.ui;

import org.informatikEHM.kinoReservierung.util.DBTable;
import org.informatikEHM.kinoReservierung.util.DBTableModel;

import javax.swing.*;
import java.sql.Connection;

public class MainWindow extends JTabbedPane {
    public MainWindow(Connection db) throws Exception {
        super(TOP, SCROLL_TAB_LAYOUT);
        addDBTabs(db);
    }

    private void addDBTabs(Connection db) throws Exception {
        addTab("Filme", new DBTable(db, "SELECT * FROM film", new DBTableModel.ColumnInfo[]{
                new DBTableModel.ColumnInfo("Film Nummer", "idFilm"),
                new DBTableModel.ColumnInfo("Titel", "titel"),
                new DBTableModel.ColumnInfo("Dauer", "dauer"),
                new DBTableModel.ColumnInfo("FSK-Freigabe", "fsk")
        }, 0, "film"));
        addTab("Kinosäle", new DBTable(db, "SELECT * FROM kinosaal", new DBTableModel.ColumnInfo[]{
                new DBTableModel.ColumnInfo("Nummer", "idKinosaal"),
                new DBTableModel.ColumnInfo("Wie viele Plätze?", "anzahlPlaetze")
        }, 0, "kinosaal"));
        addTab("Kunden", new DBTable(db, "SELECT * FROM kunde", new DBTableModel.ColumnInfo[]{
                new DBTableModel.ColumnInfo("Kunden NR", "idKunde"),
                new DBTableModel.ColumnInfo("Nachname", "nachname"),
                new DBTableModel.ColumnInfo("Vorname", "vorname"),
                new DBTableModel.ColumnInfo("Geburtsdatum", "gebDatum")
        }, 0, "kunde"));
    }
}
