package com.acme.gameserver.controller;

import com.acme.core.ashley.EntityEngine;
import com.acme.core.ashley.ManagerSystem;
import com.acme.core.ashley.Wired;
import com.acme.gameserver.component.HateComponent;
import com.acme.gameserver.event.CombatEvents;
import com.acme.gameserver.event.HateEvents;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Wired
public class HateController extends ManagerSystem implements CombatEvents {

    private static final Family HATE_OWNERS_FAMILY = Family.all(HateComponent.class).get();

    private ComponentMapper<HateComponent> hcm;

    private ImmutableArray<Entity> hateOwners;

    @Override
    public void addedToEngine(EntityEngine engine) {
        super.addedToEngine(engine);
        hateOwners = engine.getEntitiesFor(HATE_OWNERS_FAMILY);
    }

    @Override
    public void removedFromEngine(EntityEngine engine) {
        super.removedFromEngine(engine);
        hateOwners = null;
    }

    @Override
    public void onEntityDamaged(Entity attacker, Entity victim, int damage) {
        if (HATE_OWNERS_FAMILY.matches(victim)) {
            increaseHate(victim, attacker, damage);
        }
    }

    @Override
    public void onEntityKilled(Entity killer, Entity victim) {
        removeHater(victim);
        if (HATE_OWNERS_FAMILY.matches(victim)) {
            clearHaters(victim);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
        removeHater(entity);
    }

    public void increaseHate(Entity entity, Entity hater, int amount) {
        HateComponent hateComponent = hcm.get(entity);
        Map<Entity, Integer> attackers = hateComponent.getHaters();
        if (attackers.putIfAbsent(hater, 0) == null) {
            post(HateEvents.class).onHaterAdded(entity, hater);
        }
        attackers.compute(hater, (e, hate) -> hate + amount);
    }

    public void removeHater(Entity hater) {
        hateOwners.forEach(e -> removeHater(e, hater));
    }

    public void removeHater(Entity entity, Entity hater) {
        HateComponent hateComponent = hcm.get(entity);
        Map<Entity, Integer> haters = hateComponent.getHaters();
        if (haters.remove(hater) != null) {
            Entity target = hateComponent.getTarget();
            if (target == hater) {
                hateComponent.setTarget(null);
            }
            post(HateEvents.class).onHaterRemoved(entity, hater);
        }
        if (haters.isEmpty()) {
            post(HateEvents.class).onHatersEmpty(entity);
        }
    }

    public void clearHaters(Entity entity) {
        HateComponent hateComponent = hcm.get(entity);
        Set<Entity> haters = hateComponent.getHaters().keySet();
        Set<Entity> h = new HashSet<>();
        h.addAll(haters);

        haters.clear();
        hateComponent.setTarget(null);

        HateEvents hateEvents = post(HateEvents.class);
        h.forEach(hater -> hateEvents.onHaterRemoved(entity, hater));
        hateEvents.onHatersEmpty(entity);
    }

    public boolean hasHaters(Entity entity) {
        return !hcm.get(entity).getHaters().isEmpty();
    }
}
