package com.karlosoft.gui;

import java.util.Arrays;
import java.awt.*;

public class FontLocalizator {
    public static String selectedFont = "";

    public static String returnFont() {
        if (selectedFont.equals("")) {
            if (findFont("Ubuntu")) {
                selectedFont = "Ubuntu";
            } else if (findFont("Open Sans")) {
                selectedFont = "Open Sans";
            } else if (findFont("Calibri")) {
                selectedFont = "Calibri";
            } else {
                Popup.showMessage(1, "Error loading font", "Could not load any default fonts.");
                System.exit(1);
            }
        }
        return selectedFont;
    }

    public static boolean findFont(String name) {
        return Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()).contains(name);
    }
}
