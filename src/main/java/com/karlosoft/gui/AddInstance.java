package com.karlosoft.gui;

import javax.swing.*;

import com.karlosoft.Controller;

import java.awt.*;

public class AddInstance {
    
    static JDialog dialog;
    static String folder = "";
    
    public static void run() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2, 30, 30));  
        JOptionPane jop = new JOptionPane();

        //name
        JLabel nameLabel = new JLabel("Name");
        JTextField nameField = new JTextField();
        panel.add(nameLabel);
        panel.add(nameField);

        //select folder
        JLabel folderLabel = new JLabel("Folder");
        panel.add(folderLabel);

        //select folder dialog
        JButton folderButton = new JButton("Select folder");
        folderButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.showOpenDialog(null);
            if (chooser.getSelectedFile() != null) {
                folder = chooser.getSelectedFile().getAbsolutePath();
                folderLabel.setText("Folder (selected: " + chooser.getSelectedFile().getAbsolutePath() + ")");
            }
            
        });
        panel.add(folderButton);

        
        //select if db
        JLabel dbLabel = new JLabel("Database", JLabel.CENTER);
        dbLabel.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 20));
        panel.add(dbLabel);

        JCheckBox dbCheckBox = new JCheckBox();
        dbCheckBox.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        panel.add(dbCheckBox);



        //add instance button
        JButton addInstanceButton = new JButton("Add instance");
        JTextField urlTextField = new JTextField();
        JTextField tokenTextField = new JTextField();
        JTextField secretTextField = new JTextField();

        
        //select if deployable
        JLabel deployableLabel = new JLabel("Deployable", JLabel.CENTER);
        deployableLabel.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 20));
        panel.add(deployableLabel);

        JCheckBox deployableCheckBox = new JCheckBox();
        deployableCheckBox.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        deployableCheckBox.addActionListener(
            e -> {
                if (deployableCheckBox.isSelected()) {
                    //add input for url
                    JLabel urlLabel = new JLabel("Url", JLabel.CENTER);
                    urlLabel.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 20));
                    panel.add(urlLabel);

                    urlTextField.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
                    panel.add(urlTextField);

                    //add input for token
                    JLabel tokenLabel = new JLabel("Token", JLabel.CENTER);
                    tokenLabel.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 20));
                    panel.add(tokenLabel);

                    tokenTextField.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
                    panel.add(tokenTextField);

                    //add input for secret
                    JLabel secretLabel = new JLabel("Secret", JLabel.CENTER);
                    secretLabel.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 20));
                    panel.add(secretLabel);

                    secretTextField.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
                    panel.add(secretTextField);
                    panel.add(addInstanceButton);
                    
                } else {
                    //delete url
                    for (byte i = 0; i < 7; i++) {
                        panel.remove(panel.getComponentCount() - 1);
                    }
                    panel.add(addInstanceButton);
                }
                dialog.setContentPane(panel);
            }
        );

        addInstanceButton.addActionListener(e -> {
            while (true) {
                //if name is empty
                if (nameField.getText().isEmpty()) {
                    Popup.showMessage(1, "Name is empty", "Name cannot be empty");
                    break;
                }

                //if name contains ,
                if (nameField.getText().contains(",")) {
                    Popup.showMessage(1, "Name contains ,", "Name cannot contain ,");
                    break;
                }

                //if folder is empty
                if (folder.isEmpty()) {
                    Popup.showMessage(1, "Folder is empty", "Folder cannot be empty");
                    break;
                }

                //if folder does not exist
                if (!Controller.folderExists(folder)) {
                    Popup.showMessage(1, "Folder does not exist", "Folder does not exist");
                    break;
                }

                //if deployable checked
                if (deployableCheckBox.isSelected()) {
                    //if url is empty
                    if (urlTextField.getText().isEmpty()) {
                        Popup.showMessage(1, "Url is empty", "Url cannot be empty");
                        break;
                    }

                    //if token is empty
                    if (tokenTextField.getText().isEmpty()) {
                        Popup.showMessage(1, "Token is empty", "Token cannot be empty");
                        break;
                    }

                    //if secret is empty
                    if (secretTextField.getText().isEmpty()) {
                        Popup.showMessage(1, "Secret is empty", "Secret cannot be empty");
                        break;
                    }
                }

                String gitHubUrl = "";
                if (Popup.showConfirm("GitHub repository", "Do you want to add a GitHub repository?")) {
                    while (gitHubUrl.equals("")) {
                        gitHubUrl = Popup.simpleInput("GitHub repository", "Enter the url of the repository");
                        if (gitHubUrl.equals("")) {
                            Popup.showMessage(1, "Url is empty", "Url cannot be empty");
                        }
                    }
                }

                String id = Controller.generateRandomId();
                if (Controller.createDefaultConfFile(id)) {
                    //continue
                    Controller.setConfigParameter(id, "folder", folder);
                    Controller.setConfigParameter(id, "database", dbCheckBox.isSelected() ? "true" : "false");
                    if (deployableCheckBox.isSelected()) {
                        Controller.setConfigParameter(id, "url", urlTextField.getText());
                        Controller.setConfigParameter(id, "token", tokenTextField.getText());
                        Controller.setConfigParameter(id, "secret", secretTextField.getText());
                    }
                    if (!gitHubUrl.equals("")) {
                        Controller.setConfigParameter(id, "github", gitHubUrl);
                    }

                    //write changes to global config
                    Controller.addToGlobalConf("app.instancesNames", nameField.getText());
                    Controller.addToGlobalConf("app.instancesCode", id);
                    Popup.showMessage(0, "Instance created", "Instance created");
                    
                    //close and refresh windows
                    dialog.dispose();
                    Controller.restartApp();

                } else {
                    Popup.showMessage(1, "Error creating config file", "Error creating config file.");
                }
                

                break;
            }
        });


        panel.add(deployableCheckBox);    
        panel.add(addInstanceButton);

        //show panel
        dialog = jop.createDialog("Add a new instance - Enplated Syncer");
        dialog.setSize(1000, 450);
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
}
