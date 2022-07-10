package com.karlosoft;

import com.karlosoft.gui.*;

import java.util.Arrays;
import java.util.Properties;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Controller {
    //FILE MODIFIERS
    public static void generateDefault() throws MalformedURLException, IOException {
        //check if folder config exists
        String path = Controller.getWorkingDirectory() + "/config";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }

        //check if folder github exists
        path = Controller.getWorkingDirectory() + "/github";
        file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }

        //generate appGlobal.conf
        path = Controller.getWorkingDirectory() + "/config/appGlobal.conf";
        file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //write default config
            try {
                Writer writer = Files.newBufferedWriter(Paths.get(path), Charset.forName("UTF-8"));
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                bufferedWriter.write("app.instancesNames=");
                bufferedWriter.newLine();
                bufferedWriter.write("app.instancesCode=");
                bufferedWriter.newLine();
                bufferedWriter.write("app.githubEmail=");
                bufferedWriter.newLine();
                bufferedWriter.write("app.githubPassword=");
                bufferedWriter.newLine();
                bufferedWriter.write("app.dbPath=");
                bufferedWriter.newLine();
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //check if folder images exists
        path = Controller.getWorkingDirectory() + "/images";
        file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }

        //check if file exists
        path = Controller.getWorkingDirectory() + "/images/png-favicon.png";
        file = new File(path);
        if (!file.exists()) {
            //copy file
            try(InputStream in = new URL("https://mirror.k-cermak.com/data/enplated-syncer/png-favicon.png").openStream()){
                Files.copy(in, Paths.get(Controller.getWorkingDirectory() + "/images/png-favicon.png"));
            }
        }
    }

    public static String getConfigParameter(String file, String parameter) {
        Properties prop = new Properties();
        String fileName = getWorkingDirectory() + "/config/" + file + ".conf";

        //use utf-8
        Charset charset = StandardCharsets.UTF_8;
        Path path = Paths.get(fileName);
        try {
            prop.load(Files.newBufferedReader(path, charset));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop.getProperty(parameter);
    }

    public static void setConfigParameter(String file, String parameter, String newValue) {
        //replace \ with \\
        newValue = newValue.replace("\\", "\\\\\\\\");
        String fileName = getWorkingDirectory() + "/config/" + file + ".conf";
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

    public static boolean createDefaultConfFile(String name) {
        String fileName = getWorkingDirectory() + "/config/" + name + ".conf";

        File file = new File(fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Writer writer = Files.newBufferedWriter(Paths.get(fileName), Charset.forName("UTF-8"));
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write("folder=folder");
            bufferedWriter.newLine();
            bufferedWriter.write("database=false");
            bufferedWriter.newLine();
            bufferedWriter.write("github=false");
            bufferedWriter.newLine();
            bufferedWriter.write("url=false");
            bufferedWriter.newLine();
            bufferedWriter.write("token=false");
            bufferedWriter.newLine();
            bufferedWriter.write("secret=false");
            bufferedWriter.newLine();
            bufferedWriter.close();   
            return true;

        } catch (IOException ex) {
            return false;
        }
    }

    public static void changeId(String oldId, String newId) {
        //get all ids to array
        String[] ids = Controller.getConfigParameter("appGlobal", "app.instancesCode").split(",");
        //get index of id
        int index = Arrays.asList(ids).indexOf(oldId);
        //set new id
        ids[index] = newId;
        //set new id
        setConfigParameter("appGlobal", "app.instancesCode", String.join(",", ids));

        //change conf file
        File confFile = new File(getWorkingDirectory() + "/config/" + oldId + ".conf");
        confFile.renameTo(new File(getWorkingDirectory() + "/config/" + newId + ".conf"));
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

    public static void changeFolder(String id, String folder) {
        setConfigParameter(id, "folder", folder);
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
        File confFile = new File(getWorkingDirectory() + "/config/" + id + ".conf");
        confFile.delete();
    }


    //FUNCTIONAL
    public static String getCurrentVersion() {
        return "1.0.0";
    }

    public static String getWorkingDirectory() {
        return System.getProperty("user.dir");
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

    public static int numberOfFiles(String instanceId) {
        String instanceFolder = getConfigParameter(instanceId, "folder");
        return ZipUtils.numberOfFiles(instanceFolder);
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

    public static String selectFolder() {
        return Popup.selectFolder();
    }


    //APP CLOSE AND RESTART
    public static void closeApp() {
        System.exit(0);
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

    
    //CHECKERS
    public static boolean folderExists(String path) {
        if (new java.io.File(path).exists()) {
            return true;
        }
        return false;
    }

    public static boolean checkIdExist(String id) {
        if (new java.io.File(getWorkingDirectory() + "/config/" + id + ".conf").exists()) {
            return true;
        }
        return false;
    }


    //DATABASE 
    public static void shutdownDatabase(String instanceID) {
        //get db property
        String db = getConfigParameter("appGlobal", "app.dbPath");
        
        if (!db.equals("") && getConfigParameter(instanceID, "database").equals("true")) {
            try {
                runBatCommand("mysql_stop.bat", db);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void startDatabase(String instanceID) {
        //get db property
        String db = getConfigParameter("appGlobal", "app.dbPath");
        
        if (!db.equals("") && getConfigParameter(instanceID, "database").equals("true")) {
            try {
                runBatCommand("mysql_start.bat", db);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void runBatCommand(String cmd, String folder) throws IOException {
        Runtime.getRuntime().exec(
            new String[] {"cmd", "/C", cmd},
             null,
             new File(folder)
        );
    }


    //GITHUB
    public static void uploadGithub(String instanceId) {
        String instanceFolder = getConfigParameter(instanceId, "folder");
        if (ZipUtils.numberOfFiles(instanceFolder) < 1) {
            Popup.showMessage(1, "Error", "Folder is empty. Please add files to folder before uploading.");
            return;
        }

        shutdownDatabase(instanceId);
        RefreshableWindow.setTotalFiles(4);
        RefreshableWindow.setText("Uploading to GitHub, please wait...");

        new Thread(() -> {
            //create folder
            String folder = GithubSyncer.createFolder(instanceId);

            //add zip to it
            RefreshableWindow.addFile();
            ZipUtils appZip = new ZipUtils();
            ZipUtils.setSourceFolder(instanceFolder);
            appZip.generateFileList(new File(instanceFolder));
            appZip.zipIt(folder + "\\" + instanceId + ".zip", false);

            //initialize git in folder
            RefreshableWindow.addFile();
            GithubSyncer.initializeGit(folder);
            GithubSyncer.setDetails(folder, instanceId);

            //upload zip
            RefreshableWindow.addFile();
            GithubSyncer.push(folder);

            //delete folder
            RefreshableWindow.addFile();
            GithubSyncer.deleteFolder(folder);

            //sleep for 500 ms - this is due to small zip that uploads faster than window is generated
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            RefreshableWindow.closeWindow();
        }).start();

        RefreshableWindow.run();
        RefreshableWindow.resetData();
        startDatabase(instanceId);

        Popup.showMessage(0, "Upload completed", "Instance successfully uploaded to GitHub");
    }

    public static void downloadGithub(String instanceId) {
        shutdownDatabase(instanceId);
        String folder = GithubSyncer.createFolder(instanceId);
        String instanceFolder = getConfigParameter(instanceId, "folder");

        RefreshableWindow.setTotalFiles(3);
        RefreshableWindow.setText("Loading data, please wait...");

        new Thread(() -> {
            //init git
            GithubSyncer.initializeGit(folder);
            RefreshableWindow.addFile();
            GithubSyncer.setDetails(folder, instanceId);
            RefreshableWindow.addFile();

            //download zip
            GithubSyncer.pull(folder, instanceId);
            RefreshableWindow.addFile();

            //sleep for 500 ms - this is due to small zip that downloads faster than window is generated
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            RefreshableWindow.closeWindow();
        }).start();

        RefreshableWindow.run();
        RefreshableWindow.resetData();

        //check if file exist
        File file = new File(folder + "\\" + instanceId + ".zip");
        if (file.exists()) {
            RefreshableWindow.setTotalFiles(ZipUtils.numberOfFilesZip(folder + "\\" + instanceId + ".zip"));
            RefreshableWindow.setText("Importing new files, please wait...");

            new Thread(() -> {
                //unzip
                ZipUtils.deleteFolderData(instanceFolder);
                ZipUtils.unzip(folder + "\\" + instanceId + ".zip", instanceFolder, true);

                //delete folder
                GithubSyncer.deleteFolder(folder);   

                //sleep for 500 ms - this is due to small zip that downloads faster than window is generated
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                RefreshableWindow.closeWindow();
            }).start();

            RefreshableWindow.run();
            RefreshableWindow.resetData();
            Popup.showMessage(0, "Import complete", "Instance successfully imported from GitHub");

        } else {
            //delete folder
            GithubSyncer.deleteFolder(folder);
            Popup.showMessage(1, "Error", "Instance not found on GitHub. Is instance ID correct?");
        }
        startDatabase(instanceId);
    }


    //IMPORT / EXPORT
    public static void exportInstance(String id, String location) {   
        //get instance location
        String instanceFolder = getConfigParameter(id, "folder");
        if (ZipUtils.numberOfFiles(instanceFolder) < 1) {
            Popup.showMessage(1, "Error", "Folder is empty. Please add files to folder before exporting.");
            return;
        }

        shutdownDatabase(id);

        //pack to zip with timestamp
        String zipName = "INSTANCE-EXPORT-"+ id + "-" + System.currentTimeMillis() + ".zip";


        RefreshableWindow.setTotalFiles(Controller.numberOfFiles(id));
        RefreshableWindow.setText("Exporting instance, please wait...");
       

        new Thread(() -> {
            ZipUtils appZip = new ZipUtils();
            ZipUtils.setSourceFolder(instanceFolder);
            appZip.generateFileList(new File(instanceFolder));
            appZip.zipIt(location + "\\" + zipName, true);

            //sleep for 500 ms - this is due to small zip that exports faster than window is generated
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            RefreshableWindow.closeWindow();
        }).start();

        RefreshableWindow.run();
        RefreshableWindow.resetData();
        startDatabase(id);

        Popup.showMessage(0, "Export complete", "Instance exported to " + location + "\\" + zipName);
    }

    public static void importInstance(String zipFile, String instanceId) {
        shutdownDatabase(instanceId);
        String folder = getConfigParameter(instanceId, "folder");

        //import new
        RefreshableWindow.setTotalFiles(ZipUtils.numberOfFilesZip(zipFile));
        RefreshableWindow.setText("Importing new files, please wait...");

        new Thread(() -> {
            ZipUtils.deleteFolderData(folder);
            ZipUtils.unzip(zipFile, folder, true);

            //sleep for 500 ms - this is due to small zip that imports faster than window is generated
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            RefreshableWindow.closeWindow();
        }).start();

        RefreshableWindow.run();
        RefreshableWindow.resetData();
        startDatabase(instanceId);

        Popup.showMessage(0, "Import complete", "Instance imported from " + zipFile);
    }


    //GUI
    public static void globalSettingsPanel() {
        GlobalSettingsPanel.run();
    }

    public static void addInstance() {
        AddInstance.run();
    }

    public static void runInstance(String id, String name) {
        Instance.run(id, name);
    } 

    public static void closeInstanceSelect() {
        InstanceSelect.close();
    }

    public static void deploy(String instanceId) {
        shutdownDatabase(instanceId);
        String instanceFolder = getConfigParameter(instanceId, "folder");

        if (ZipUtils.numberOfFiles(instanceFolder) < 1) {
            Popup.showMessage(1, "Error", "Folder is empty. Please add files to folder before deploying.");
            return;
        }

        RefreshableWindow.setTotalFiles(3);
        RefreshableWindow.setText("Deploying, please wait...");

        new Thread(() -> {
            String folder = GithubSyncer.createFolder(instanceId);
            String url = getConfigParameter(instanceId, "url");
            String token = getConfigParameter(instanceId, "token");
            String secret = getConfigParameter(instanceId, "secret");

            //zip instance to folder
            RefreshableWindow.addFile();
            ZipUtils appZip = new ZipUtils();
            ZipUtils.setSourceFolder(instanceFolder);
            appZip.generateFileList(new File(instanceFolder));
            appZip.zipIt(folder + "\\" + instanceId + ".zip", false);

            RefreshableWindow.addFile();
            try {
                Requests.sendZip(url, token, secret, folder + "\\" + instanceId + ".zip");
            } catch (IOException e) {
                e.printStackTrace();
            }

            //delete folder
            RefreshableWindow.addFile();
            GithubSyncer.deleteFolder(folder);

            //sleep for 500 ms - this is due to small zip that deploys faster than window is generated
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            RefreshableWindow.closeWindow();
        }).start();

        RefreshableWindow.run();
        RefreshableWindow.resetData();

        startDatabase(instanceId);
    }

    public static void refreshWindow() {
        RefreshableWindow.addFile();
    }
}