package com.acme.server;

import com.acme.engine.application.Context;
import com.acme.engine.application.ContextBuilder;
import com.acme.server.console.PlayerCommands;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.Hashtable;

public class Activator implements BundleActivator {

    private Context application;

    @Override
    public void start(BundleContext context) throws Exception {
        BrowserQuest browserQuest = new BrowserQuest();
        application = new ContextBuilder(browserQuest)
                .setApplicationName("BrowserQuest Server")
                .setUpdateInterval(1000 / 60)
                .build();
        application.waitForStart(0);

        Hashtable<String, Object> props = new Hashtable<>();
        props.put("osgi.command.scope", "server.players");
        props.put("osgi.command.function", new String[]{"online", "kick"});
        context.registerService(PlayerCommands.class.getName(), new PlayerCommands(browserQuest), props);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        application.dispose();
        application.waitForDispose(0);
    }
}
