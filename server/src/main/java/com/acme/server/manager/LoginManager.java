package com.acme.server.manager;

import com.acme.engine.application.Context;
import com.acme.engine.ecs.core.ComponentMapper;
import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.ecs.systems.PassiveSystem;
import com.acme.server.combat.StatsController;
import com.acme.server.component.KnownListComponent;
import com.acme.server.component.PlayerComponent;
import com.acme.server.component.PositionComponent;
import com.acme.server.component.WorldComponent;
import com.acme.server.controller.PositionController;
import com.acme.server.inventory.Inventory;
import com.acme.server.packet.outbound.HitPointsPacket;
import com.acme.server.packet.outbound.WelcomePacket;
import com.acme.server.system.KnownListSystem;
import com.acme.server.system.PacketSystem;
import com.acme.server.util.PositionUtils;
import com.acme.server.util.Rnd;
import com.acme.server.world.Area;
import com.acme.server.world.Instance;
import com.acme.server.world.Orientation;
import com.acme.server.world.Position;

import java.util.Collection;

@Wire
public class LoginManager extends PassiveSystem {

    private ComponentMapper<Inventory> icm;
    private ComponentMapper<PlayerComponent> pcm;
    private ComponentMapper<PositionComponent> poscm;
    private ComponentMapper<WorldComponent> wcm;
    private ComponentMapper<KnownListComponent> kcm;

    private Context context;

    private PositionController positionController;
    private StatsController statsController;
    private WorldManager worldManager;
    private KnownListSystem knownListSystem;
    private PacketSystem packetSystem;

    public void login(Entity entity, String name, int weapon, int armor) {
        PlayerComponent playerComponent = pcm.get(entity);

        if (playerComponent.getState() == PlayerComponent.State.PLAYING) {
            respawnPlayer(entity);
            return;
        }

        playerComponent.setName(name);
        playerComponent.setState(PlayerComponent.State.PLAYING);

        PositionComponent positionComponent = poscm.get(entity);

        Collection<Area> startingAreas = worldManager.getWorld().getPlayerStartingAreas().values();
        int i = Rnd.between(0, startingAreas.size() - 1);
        Area area = startingAreas.stream().skip(i).findFirst().get();
        Position position = PositionUtils.getRandomPositionInside(area);
        playerComponent.setSpawnArea(area);
        positionComponent.setPosition(position);
        positionComponent.setOrientation(Orientation.BOTTOM);

        WorldComponent worldComponent = wcm.get(entity);
        Instance instance = worldManager.getAvailableInstance();
        worldComponent.setInstance(instance);

        Inventory inventory = icm.get(entity);
        inventory.setWeapon(weapon);
        inventory.setArmor(armor);

        int hitPoints = 200;
        statsController.setMaxHitPointsAndReset(entity, hitPoints);
        context.schedule(() -> spawnPlayer(entity, name, position, hitPoints));
    }

    private void respawnPlayer(Entity entity) {
        PlayerComponent playerComponent = pcm.get(entity);
        Area spawnArea = playerComponent.getSpawnArea();
        Position position = PositionUtils.getRandomPositionInside(spawnArea);
        poscm.get(entity).setPosition(position);
        statsController.resetHitPoints(entity);
        knownListSystem.clearKnownList(entity);
        context.schedule(() -> spawnPlayer(entity, playerComponent.getName(), position, statsController.getMaxHitPoints(entity)));
    }

    private void spawnPlayer(Entity entity, String name, Position position, int hitPoints) {
        KnownListComponent knownListComponent = kcm.get(entity);
        knownListComponent.setDistanceToFindObject(100);
        knownListComponent.setDistanceToForgetObject(100);

        if (worldManager.getPlayerById(entity.getId()) == null) {
            worldManager.bringIntoWorld(entity);
        }
        worldManager.spawn(entity);
        WelcomePacket welcomePacket = new WelcomePacket(entity.getId(), name, position.getX(), position.getY(), 0);
        packetSystem.sendPacket(entity, welcomePacket);
        packetSystem.sendPacket(entity, new HitPointsPacket(hitPoints));
    }
}
