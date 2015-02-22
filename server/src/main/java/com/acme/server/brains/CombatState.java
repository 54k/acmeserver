package com.acme.server.brains;

import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.brains.BrainState;
import com.acme.server.combat.CombatController;
import com.acme.server.combat.HateListController;
import com.acme.server.controller.PositionController;
import com.acme.server.packet.outbound.ChatPacket;
import com.acme.server.system.PacketSystem;
import com.acme.server.world.Position;

@Wire
public class CombatState implements BrainState<Entity> {

    private CombatController combatController;
    private HateListController hateListController;
    private PositionController positionController;
    private PacketSystem packetSystem;

    @Override
    public void enter(Entity entity) {
        packetSystem.sendToSelfAndRegion(entity, new ChatPacket(entity, "To arms!"));
    }

    @Override
    public void update(Entity entity, float deltaTime) {
        Entity target = combatController.getTarget(entity);
        Entity mostHated = hateListController.getMostHated(entity);

        if (target != mostHated) {
            combatController.setTarget(entity, mostHated);
            combatController.engage(entity, mostHated);
        } else {
            Position targetPosition = positionController.getPosition(target);
            positionController.updatePosition(entity, targetPosition);
        }
    }

    @Override
    public void exit(Entity entity) {
        System.out.println("Entity " + entity.getId() + " stop combating");
        combatController.setTarget(entity, null);
        hateListController.clearHaters(entity);
    }
}
