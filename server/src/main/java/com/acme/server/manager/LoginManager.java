package com.acme.server.manager;

import com.acme.commons.application.Context;
import com.acme.commons.ashley.ManagerSystem;
import com.acme.commons.ashley.Wired;
import com.acme.server.component.*;
import com.acme.server.packet.outbound.HitPointsPacket;
import com.acme.server.packet.outbound.WelcomePacket;
import com.acme.server.system.NetworkSystem;
import com.acme.server.util.Rnd;
import com.acme.server.world.Area;
import com.acme.server.world.Instance;
import com.acme.server.world.Orientation;
import com.acme.server.world.Position;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import java.util.Collection;

@Wired
public class LoginManager extends ManagerSystem {

    private Context context;

    private ComponentMapper<PlayerComponent> pcm;
    private ComponentMapper<PositionComponent> poscm;
    private ComponentMapper<WorldComponent> wcm;
    private ComponentMapper<KnownListComponent> kcm;
    private ComponentMapper<StatsComponent> scm;

    private PositionManager positionManager;
    private WorldManager worldManager;
    private InventoryManager inventoryManager;
    private NetworkSystem networkSystem;

    public void login(Entity entity, String name, int weapon, int armor) {
        PlayerComponent playerComponent = pcm.get(entity);
        playerComponent.setName(name);
        playerComponent.setState(PlayerComponent.State.PLAYING);

        PositionComponent positionComponent = poscm.get(entity);

        Collection<Area> startingAreas = worldManager.getWorld().getPlayerStartingAreas().values();
        int i = Rnd.between(0, startingAreas.size() - 1);
        Area area = startingAreas.stream().skip(i).findFirst().get();
        Position position = PositionManager.getRandomPositionInside(area);

        positionComponent.setPosition(position);
        positionComponent.setOrientation(Orientation.BOTTOM);

        WorldComponent worldComponent = wcm.get(entity);
        Instance instance = worldManager.getAvailableInstance();
        worldComponent.setInstance(instance);

        KnownListComponent knownListComponent = kcm.get(entity);
        knownListComponent.setDistanceToFindObject(30);
        knownListComponent.setDistanceToForgetObject(30);

        inventoryManager.tryEquipWeapon(entity, weapon);
        inventoryManager.tryEquipArmor(entity, armor);

        StatsComponent statsComponent = scm.get(entity);
        int hitPoints = 60;
        statsComponent.setHitPoints(hitPoints);
        statsComponent.setMaxHitPoints(hitPoints);

        context.schedule(() -> spawnPlayer(entity, name, position, hitPoints));
    }

    private void spawnPlayer(Entity entity, String name, Position position, int hitPoints) {
        worldManager.bringIntoWorld(entity);
        worldManager.spawn(entity);
        WelcomePacket welcomePacket = new WelcomePacket(entity.getId(), name, position.getX(), position.getY(), 0);
        networkSystem.sendPacket(entity, welcomePacket);
        networkSystem.sendPacket(entity, new HitPointsPacket(hitPoints));
    }
}
