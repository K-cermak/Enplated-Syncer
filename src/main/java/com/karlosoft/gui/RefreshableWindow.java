package com.karlosoft.gui;

import javax.swing.*;

import com.karlosoft.Controller;

import java.awt.*;

public class RefreshableWindow {
    static JDialog dialog;
    static JProgressBar progressBar = new JProgressBar();;

    private static int currentFile = 0;
    private static int totalFiles = 0;
    private static String text = "";

    public static void setTotalFiles(int totalFiles) {
        RefreshableWindow.totalFiles = totalFiles;
    }

    public static void addFile() {
        currentFile++;
    }

    public static void resetData() {
        currentFile = 0;
        text = "";
    }

    public static void setText(String text) {
        RefreshableWindow.text = text;
    }

    public static void run() {
        //panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 10, 10));
        JScrollPane scrollPane = new JScrollPane(panel);  
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        JOptionPane jop = new JOptionPane();


        //text
        JLabel label = new JLabel(text);
        label.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        
        //progress bar
        progressBar.setMinimum(0);
        progressBar.setMaximum(totalFiles);
        progressBar.setValue(currentFile);
        progressBar.setStringPainted(true);

        //auto update percentage
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Popup.showMessage(2, "An error has occurred", "An error has occurred: " + e.getMessage());
                }
                progressBar.setValue(currentFile);
            }
        }).start();

        //text and progress to panel
        panel.add(label);
        panel.add(progressBar);

        //dialog
        dialog = jop.createDialog("Please wait..");
        dialog.setSize(300, 100);
        dialog.setContentPane(scrollPane);
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.setIconImage(new ImageIcon(Controller.getWorkingDirectory() + "/images/png-favicon.png").getImage());
        dialog.setVisible(true);
    }

    public static void closeWindow() {
        dialog.dispose();
    }
}
