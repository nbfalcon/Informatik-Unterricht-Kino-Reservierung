package org.informatikEHM.kinoReservierung.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public class SwingUtilitiesX {
    public static void exception2Dialog(@Nullable Component parent, @NotNull String failure, @NotNull ThrowingRunnable runMe) {
        try {
            runMe.run();
        } catch (Exception e) {
            failure += " scheiterte";

            StringWriter text = new StringWriter();
            text.append(failure).append(": ").append(e.getLocalizedMessage()).append("\n");
            e.printStackTrace(new PrintWriter(text));
            JOptionPane.showConfirmDialog(parent, failure, text.toString(), JOptionPane.YES_NO_OPTION);
        }
    }

    public interface ThrowingRunnable {
        void run() throws Exception;
    }
}
