package com.karlosoft;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import com.karlosoft.gui.*;

public class Controller {
    public static String getConfigParameter(String file, String parameter) {
        Properties prop = new Properties();
        String fileName = "./src/main/java/com/karlosoft/config/" + file + ".conf";
        try (FileInputStream fis = new FileInputStream(fileName)) {
            prop.load(fis);
        } catch (FileNotFoundException ex) {
            Popup.showMessage(1, "Error loading config file", "Config file not found. Please reinstall your app.");
            System.exit(1);
        } catch (IOException ex) {
            Popup.showMessage(1, "Error loading config file", "Config file not found. Please reinstall your app.");
            System.exit(1);
        }
        return prop.getProperty(parameter);
    }

    public static void setConfigParameter(String file, String parameter, String newValue) {
        Properties prop = new Properties();
        String fileName = "./src/main/java/com/karlosoft/config/" + file + ".conf";
        try (FileInputStream fis = new FileInputStream(fileName)) {
            prop.load(fis);
        } catch (FileNotFoundException ex) {
            Popup.showMessage(1, "Error loading config file", "Config file not found. Please reinstall your app.");
            System.exit(1);
        } catch (IOException ex) {
            Popup.showMessage(1, "Error loading config file", "Config file not found. Please reinstall your app.");
            System.exit(1);
        }
        prop.setProperty(parameter, newValue);
        try (Writer writer = new java.io.FileWriter(fileName)) {
            prop.store(writer, null);
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
        try (Writer writer = new java.io.FileWriter(fileName)) {
            writer.write("name=name\n");
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

    public static void refreshMainWindow() {
        InstanceSelect.close();
        InstanceSelect.run(Controller.getConfigParameter("appGlobal", "app.instancesNames").split(","), Controller.getConfigParameter("appGlobal", "app.instancesCode").split(","));
    }

    public static void runInstance(String id, String name) {
        Instance.run(id, name);
    } 

    public static void closeInstanceSelect() {
        InstanceSelect.close();
    }

}