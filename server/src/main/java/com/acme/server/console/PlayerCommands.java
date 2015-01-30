package com.acme.server.console;

import com.acme.commons.ashley.WiringEngine;
import com.acme.commons.network.SessionComponent;
import com.acme.server.BrowserQuest;
import com.acme.server.manager.WorldManager;
import org.apache.felix.service.command.Descriptor;

public class PlayerCommands {

    private final BrowserQuest browserQuest;

    public PlayerCommands(BrowserQuest browserQuest) {
        this.browserQuest = browserQuest;
    }

    @Descriptor("show online")
    public void online() {
        WiringEngine engine = getEngine();
        System.out.println(engine.getSystem(WorldManager.class).getPlayers());
    }

    @Descriptor("kick player")
    public void kick(@Descriptor("player id") long id) {
        getEngine().getSystem(WorldManager.class).findPlayerById(id)
                .getComponent(SessionComponent.class)
                .getSession().close();
    }

    private WiringEngine getEngine() {
        return browserQuest.getContext().get(WiringEngine.class);
    }
}
