package com.acme.server.console;

import com.acme.engine.ecs.core.Engine;
import com.acme.engine.mechanics.network.SessionComponent;
import com.acme.server.BrowserQuest;
import com.acme.server.managers.WorldManager;
import org.apache.felix.service.command.Descriptor;

public class PlayerCommands {

    private final BrowserQuest browserQuest;

    public PlayerCommands(BrowserQuest browserQuest) {
        this.browserQuest = browserQuest;
    }

    @Descriptor("show online players")
    public void online() {
        Engine engine = getEngine();
        System.out.println(engine.getSystem(WorldManager.class).getPlayers());
    }

    @Descriptor("kick player")
    public void kick(@Descriptor("player id") long id) {
        getEngine().getSystem(WorldManager.class).getPlayerById(id)
                .getComponent(SessionComponent.class)
                .getSession().close();
    }

    private Engine getEngine() {
        return browserQuest.getEngine();
    }
}
