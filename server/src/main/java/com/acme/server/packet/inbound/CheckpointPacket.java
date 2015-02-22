package com.acme.server.packet.inbound;

import com.acme.engine.aegis.core.ComponentMapper;
import com.acme.engine.aegis.core.Wired;
import com.acme.engine.network.InboundPacket;
import com.acme.server.component.PlayerComponent;
import com.acme.server.manager.WorldManager;
import com.acme.server.world.Area;

@Wired
public class CheckpointPacket extends InboundPacket {

    private int checkpointId;

    private ComponentMapper<PlayerComponent> pcm;
    private WorldManager worldManager;

    @Override
    public void read() {
        checkpointId = readInt();
    }

    @Override
    public void run() {
        Area spawnArea = worldManager.getWorld().getPlayerSpawnAreas().get(checkpointId);
        if (spawnArea != null) {
            pcm.get(getClient()).setSpawnArea(spawnArea);
        }
    }
}
