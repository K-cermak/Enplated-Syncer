package com.karlosoft.gui;

import com.karlosoft.Controller;

import javax.swing.*;
import java.awt.*;

public class InstanceSelect {

    static JDialog dialog;

    public static void run(String[] instanceNames, String[] instanceCodes) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(instanceNames.length + 1, 2, 30, 30));
        JScrollPane scrollPane = new JScrollPane(panel);  
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        JOptionPane jop = new JOptionPane();

        JButton addButton = new JButton("Add a new instance");
        addButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        addButton.addActionListener(e ->
        {
            Controller.addInstance();
        });

        panel.add(addButton);

        JButton globalSettingsButton = new JButton("Global settings");
        globalSettingsButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        globalSettingsButton.addActionListener(e ->
        {
            Controller.globalSettingsPanel();
        });

        panel.add(globalSettingsButton);



        for (int i = 0; i < instanceNames.length; i++) {
            if (instanceNames[i] != null && !instanceNames[i].equals("")) {
                JLabel instanceNameText = new JLabel(instanceNames[i] + " (ID: " + instanceCodes[i] + ")", JLabel.CENTER);
                instanceNameText.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 20));
                panel.add(instanceNameText);

                final String id = instanceCodes[i];
                final String name = instanceNames[i];
                JButton openButton = new JButton("Open");
                openButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
                openButton.addActionListener(e ->
                {
                    Controller.runInstance(id, name);
                });

                panel.add(openButton);
            }
        }

        //global info button
        JButton globalInfoButton = new JButton("About Enplated Syncer");
        globalInfoButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        globalInfoButton.addActionListener(e ->
        {
            Controller.globalInfoPanel();
        });
        panel.add(globalInfoButton);

        dialog = jop.createDialog("Select your syncer instance - Enplated Syncer " + Controller.getCurrentVersion());
        dialog.setSize(1000, 450);
        dialog.setContentPane(scrollPane);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setIconImage(new ImageIcon(Controller.getWorkingDirectory() + "/images/png-favicon.png").getImage());
        dialog.setVisible(true);
    }

    public static void close() {
        dialog.dispose();
    }
}