package com.karlosoft;
import java.io.IOException;
import java.net.MalformedURLException;

import com.karlosoft.gui.*;

public class App 
{
    public static void main(String[] args) {
        try {
            Controller.generateDefault();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        String[] instancesName = Controller.getConfigParameter("appGlobal", "app.instancesNames").split(",");
        String[] instancesCodes = Controller.getConfigParameter("appGlobal", "app.instancesCode").split(",");

        InstanceSelect.run(instancesName, instancesCodes);
    }
}