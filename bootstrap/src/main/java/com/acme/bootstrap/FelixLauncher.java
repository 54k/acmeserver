package com.acme.bootstrap;

//import com.acme.gameserver.Activator;
//import org.apache.felix.framework.Felix;
//import org.apache.felix.framework.util.FelixConstants;
//import org.osgi.framework.BundleActivator;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;

public class FelixLauncher {

    public static void main(String[] args) throws Exception {
        //        Map<String, Object> configMap = new HashMap<>();
        //
        //        ArrayList<BundleActivator> activators = new ArrayList<>();
        ////
        ////        Activator appActivator = new Activator();
        ////        activators.add(appActivator);
        //
        //        org.apache.felix.gogo.shell.Activator gogoActivator = new org.apache.felix.gogo.shell.Activator();
        //        activators.add(gogoActivator);
        //
        //        org.apache.felix.gogo.runtime.activator.Activator runtimeActivator = new org.apache.felix.gogo.runtime.activator.Activator();
        //        activators.add(runtimeActivator);
        //
        //        org.apache.felix.gogo.command.Activator commandActivator = new org.apache.felix.gogo.command.Activator();
        //        activators.add(commandActivator);
        //
        //        configMap.put(FelixConstants.SYSTEMBUNDLE_ACTIVATORS_PROP, activators);
        //
        //        Felix felix = new Felix(configMap);
        //        felix.start();
        //        felix.waitForStop(0);
        org.apache.felix.main.Main.main(args);
    }
}
