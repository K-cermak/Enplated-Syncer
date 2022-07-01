package com.karlosoft.gui;

import javax.swing.*;

import com.karlosoft.Controller;

import java.awt.*;

public class GlobalSettingsPanel {

    static JDialog dialog;

    public static void run() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 30, 30));  
        JOptionPane jop = new JOptionPane();

        //input for github email
        JLabel githubEmailLabel = new JLabel("Github email:", JLabel.CENTER);
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

        //save
        JButton saveButton = new JButton("Save");
        saveButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        saveButton.addActionListener(e ->
        {
            Controller.setConfigParameter("appGlobal", "app.githubEmail", githubEmailText.getText());
            Controller.setConfigParameter("appGlobal", "app.githubPassword", String.valueOf(githubPasswordText.getPassword()));
            Popup.showMessage(0, "Success", "Succesfully edited");
            dialog.dispose();
        });
        panel.add(saveButton);

        //show panel
        dialog = jop.createDialog("Global settings - Enplated Syncer");
        dialog.setSize(1000, 450);
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    public static void close() {
        dialog.dispose();
    }
}
