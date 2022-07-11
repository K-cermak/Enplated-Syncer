package com.karlosoft;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.karlosoft.gui.Popup;

public class ZipUtils {

    private List <String> fileList;
    private static String SOURCE_FOLDER = ""; // SourceFolder path

    public ZipUtils() {
        fileList = new ArrayList < String > ();
    }

    public static void setSourceFolder(String sourceFolder) {
        SOURCE_FOLDER = sourceFolder;
    }

    public void zipIt(String zipFile, boolean notify) {
        byte[] buffer = new byte[1024];
        String source = new File(SOURCE_FOLDER).getName();
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(fos);

            FileInputStream in = null;

            for (String file: this.fileList) {
                //replace \ with /
                String fileName = file.replace("\\", "/");


                if (notify == true) {
                    Controller.refreshWindow();
                }

                ZipEntry ze = new ZipEntry(source + "/" + fileName);
                zos.putNextEntry(ze);
                try {
                    in = new FileInputStream(SOURCE_FOLDER + "/" + file);
                    int len;
                    while ((len = in .read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                } finally {
                    in.close();
                }
            }

            zos.closeEntry();

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                zos.close();
            } catch (IOException e) {
                Popup.showMessage(2, "An error has occurred", "An error has occurred: " + e.getMessage());
            }
        }
    }

    public void generateFileList(File node) {
        // add file only
        if (node.isFile()) {
            fileList.add(generateZipEntry(node.toString()));
        }

        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename: subNote) {
                generateFileList(new File(node, filename));
            }
        }
    }

    private String generateZipEntry(String file) {
        return file.substring(SOURCE_FOLDER.length() + 1, file.length());
    }

    public static int numberOfFiles(String folder) {
        int count = 0;
        File f = new File(folder);
        File[] files = f.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                count++;
            } else if (file.isDirectory()) {
                count = count + numberOfFiles(file.getAbsolutePath());
            }
        }
        return count;
    }

    public static void unzip(String zipFile, String unzipTo, boolean notify) {
        unzipTo = oneFolderUp(unzipTo);
        try {
            File f = new File(unzipTo);
            if (!f.isDirectory()) {
                f.mkdir();
            }
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                if (notify == true) {
                    Controller.refreshWindow();
                }
                String fileName = ze.getName();
                File newFile = new File(unzipTo + "/" + fileName);
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String oneFolderUp(String folder) {
        String[] parts = folder.split("\\\\");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            sb.append(parts[i]);
            sb.append("\\");
        }
        return sb.toString();
    }

    public static void deleteFolderData(String folder) {
        //delete all files including subfolders
        File f = new File(folder);
        File[] files = f.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                deleteFolderData(file.getAbsolutePath());
                file.delete();
            }
        }
    }

    public static int numberOfFilesZip(String zipFile) {
        int count = 0;
        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                count++;
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return count;
    }
}