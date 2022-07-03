package com.karlosoft.gui;

import com.karlosoft.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Instance {
    /*
        export to file
        import file

        upload to github
        download from github
        
        deploy
        change settings
    */

    static JDialog dialog;

    public static void run(String instanceId, String name) {
        Controller.closeInstanceSelect();

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 30, 30));
        JScrollPane scrollPane = new JScrollPane(panel);  
        JOptionPane jop = new JOptionPane();


        //import from file
        JButton importButton = new JButton("Import from zip");
        importButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        importButton.addActionListener(e ->
        {
            String zipFile = Popup.selectFile("zip");
            if (!zipFile.equals("") && zipFile != null) {
                //if it is really zip
                if (zipFile.endsWith(".zip")) {
                    if (Popup.showConfirm("Import from zip", "Are you sure you want to import from zip? This will overwrite the current instance data. This action cannot be undone.")) {
                        Controller.importInstance(zipFile, instanceId);
                    }
                } else {
                    Popup.showMessage(1, "Import from zip", "The selected file is not a zip file.");
                }
            }
        });

        panel.add(importButton);


        //export to file
        JButton exportButton = new JButton("Export to zip");
        exportButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        exportButton.addActionListener(e ->
        {
            String exportLocation = Controller.selectFolder();
            if (!exportLocation.equals("") && exportLocation != null) {
                Controller.exportInstance(instanceId, exportLocation);
            }
        });

        panel.add(exportButton);


        //download from github
        JButton downloadButton = new JButton("Download from github");
        downloadButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        downloadButton.addActionListener(e ->
        {
            //todo
        });



        //upload to github
        JButton uploadButton = new JButton("Upload to github");
        uploadButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        uploadButton.addActionListener(e ->
        {
            //todo
        });

        //verify that github is set
        if (Controller.getConfigParameter(instanceId, "github").equals("false")) {
            uploadButton.setEnabled(false);
            downloadButton.setEnabled(false);
        } else {
            //check if github credientials are set
            if (Controller.getConfigParameter("appGlobal", "app.githubEmail").equals("") || Controller.getConfigParameter("appGlobal", "app.githubPassword").equals("anObject")) {
                //print error
                Popup.showMessage(2, "Github credientials are not set", "Synchronization with GitHub was disabled because the login credentials were not set in the global settings.");
                uploadButton.setEnabled(false);
                downloadButton.setEnabled(false);
            }
        }

        panel.add(downloadButton);
        panel.add(uploadButton);    


        //deploy to web
        JButton deployButton = new JButton("Deploy");
        deployButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        deployButton.addActionListener(e ->
        {
            //todo
        });

        if (Controller.getConfigParameter(instanceId, "url").equals("false")) {
            deployButton.setEnabled(false);
        }

        panel.add(deployButton);


        //change settings
        JButton settingsButton = new JButton("Change settings");
        settingsButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        settingsButton.addActionListener(e ->
        {
            //todo
        });

        panel.add(settingsButton);

        dialog = jop.createDialog("Instance: " + name);
        dialog.setSize(1000, 450);
        dialog.setContentPane(scrollPane);
        dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                Popup.closeOption();
            }
        });
        dialog.setVisible(true);
    }

    public static void close() {
        dialog.dispose();
    }
}