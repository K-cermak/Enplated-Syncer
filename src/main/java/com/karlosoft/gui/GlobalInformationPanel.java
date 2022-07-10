package com.karlosoft.gui;

import javax.swing.*;

import com.karlosoft.Controller;

import java.awt.*;

public class GlobalInformationPanel {
    static JDialog dialog;

    public static void run() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2, 30, 30));  
        JOptionPane jop = new JOptionPane();

        //current version
        JLabel currentVersionLabel = new JLabel("Version installed:", JLabel.CENTER);
        currentVersionLabel.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 20));
        panel.add(currentVersionLabel);

        JLabel currentVersionText = new JLabel(Controller.getCurrentVersion());
        currentVersionText.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 20));
        panel.add(currentVersionText);


        //installed folder
        JLabel installFolderLabel = new JLabel("Installed in folder:", JLabel.CENTER);
        installFolderLabel.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 20));
        panel.add(installFolderLabel);

        JLabel installFolderText = new JLabel(Controller.getWorkingDirectory());
        installFolderText.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 12));
        panel.add(installFolderText);


        //authors
        JLabel authorsLabel = new JLabel("Authors:", JLabel.CENTER);
        authorsLabel.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 20));
        panel.add(authorsLabel);

        JLabel authorsText = new JLabel("Karel Čermák - k-cermak.com");
        authorsText.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        panel.add(authorsText);


        //donate
        JLabel donateLabel = new JLabel("Donate:", JLabel.CENTER);
        donateLabel.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 20));
        panel.add(donateLabel);

        JButton donateButton = new JButton("Donate <3");
        donateButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        donateButton.addActionListener(e ->
        {
            Controller.openUrl("https://enplated-syncer.k-cermak.com/donate");
        });
        panel.add(donateButton);


        //license
        JLabel licenseLabel = new JLabel("License:", JLabel.CENTER);
        licenseLabel.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 20));
        panel.add(licenseLabel);

        JButton licenseButton = new JButton("Open License text");
        licenseButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        licenseButton.addActionListener(e ->
        {
            Controller.openUrl("https://enplated-syncer.k-cermak.com/license");
        });
        panel.add(licenseButton);


        //website
        JLabel websiteLabel = new JLabel("Website:", JLabel.CENTER);
        websiteLabel.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 20));
        panel.add(websiteLabel);

        JButton websiteButton = new JButton("Open Website");
        websiteButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        websiteButton.addActionListener(e ->
        {
            Controller.openUrl("https://enplated-syncer.k-cermak.com");
        });
        panel.add(websiteButton);

        //github
        JLabel githubLabel = new JLabel("Github:", JLabel.CENTER);
        githubLabel.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 20));
        panel.add(githubLabel);

        JButton githubButton = new JButton("Open Github");
        githubButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        githubButton.addActionListener(e ->
        {
            Controller.openUrl("https://github.com/K-cermak/Enplated-Syncer");
        });
        panel.add(githubButton);





        //show panel
        dialog = jop.createDialog("About Enplated Syncer " + Controller.getCurrentVersion());
        dialog.setSize(1000, 450);
        dialog.setContentPane(panel);
        dialog.setIconImage(new ImageIcon(Controller.getWorkingDirectory() + "/images/png-favicon.png").getImage());
        dialog.setVisible(true);
    }
}
