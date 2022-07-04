package com.karlosoft.gui;

import javax.swing.*;

import com.karlosoft.Controller;

import java.awt.*;

public class InstanceSettings {

    public static void showChangesPopup() {
        Popup.showMessage(0, "Changes saved", "Changes successfully saved! App will be now restarted.");
        Controller.restartApp();
    }

    static JDialog dialog;

    public static void changeSettings(String instanceId) {
        
        dialog = new JDialog();
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 1, 30, 30));
        JScrollPane scrollPane = new JScrollPane(panel);

        //label
        JLabel label = new JLabel("Select settings that you want to change:");
        label.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        panel.add(label);


        //button change name
        JButton changeNameButton = new JButton("Change name");
        changeNameButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        changeNameButton.addActionListener(e ->
        {
            changeName(instanceId);
        });

        //change folder
        JButton changeFolderButton = new JButton("Change folder");
        changeFolderButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        changeFolderButton.addActionListener(e ->
        {
            if (Popup.showConfirm("Change folder", "Are you sure you want to change the folder? This operation will not delete the current folder, but the new folder should be named the same as the current folder.")) {
                String newFolder = Popup.selectFolder();
                if (!newFolder.equals("") && newFolder != null) {
                    Controller.changeFolder(instanceId, newFolder);
                }
            }
        });

        //change database settings
        JButton changeDatabaseSettingsButton = new JButton("Change database settings");
        changeDatabaseSettingsButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        changeDatabaseSettingsButton.addActionListener(e ->
        {
            changeDatabase(instanceId);
        });

        //change deployable settings
        JButton changeDeployableSettingsButton = new JButton("Change deployable settings");
        changeDeployableSettingsButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        changeDeployableSettingsButton.addActionListener(e ->
        {
            changeDeployable(instanceId);
        });

        //change github settings
        JButton changeGithubSettingsButton = new JButton("Change GitHub settings");
        changeGithubSettingsButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        changeGithubSettingsButton.addActionListener(e ->
        {
            changeGithub(instanceId);
        });

        //delete instance
        JButton deleteInstanceButton = new JButton("Delete instance");
        deleteInstanceButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        deleteInstanceButton.addActionListener(e ->
        {
            if (Popup.showConfirm("Delete instance", "Are you sure you want to delete this instance? This operation will delete the current instance. It will not delete any data in instance folder or data on GitHub or web.")) {
                Controller.deleteInstance(instanceId);
                Popup.showMessage(0, "Instance deleted successfully", "Instance deleted successfully!");
            }
        });

        //cancel
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        cancelButton.addActionListener(e ->
        {
            dialog.dispose();
        });

        panel.add(changeNameButton);
        panel.add(changeFolderButton);
        panel.add(changeDatabaseSettingsButton);
        panel.add(changeDeployableSettingsButton);
        panel.add(changeGithubSettingsButton);
        panel.add(deleteInstanceButton);
        panel.add(cancelButton);

        dialog.setContentPane(scrollPane);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setTitle("Close");
        dialog.setSize(300, 600);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    public static void close() {
        dialog.dispose();
    }



    public static void changeDatabase(String instanceId) {
        JDialog dialog = new JDialog();
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 30, 30));
        JScrollPane scrollPane = new JScrollPane(panel);
        
        //label
        JLabel label = new JLabel("Is this instance a database?");
        label.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        panel.add(label);

        //checkbox
        JCheckBox checkBox = new JCheckBox("Yes");
        checkBox.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        checkBox.addActionListener(e ->
        {
            //todo
        });
        if (Controller.getConfigParameter(instanceId, "database").equals("true")) {
            checkBox.setSelected(true);
        } else {
            checkBox.setSelected(false);
        }
        panel.add(checkBox);

        //save
        JButton saveButton = new JButton("Save");
        saveButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        saveButton.addActionListener(e ->
        {
            if (checkBox.isSelected()) {
                Controller.setConfigParameter(instanceId, "database", "true");
            } else {
                Controller.setConfigParameter(instanceId, "database", "false");
            }
            dialog.dispose();
            showChangesPopup();
        });
        panel.add(saveButton);

        //cancel
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        cancelButton.addActionListener(e ->
        {
            dialog.dispose();
        });
        panel.add(cancelButton);


        dialog.setContentPane(scrollPane);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setTitle("Change database settings");
        dialog.setSize(300, 400);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    public static void changeName(String instanceId) {
        JDialog dialog = new JDialog();
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 30, 30));
        JScrollPane scrollPane = new JScrollPane(panel);
        
        //label
        JLabel label = new JLabel("Instance new name");
        label.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        panel.add(label);
        
        //textfield
        JTextField textField = new JTextField();
        textField.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        textField.setText(Controller.getNameFromId(instanceId));
        panel.add(textField);
        
        //save
        JButton saveButton = new JButton("Save");
        saveButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        saveButton.addActionListener(e ->
        {
            Controller.changeName(instanceId, textField.getText());
            dialog.dispose();
            showChangesPopup();
        });
        panel.add(saveButton);
        
        //cancel
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        cancelButton.addActionListener(e ->
        {
            dialog.dispose();
        });
        panel.add(cancelButton);
        
        dialog.setContentPane(scrollPane);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setTitle("Change name");
        dialog.setSize(300, 400);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    public static void changeGithub(String instanceId) {
        JDialog dialog = new JDialog();
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 30, 30));
        JScrollPane scrollPane = new JScrollPane(panel);
        
        //label
        JLabel label = new JLabel("Sync with GitHub?");
        label.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        panel.add(label);
        
        //url
        JLabel urlLabel = new JLabel("GitHub URL");
        urlLabel.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));

        //text field
        JTextField urlTextField = new JTextField();
        urlTextField.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        String urlText = Controller.getConfigParameter(instanceId, "github");
        if (urlText.equals("false")) {
            urlTextField.setText("");
        } else {
            urlTextField.setText(urlText);
        }


        //checkbox
        JCheckBox checkBox = new JCheckBox("Yes");
        checkBox.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        checkBox.addActionListener(e ->
        {
            //change visibility url label and textfield
            if (checkBox.isSelected()) {
                urlLabel.setVisible(true);
                urlTextField.setVisible(true);
            } else {
                urlLabel.setVisible(false);
                urlTextField.setVisible(false);
            }
        });

        panel.add(checkBox);
        panel.add(urlLabel);
        panel.add(urlTextField);

        if (!Controller.getConfigParameter(instanceId, "github").equals("false")) {
            checkBox.setSelected(true);
            urlLabel.setVisible(true);
            urlTextField.setVisible(true);
        } else {
            checkBox.setSelected(false);
            urlLabel.setVisible(false);
            urlTextField.setVisible(false);
        }

        
        //save
        JButton saveButton = new JButton("Save");
        saveButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        saveButton.addActionListener(e ->
        {
            if (checkBox.isSelected()) {
                Controller.setConfigParameter(instanceId, "github", urlTextField.getText());
            } else {
                Controller.setConfigParameter(instanceId, "github", "false");
            }
            dialog.dispose();
            showChangesPopup();
        });
        panel.add(saveButton);
        
        //cancel
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        cancelButton.addActionListener(e ->
        {
            dialog.dispose();
        });
        panel.add(cancelButton);
        
        dialog.setContentPane(scrollPane);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setTitle("Change GitHub settings");
        dialog.setSize(300, 400);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    public static void changeDeployable(String instanceId) {
        //same as github, but contains url, token, secret

        JDialog dialog = new JDialog();
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(10, 1, 30, 30));
        JScrollPane scrollPane = new JScrollPane(panel);

        //deploy
        JLabel label = new JLabel("Deployable settings");
        label.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        panel.add(label);

        //url
        JLabel urlLabel = new JLabel("Deployable URL");
        urlLabel.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));

        //text field
        JTextField urlTextField = new JTextField();
        urlTextField.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        String urlText = Controller.getConfigParameter(instanceId, "url");

        if (urlText.equals("false")) {
            urlTextField.setText("");
        } else {
            urlTextField.setText(urlText);
        }

        //token
        JLabel tokenLabel = new JLabel("Deployable token");
        tokenLabel.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));

        //text field
        JTextField tokenTextField = new JTextField();
        tokenTextField.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        String tokenText = Controller.getConfigParameter(instanceId, "token");

        if (tokenText.equals("false")) {
            tokenTextField.setText("");
        } else {
            tokenTextField.setText(tokenText);
        }

        //secret
        JLabel secretLabel = new JLabel("Deployable secret");
        secretLabel.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        
        //text field
        JTextField secretTextField = new JTextField();
        secretTextField.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        String secretText = Controller.getConfigParameter(instanceId, "secret");

        if (secretText.equals("false")) {
            secretTextField.setText("");
        } else {
            secretTextField.setText(secretText);
        }

        //checkbox
        JCheckBox checkBox = new JCheckBox("Yes");
        checkBox.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        checkBox.addActionListener(e ->
        {
            //change visibility url label and textfield
            if (checkBox.isSelected()) {
                urlLabel.setVisible(true);
                urlTextField.setVisible(true);
                tokenLabel.setVisible(true);
                tokenTextField.setVisible(true);
                secretLabel.setVisible(true);
                secretTextField.setVisible(true);
            } else {
                urlLabel.setVisible(false);
                urlTextField.setVisible(false);
                tokenLabel.setVisible(false);
                tokenTextField.setVisible(false);
                secretLabel.setVisible(false);
                secretTextField.setVisible(false);
            }
        });

        panel.add(checkBox);
        panel.add(urlLabel);
        panel.add(urlTextField);
        panel.add(tokenLabel);
        panel.add(tokenTextField);
        panel.add(secretLabel);
        panel.add(secretTextField);

        if (!Controller.getConfigParameter(instanceId, "url").equals("false")) {
            checkBox.setSelected(true);
            urlLabel.setVisible(true);
            urlTextField.setVisible(true);
            tokenLabel.setVisible(true);
            tokenTextField.setVisible(true);
            secretLabel.setVisible(true);
            secretTextField.setVisible(true);
        } else {
            checkBox.setSelected(false);
            urlLabel.setVisible(false);
            urlTextField.setVisible(false);
            tokenLabel.setVisible(false);
            tokenTextField.setVisible(false);
            secretLabel.setVisible(false);
            secretTextField.setVisible(false);
        }


        //save
        JButton saveButton = new JButton("Save");
        saveButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        saveButton.addActionListener(e ->
        {
            if (checkBox.isSelected()) {
                Controller.setConfigParameter(instanceId, "url", urlTextField.getText());
                Controller.setConfigParameter(instanceId, "token", tokenTextField.getText());
                Controller.setConfigParameter(instanceId, "secret", secretTextField.getText());
            } else {
                Controller.setConfigParameter(instanceId, "url", "false");
                Controller.setConfigParameter(instanceId, "token", "false");
                Controller.setConfigParameter(instanceId, "secret", "false");
            }
            dialog.dispose();
            showChangesPopup();
        });

        panel.add(saveButton);

        //cancel
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font(FontLocalizator.returnFont(), Font.PLAIN, 15));
        cancelButton.addActionListener(e ->
        {
            dialog.dispose();
        });

        panel.add(cancelButton);

        dialog.setContentPane(scrollPane);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setTitle("Change deployable settings");
        dialog.setSize(300, 550);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
        dialog.setVisible(true);
    }
}