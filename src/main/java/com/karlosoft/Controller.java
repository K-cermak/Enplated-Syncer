package com.karlosoft;

import com.karlosoft.gui.*;

import java.util.Arrays;
import java.util.Properties;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Controller {
    public static String getConfigParameter(String file, String parameter) {
        Properties prop = new Properties();
        String fileName = "./src/main/java/com/karlosoft/config/" + file + ".conf";

        //use utf-8
        Charset charset = StandardCharsets.UTF_8;
        Path path = Paths.get(fileName);
        try {
            prop.load(Files.newBufferedReader(path, charset));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop.getProperty(parameter);
        
        
        //return prop.getProperty(parameter);
    }

    public static void setConfigParameter(String file, String parameter, String newValue) {
        //replace \ with \\
        newValue = newValue.replace("\\", "\\\\\\\\");
        String fileName = "./src/main/java/com/karlosoft/config/" + file + ".conf";
        String line = null;
        try {
            line = new String(Files.readAllBytes(Paths.get(fileName)), "UTF-8");
        } catch (IOException ex) {
            Popup.showMessage(1, "Error loading config file", "Config file not found. Please reinstall your app.");
            System.exit(1);
        }

        //replace value
        String newLine = line.replaceAll(parameter + "=.*", parameter + "=" + newValue);
        Path logFile = Paths.get(fileName);
        //write new line
        try {
            BufferedWriter writer = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8);
            writer.write(newLine);
            writer.close();
        } catch (IOException ex) {
            Popup.showMessage(1, "Error loading config file", "Config file not found. Please reinstall your app.");
            System.exit(1);
        }
    }

    public static void addToGlobalConf(String property, String newValue) {
        //get old parameter
        String oldValue = getConfigParameter("appGlobal", property);
        //add new value to old parameter
        String newParameter = oldValue + "," + newValue;
        //set new parameter
        setConfigParameter("appGlobal", property, newParameter);
    }

    public static void globalSettingsPanel() {
        GlobalSettingsPanel.run();
    }

    public static void addInstance() {
        AddInstance.run();
    }

    public static boolean folderExists(String path) {
        if (new java.io.File(path).exists()) {
            return true;
        }
        return false;
    }

    public static boolean checkIdExist(String id) {
        if (new java.io.File("./src/main/java/com/karlosoft/config/" + id + ".conf").exists()) {
            return true;
        }
        return false;
    }

    public static String generateRandomId() {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        if (checkIdExist(sb.toString())) {
            return generateRandomId();
        } else {
            return sb.toString();
        }
    }

    public static boolean createDefaultConfFile(String name) {
        String fileName = "./src/main/java/com/karlosoft/config/" + name + ".conf";
        try (Writer writer = new java.io.FileWriter(fileName, Charset.forName("utf-8"))) {
            writer.write("folder=folder\n");
            writer.write("database=false\n");
            writer.write("github=false\n");
            writer.write("url=false\n");
            writer.write("token=false\n");
            writer.write("secret=false\n");
            writer.close();     
            return true;

        } catch (IOException ex) {
            return false;
        }
    }

    public static void runInstance(String id, String name) {
        Instance.run(id, name);
    } 

    public static void closeInstanceSelect() {
        InstanceSelect.close();
    }

    public static String selectFolder() {
        return Popup.selectFolder();
    }

    public static void exportInstance(String id, String location) {   
        //get instance location
        String instanceFolder = getConfigParameter(id, "folder");
        //pack to zip with timestamp
        String zipName = "INSTANCE-EXPORT-"+ id + "-" + System.currentTimeMillis() + ".zip";


        RefreshableWindow.setTotalFiles(Controller.numberOfFiles(id));
        RefreshableWindow.setText("Exporting instance, please wait...");
       

        new Thread(() -> {
            ZipUtils appZip = new ZipUtils();
            ZipUtils.setSourceFolder(instanceFolder);
            appZip.generateFileList(new File(instanceFolder));
            appZip.zipIt(location + "\\" + zipName);
            RefreshableWindow.closeWindow();
        }).start();

        RefreshableWindow.run();
        RefreshableWindow.resetData();

        Popup.showMessage(0, "Export complete", "Instance exported to " + location + "\\" + zipName);
    }

    public static void refreshWindow() {
        RefreshableWindow.addFile();
    }

    public static int numberOfFiles(String instanceId) {
        String instanceFolder = getConfigParameter(instanceId, "folder");
        return ZipUtils.numberOfFiles(instanceFolder);
    }

    public static void importInstance(String zipFile, String instanceId) {
        String folder = getConfigParameter(instanceId, "folder");

        //import new
        RefreshableWindow.setTotalFiles(ZipUtils.numberOfFilesZip(zipFile));
        RefreshableWindow.setText("Importing new files, please wait...");

        new Thread(() -> {
            ZipUtils.deleteFolderData(folder);
            ZipUtils.unzip(zipFile, folder);

            RefreshableWindow.closeWindow();
        }).start();

        RefreshableWindow.run();
        RefreshableWindow.resetData();

        Popup.showMessage(0, "Import complete", "Instance imported from " + zipFile);
    }

    public static void closeApp() {
        System.exit(0);
    }

    public static void changeFolder(String id, String folder) {
        setConfigParameter(id, "folder", folder);
    }

    public static String getNameFromId(String id) {
        //get all ids to array
        String[] ids = Controller.getConfigParameter("appGlobal", "app.instancesCode").split(",");
        //get all names to array
        String[] names = Controller.getConfigParameter("appGlobal", "app.instancesNames").split(",");
        //get index of id
        int index = Arrays.asList(ids).indexOf(id);
        //return name
        return names[index];
    }

    public static void changeName(String id, String newName) {
        //get all ids to array
        String[] ids = Controller.getConfigParameter("appGlobal", "app.instancesCode").split(",");
        //get all names to array
        String[] names = Controller.getConfigParameter("appGlobal", "app.instancesNames").split(",");
        //get index of id
        int index = Arrays.asList(ids).indexOf(id);
        //set new name
        names[index] = newName;
        //set new names
        setConfigParameter("appGlobal", "app.instancesNames", String.join(",", names));
    }

    public static void restartApp() {
        //close all windows
        try { Instance.close(); } catch (Exception e) {}
        try { InstanceSelect.close(); } catch (Exception e) {}
        try { InstanceSettings.close(); } catch (Exception e) {}
        try { GlobalSettingsPanel.close(); } catch (Exception e) {}

        //restart app
        App.main(null);
    }

    public static void deleteInstance(String id) {
        //get all ids to array
        String[] ids = Controller.getConfigParameter("appGlobal", "app.instancesCode").split(",");
        //get all names to array
        String[] names = Controller.getConfigParameter("appGlobal", "app.instancesNames").split(",");
        //remove id
        ids = Arrays.copyOf(ids, ids.length - 1);
        //remove name
        names = Arrays.copyOf(names, names.length - 1);
        //set new ids
        setConfigParameter("appGlobal", "app.instancesCode", String.join(",", ids));
        //set new names
        setConfigParameter("appGlobal", "app.instancesNames", String.join(",", names));
        //delete conf file
        File confFile = new File("./src/main/java/com/karlosoft/config/" + id + ".conf");
        confFile.delete();
    }

    public static void uploadGithub(String instanceId) {
        //create folder
        String folder = GithubSyncer.createFolder(instanceId);
        String instanceFolder = getConfigParameter(instanceId, "folder");

        //add zip to it
        ZipUtils appZip = new ZipUtils();
        ZipUtils.setSourceFolder(instanceFolder);
        appZip.generateFileList(new File(instanceFolder));
        appZip.zipIt(folder + "\\" + instanceId + ".zip");

        //initialize git in folder
        GithubSyncer.initializeGit(folder);
        GithubSyncer.setDetails(folder, instanceId);

        //upload zip
        GithubSyncer.push(folder);

        //delete folder
        System.out.println("done");
        
    }
}