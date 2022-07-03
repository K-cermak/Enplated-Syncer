package com.karlosoft.gui;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.karlosoft.Controller;

import javax.swing.*;
import java.awt.*;

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

    public static String selectFile(String extension) {
        //select file on drive with jfilechoser
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(extension, extension));
        chooser.showOpenDialog(null);
        if (chooser.getSelectedFile() != null) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        return "";   
    }

    public static void closeOption() {
        //create window with 3 buttons - close app, close instance, cancel
        JDialog dialog = new JDialog();
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 30, 30));
        JScrollPane scrollPane = new JScrollPane(panel);
        JButton closeButton = new JButton("Close app");
        closeButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        closeButton.addActionListener(e ->
        {
            Controller.closeApp();
        });

        JButton closeInstanceButton = new JButton("Close instance");
        closeInstanceButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        closeInstanceButton.addActionListener(e ->
        {
            dialog.dispose();
            Controller.closeInstance();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        cancelButton.addActionListener(e ->
        {
            dialog.dispose();
        });

        panel.add(closeButton);
        panel.add(closeInstanceButton);
        panel.add(cancelButton);

        dialog.setContentPane(scrollPane);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setTitle("Close");
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
        dialog.setVisible(true);
    }
}
