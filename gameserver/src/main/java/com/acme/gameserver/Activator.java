package com.acme.gameserver;

import com.acme.core.application.Context;
import com.acme.core.application.ContextBuilder;
import com.acme.gameserver.console.PlayerCommands;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.Hashtable;

public class Activator implements BundleActivator {

    private Context application;

    @Override
    public void start(BundleContext context) throws Exception {
        BrowserQuest browserQuest = new BrowserQuest();
        application = new ContextBuilder(browserQuest)
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
