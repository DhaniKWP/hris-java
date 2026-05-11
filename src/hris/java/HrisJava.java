package hris.java;

import com.formdev.flatlaf.FlatLightLaf;
import form.LoginForm;

import javax.swing.*;
import java.awt.*;

public class HrisJava {

    public static void main(String[] args) {

        try {

            FlatLightLaf.setup();

            // =================================================
            // GLOBAL FONT
            // =================================================

            UIManager.put(
                    "defaultFont",
                    new Font("Segoe UI", Font.PLAIN, 14)
            );

            // =================================================
            // ROUND COMPONENT
            // =================================================

            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("TextComponent.arc", 12);

            // =================================================
            // FOCUS
            // =================================================

            UIManager.put("Component.focusWidth", 1);
            UIManager.put("Button.innerFocusWidth", 0);

            // =================================================
            // TEXTFIELD PADDING
            // =================================================

            UIManager.put(
                    "TextField.margin",
                    new Insets(6, 12, 6, 12)
            );

            UIManager.put(
                    "PasswordField.margin",
                    new Insets(6, 12, 6, 12)
            );

            // =================================================
            // TABLE STYLE
            // =================================================

            UIManager.put("Table.rowHeight", 40);

            UIManager.put("Table.showVerticalLines", false);

            UIManager.put("Table.showHorizontalLines", true);

            // =================================================
            // SCROLLBAR
            // =================================================

            UIManager.put("ScrollBar.width", 10);

        } catch (Exception e) {

            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {

            new LoginForm().setVisible(true);
        });
    }
}