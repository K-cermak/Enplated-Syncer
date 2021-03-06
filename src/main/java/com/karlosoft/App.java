package com.karlosoft;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.UIManager;

import com.karlosoft.gui.*;

public class App 
{
    public static void main(String[] args) {
        //set better design
        try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            Popup.showMessage(2, "An error has occurred", "An error has occurred: " + e.getMessage());
        }

        try {
            Controller.generateDefault();
        } catch (MalformedURLException e) {
            Popup.showMessage(2, "An error has occurred", "An error has occurred: " + e.getMessage());
        } catch (IOException e) {
            Popup.showMessage(2, "An error has occurred", "An error has occurred: " + e.getMessage());
        }
        
        String[] instancesName = Controller.getConfigParameter("appGlobal", "app.instancesNames").split(",");
        String[] instancesCodes = Controller.getConfigParameter("appGlobal", "app.instancesCode").split(",");

        InstanceSelect.run(instancesName, instancesCodes);
    }
}