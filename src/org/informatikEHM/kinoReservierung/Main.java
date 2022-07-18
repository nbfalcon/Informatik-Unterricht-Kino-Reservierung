package org.informatikEHM.kinoReservierung;

import org.informatikEHM.kinoReservierung.ui.MainWindow;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MariaDB Treiber nicht auf der CLASSPATH");
            System.exit(1);
        }

        if (args.length > 1) {
            System.err.println("Nutzung: java -jar kinoreservierung.jar [Datenbank, optional]");
            System.exit(1);
        }

        String dbString;
        if (args.length == 0) {
            dbString = "jdbc:mariadb://localhost/informatikKino";
        }
        else {
            dbString = args[0];
        }

        Connection db = DriverManager.getConnection(dbString);

        SwingUtilities.invokeLater(() -> {
            MainWindow app;
            try {
                app = new MainWindow(db);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            JFrame mainWindow = new JFrame();
            mainWindow.setContentPane(app);
            mainWindow.setSize(800, 600);
            mainWindow.setVisible(true);
            mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        });
    }
}
