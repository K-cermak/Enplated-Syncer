package com.karlosoft;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class GithubSyncer {
    public static String createFolder(String InstanceId) {
        String path = "./src/main/java/com/karlosoft/github/" + InstanceId;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        return path;
    }

    public static void initializeGit(String folder) {
        String path = folder + "/.git";
        File file = new File(path);
        if (!file.exists()) {
            try {
                Runtime.getRuntime().exec("git init " + folder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void setDetails(String folder, String instanceId) {
        String email = Controller.getConfigParameter("appGlobal", "app.githubEmail");
        String password = Controller.getConfigParameter("appGlobal", "app.githubPassword");
        String remote = Controller.getConfigParameter(instanceId, "github");
        String name = "EnplatedSyncer";

        //set email
        try {
            execCmd("git config --global user.email " + email, folder);
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        //set password
        try {
            execCmd("git config --global user.password " + password, folder);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //set name
        try {
            execCmd("git config --global user.name " + name, folder);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //set remote
        try {
            execCmd("git remote add origin " + remote, folder);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void push(String folder) {
        try {
            execCmd("git add .", folder);
            execCmd("git commit -m \"EnplatedSyncer\"", folder);
            execCmd("git branch -M main", folder);
            execCmd("git push origin main", folder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String execCmd(String cmd, String runInFolder) {
        String result = null;
        try {
            Process process = Runtime.getRuntime().exec(cmd, null, new File(runInFolder));
            InputStream is = process.getInputStream();
            try (Scanner s = new Scanner(is).useDelimiter("\\A")) {
                result = s.hasNext() ? s.next() : "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
