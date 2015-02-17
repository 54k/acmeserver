package com.acme.server;

import com.acme.server.console.PlayerCommands;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.Hashtable;

public class Activator implements BundleActivator {

    private BrowserQuest browserQuest;

    @Override
    public void start(BundleContext context) throws Exception {
        browserQuest = new BrowserQuest();
        browserQuest.start();

        Hashtable<String, Object> props = new Hashtable<>();
        props.put("osgi.command.scope", "server.players");
        props.put("osgi.command.function", new String[]{"online", "kick"});
        context.registerService(PlayerCommands.class.getName(), new PlayerCommands(browserQuest), props);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        browserQuest.stop();
    }
}
