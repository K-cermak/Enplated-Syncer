package com.karlosoft.gui;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Popup {
    public static void showMessage(int type, String title, String message) {
        /*
         * type: 0 - Success
         *       1 - Error
         *       2 - Warning
         *       3 - Information
         */

        int messageType = 0;

        if (type == 0) {
            messageType = JOptionPane.INFORMATION_MESSAGE;
        } else if (type == 1) {
            messageType = JOptionPane.ERROR_MESSAGE;
        } else if (type == 2) {
            messageType = JOptionPane.WARNING_MESSAGE;
        } else if (type == 3) {
            messageType = JOptionPane.INFORMATION_MESSAGE;
        }
   
        JFrame f = new JFrame();
        JOptionPane.showMessageDialog(f, message, title, messageType);
    }

    public static boolean showConfirm(String title, String message) {
        int confirm = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            return true;
        }
        return false;
    }

    public static String simpleInput(String title, String message) {
        String input = JOptionPane.showInputDialog(null, message, title, JOptionPane.QUESTION_MESSAGE);
        return input;
    }

    public static String selectFolder() {
        //select folder on drive with jfilechoser
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.showOpenDialog(null);
        if (chooser.getSelectedFile() != null) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        return "";   
    }
}
