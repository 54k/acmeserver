package com.acme.server.packets.inbound;

import com.acme.commons.network.InboundPacket;
import com.acme.ecs.core.ComponentMapper;
import com.acme.ecs.core.Wire;
import com.acme.server.managers.PlayerComponent;
import com.acme.server.model.system.passive.WorldSystem;
import com.acme.server.world.Area;

@Wire
public class CheckpointPacket extends InboundPacket {

    private int checkpointId;

    private ComponentMapper<PlayerComponent> pcm;
    private WorldSystem worldSystem;

    @Override
    public void read() {
        checkpointId = readInt();
    }

    @Override
    public void run() {
        Area spawnArea = worldSystem.getWorld().getPlayerSpawnAreas().get(checkpointId);
        if (spawnArea != null) {
            pcm.get(getClient()).setSpawnArea(spawnArea);
        }
    }
}
