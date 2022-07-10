package com.karlosoft.gui;

import javax.swing.*;

import com.karlosoft.Controller;

import java.awt.*;

public class GlobalSettingsPanel {

    static JDialog dialog;
    static String folder;

    public static void run() {
        folder = Controller.getConfigParameter("appGlobal", "app.dbPath");
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 30, 30));  
        JOptionPane jop = new JOptionPane();

        //input for github email
        JLabel githubEmailLabel = new JLabel("Github e-mail:", JLabel.CENTER);
        githubEmailLabel.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 20));
        panel.add(githubEmailLabel);

        JTextField githubEmailText = new JTextField();
        githubEmailText.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        githubEmailText.setText(Controller.getConfigParameter("appGlobal", "app.githubEmail"));
        panel.add(githubEmailText);

        //input for github password
        JLabel githubPasswordLabel = new JLabel("Github password:", JLabel.CENTER);
        githubPasswordLabel.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 20));
        panel.add(githubPasswordLabel);

        JPasswordField githubPasswordText = new JPasswordField();
        githubPasswordText.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        githubPasswordText.setText(Controller.getConfigParameter("appGlobal", "app.githubPassword"));
        panel.add(githubPasswordText);

        
        //database path
        JTextArea databasePathLabel = new JTextArea("                                 Database path:\n"+ folder +"");
        databasePathLabel.setEditable(false);
        databasePathLabel.setBackground(new Color(240, 240, 240));
        databasePathLabel.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 20));
        panel.add(databasePathLabel);

        //button for select
        JButton databasePathButton = new JButton("Select");
        databasePathButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        databasePathButton.addActionListener(e -> {
            String newFolder = Controller.selectFolder();
            if (!newFolder.equals("") && newFolder != null) {
                folder = newFolder;
                databasePathLabel.setText("                                 Database path:\n"+ folder +"");
            }
        });
        panel.add(databasePathButton);

        //save
        JButton saveButton = new JButton("Save");
        saveButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        saveButton.addActionListener(e ->
        {
            Controller.setConfigParameter("appGlobal", "app.githubEmail", githubEmailText.getText());
            Controller.setConfigParameter("appGlobal", "app.githubPassword", String.valueOf(githubPasswordText.getPassword()));
            Controller.setConfigParameter("appGlobal", "app.dbPath", folder);
            Popup.showMessage(0, "Success", "Successfully edited");
            dialog.dispose();
        });
        panel.add(saveButton);

        //cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        cancelButton.addActionListener(e -> dialog.dispose());
        panel.add(cancelButton);

        //show panel
        dialog = jop.createDialog("Global settings - Enplated Syncer " + Controller.getCurrentVersion());
        dialog.setSize(1000, 450);
        dialog.setContentPane(panel);
        dialog.setIconImage(new ImageIcon(Controller.getWorkingDirectory() + "/images/png-favicon.png").getImage());
        dialog.setVisible(true);
    }

    public static void close() {
        dialog.dispose();
    }
}
