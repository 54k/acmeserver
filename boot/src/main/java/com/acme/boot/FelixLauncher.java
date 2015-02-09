package com.acme.boot;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.util.FelixConstants;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.launch.Framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FelixLauncher {

    private static final String[] SYSTEM_BUNDLE_ACTIVATORS = {
            "com.acme.server.Activator",
            "org.apache.felix.gogo.shell.Activator",
            "org.apache.felix.gogo.runtime.activator.Activator",
            "org.apache.felix.gogo.command.Activator"
    };

    public static void main(String[] args) throws Exception {
        Map<String, Object> configMap = new HashMap<>();
        copySystemProperties(configMap);
        registerSystemBundleActivators(configMap);

        Framework framework = new Felix(configMap);
        framework.init();
        addShutdownHook(framework);
        waitForStop(framework);
    }

    public static void copySystemProperties(Map<String, Object> configMap) {
        for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
            String propName = (String) entry.getKey();
            if (propName.startsWith("org.osgi")) {
                configMap.put(propName, entry.getValue());
            }
        }
    }

    public static void registerSystemBundleActivators(Map<String, Object> configMap) throws Exception {
        ArrayList<BundleActivator> activators = new ArrayList<>();
        for (String activatorName : SYSTEM_BUNDLE_ACTIVATORS) {
            @SuppressWarnings("unchecked")
            Class<? extends BundleActivator> clazz = (Class<? extends BundleActivator>) Class.forName(activatorName);
            BundleActivator bundleActivator = clazz.newInstance();
            activators.add(bundleActivator);
        }
        configMap.put(FelixConstants.SYSTEMBUNDLE_ACTIVATORS_PROP, activators);
    }

    public static void addShutdownHook(Framework framework) {
        Runtime.getRuntime().addShutdownHook(new Thread("BrowserQuest Shutdown Hook") {
            @Override
            public void run() {
                try {
                    framework.stop();
                    framework.waitForStop(0);
                } catch (Throwable t) {
                    System.err.println("Error stopping framework: " + t);
                }
            }
        });
    }

    private static void waitForStop(Framework framework) throws BundleException, InterruptedException {
        FrameworkEvent event;
        do {
            framework.start();
            event = framework.waitForStop(0);
        } while (event.getType() == FrameworkEvent.STOPPED_UPDATE);
        System.exit(0);
    }
}
