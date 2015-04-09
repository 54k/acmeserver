package com.acme.server.console;

import com.acme.commons.network.SessionComponent;
import com.acme.ecs.core.Engine;
import com.acme.server.BrowserQuest;
import com.acme.server.model.system.passive.WorldSystem;
import org.apache.felix.service.command.Descriptor;

public class PlayerCommands {

    private final BrowserQuest browserQuest;

    public PlayerCommands(BrowserQuest browserQuest) {
        this.browserQuest = browserQuest;
    }

    @Descriptor("show online players")
    public void online() {
        Engine engine = getEngine();
        System.out.println(engine.getSystem(WorldSystem.class).getPlayers());
    }

    @Descriptor("kick player")
    public void kick(@Descriptor("player id") long id) {
        getEngine().getSystem(WorldSystem.class).getPlayerById(id)
                .getComponent(SessionComponent.class)
                .getSession().close();
    }

    private Engine getEngine() {
        return browserQuest.getEngine();
    }
}
