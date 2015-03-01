package com.acme.server.brains;

import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.brains.BrainState;
import com.acme.engine.mechanics.brains.BrainStateMachine;
import com.acme.server.combat.CombatSystem;
import com.acme.server.combat.HateListSystem;
import com.acme.server.packets.PacketSystem;
import com.acme.server.position.TransformSystem;
import com.acme.server.world.Position;

@Wire
public class CombatState implements BrainState<Entity> {

    private CombatSystem combatSystem;
    private HateListSystem hateListSystem;
    private TransformSystem transformSystem;
    private PacketSystem packetSystem;

    @Override
    public void enter(BrainStateMachine<Entity> brainStateMachine) {
    }

    @Override
    public void update(BrainStateMachine<Entity> brainStateMachine, float deltaTime) {
        Entity owner = brainStateMachine.getOwner();
        Entity target = combatSystem.getTarget(owner);
        Entity mostHated = hateListSystem.getMostHated(owner);

        if (mostHated == null) {
            return;
        }

        if (target != mostHated) {
            combatSystem.setTarget(owner, mostHated);
            combatSystem.engage(owner, mostHated);
        } else {
            Position targetPosition = transformSystem.getPosition(target);
            transformSystem.updatePosition(owner, targetPosition);
        }
    }

    @Override
    public void exit(BrainStateMachine<Entity> brainStateMachine) {
        Entity owner = brainStateMachine.getOwner();
        combatSystem.setTarget(owner, null);
        hateListSystem.clearHaters(owner);
    }
}
