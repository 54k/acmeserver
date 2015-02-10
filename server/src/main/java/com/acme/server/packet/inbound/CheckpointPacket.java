package com.acme.server.packet.inbound;

import com.acme.engine.ashley.Wired;
import com.acme.engine.network.InboundPacket;
import com.acme.server.component.PlayerComponent;
import com.acme.server.manager.WorldManager;
import com.acme.server.world.Area;
import com.badlogic.ashley.core.ComponentMapper;

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
        pcm.get(getClient()).setSpawnArea(spawnArea);
    }
}
