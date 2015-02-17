package com.acme.server.combat;

import com.acme.engine.ashley.EntityEngine;
import com.acme.engine.ashley.ManagerSystem;
import com.acme.engine.ashley.Wired;
import com.acme.server.util.EntityContainer;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Wired
public class HateListController extends ManagerSystem implements CombatListener {

    private static final Family hateListFamily = Family.all(HateList.class).get();

    private ComponentMapper<HateList> hateListCm;
    private ImmutableArray<Entity> hateOwners;

    @Override
    public void addedToEngine(EntityEngine engine) {
        super.addedToEngine(engine);
        hateOwners = engine.getEntitiesFor(hateListFamily);
    }

    @Override
    public void removedFromEngine(EntityEngine engine) {
        super.removedFromEngine(engine);
        hateOwners = null;
    }

    @Override
    public void onEntityDamaged(Entity attacker, Entity victim, int damage) {
        if (hateListFamily.matches(victim)) {
            increaseHate(victim, attacker, damage);
        }
    }

    @Override
    public void onEntityKilled(Entity killer, Entity victim) {
        removeHater(victim);
        if (hateListFamily.matches(victim)) {
            clearHaters(victim);
        }
    }

    @Override
    public void entityRemoved0(Entity entity) {
        removeHater(entity);
    }

    public void increaseHate(Entity entity, Entity hater, int amount) {
        HateList hateList = hateListCm.get(entity);
        Map<Entity, Integer> haters = hateList.haters;
        if (haters.putIfAbsent(hater, 0) == null) {
            post(HateListListener.class).onHaterAdded(entity, hater);
        }
        haters.compute(hater, (e, hate) -> hate + amount);
    }

    public void removeHater(Entity hater) {
        hateOwners.forEach(e -> removeHater(e, hater));
    }

    public void removeHater(Entity entity, Entity hater) {
        HateList hateList = hateListCm.get(entity);
        Map<Entity, Integer> haters = hateList.haters;
        if (haters.remove(hater) != null) {
            post(HateListListener.class).onHaterRemoved(entity, hater);
        }
        if (haters.isEmpty()) {
            post(HateListListener.class).onHatersEmpty(entity);
        }
    }

    public void clearHaters(Entity entity) {
        HateList hateList = hateListCm.get(entity);
        Set<Entity> haters = hateList.haters.keySet();
        Set<Entity> h = new HashSet<>();
        h.addAll(haters);

        haters.clear();

        HateListListener hateListListener = post(HateListListener.class);
        h.forEach(hater -> hateListListener.onHaterRemoved(entity, hater));
        hateListListener.onHatersEmpty(entity);
    }

    public EntityContainer getHaters(Entity entity) {
        EntityContainer entityContainer = new EntityContainer();
        entityContainer.addAll(hateListCm.get(entity).haters.keySet());
        return entityContainer;
    }

    public boolean hasHaters(Entity entity) {
        return !hateListCm.get(entity).haters.isEmpty();
    }

    public Entity getMostHated(Entity entity) {
        return hateListCm.get(entity).getMostHated();
    }
}
