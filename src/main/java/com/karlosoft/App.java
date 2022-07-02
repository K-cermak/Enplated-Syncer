package com.karlosoft;
import com.karlosoft.gui.*;

public class App 
{
    public static void main(String[] args) {
        String[] instancesName = Controller.getConfigParameter("appGlobal", "app.instancesNames").split(",");
        String[] instancesCodes = Controller.getConfigParameter("appGlobal", "app.instancesCode").split(",");

        InstanceSelect.run(instancesName, instancesCodes);
    }
}