package com.acme.server.manager;

import com.acme.commons.ashley.ManagerSystem;
import com.acme.commons.ashley.Wired;
import com.acme.server.component.HateListComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import java.util.Map;

@Wired
public class HateListManager extends ManagerSystem {

    private ComponentMapper<HateListComponent> ccm;

    private CombatManager combatManager;

    public void increaseHate(Entity attacker, Entity target, int damage) {
        HateListComponent hateListComponent = ccm.get(target);
        Map<Entity, Integer> attackers = hateListComponent.getAttackers();
        attackers.putIfAbsent(attacker, 0);
        attackers.compute(attacker, (e, hate) -> hate + damage);
        Entity mostHated = getMostHated(target);
        Entity currentTarget = hateListComponent.getTarget();
        if (currentTarget != target) {
            hateListComponent.setTarget(mostHated);
            combatManager.engage(target, attacker);
        }
    }

    private Entity getMostHated(Entity target) {
        HateListComponent hateListComponent = ccm.get(target);
        Map<Entity, Integer> attackers = hateListComponent.getAttackers();
        return attackers.entrySet().stream()
                .max((e1, e2) -> e1.getValue() > e2.getValue() ? 1 : e1.getValue().equals(e2.getValue()) ? 0 : -1)
                .get().getKey();
    }
}
