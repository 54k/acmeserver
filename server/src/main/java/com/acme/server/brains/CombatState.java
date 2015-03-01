package com.acme.server.brains;

import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.brains.BrainState;
import com.acme.engine.mechanics.brains.BrainStateMachine;
import com.acme.server.combat.CombatController;
import com.acme.server.combat.HateListController;
import com.acme.server.controller.PositionController;
import com.acme.server.system.PacketSystem;
import com.acme.server.world.Position;

@Wire
public class CombatState implements BrainState<Entity> {

    private CombatController combatController;
    private HateListController hateListController;
    private PositionController positionController;
    private PacketSystem packetSystem;

    @Override
    public void enter(BrainStateMachine<Entity> brainStateMachine) {
    }

    @Override
    public void update(BrainStateMachine<Entity> brainStateMachine, float deltaTime) {
        Entity owner = brainStateMachine.getOwner();
        Entity target = combatController.getTarget(owner);
        Entity mostHated = hateListController.getMostHated(owner);

        if (mostHated == null) {
            return;
        }

        if (target != mostHated) {
            combatController.setTarget(owner, mostHated);
            combatController.engage(owner, mostHated);
        } else {
            Position targetPosition = positionController.getPosition(target);
            positionController.updatePosition(owner, targetPosition);
        }
    }

    @Override
    public void exit(BrainStateMachine<Entity> brainStateMachine) {
        Entity owner = brainStateMachine.getOwner();
        combatController.setTarget(owner, null);
        hateListController.clearHaters(owner);
    }
}
