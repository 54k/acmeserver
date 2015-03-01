package com.acme.server.packets.inbound;

import com.acme.engine.ecs.core.ComponentMapper;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.network.InboundPacket;
import com.acme.server.managers.PlayerComponent;
import com.acme.server.managers.WorldManager;
import com.acme.server.world.Area;

@Wire
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
