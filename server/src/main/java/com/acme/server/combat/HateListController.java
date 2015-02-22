package com.acme.server.combat;

import com.acme.engine.ecs.core.*;
import com.acme.engine.ecs.systems.PassiveSystem;
import com.acme.engine.ecs.utils.ImmutableList;
import com.acme.server.util.EntityContainer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Wire
public class HateListController extends PassiveSystem implements CombatListener {

    private static final Family hateListFamily = Family.all(HateList.class).get();

    private ComponentMapper<HateList> hateListCm;
    private ImmutableList<Entity> hateOwners;

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        hateOwners = engine.getEntitiesFor(hateListFamily);
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
    public void entityRemoved(Entity entity) {
        removeHater(entity);
    }

    public void increaseHate(Entity entity, Entity hater, int amount) {
        HateList hateList = hateListCm.get(entity);
        Map<Entity, Integer> haters = hateList.haters;
        if (haters.putIfAbsent(hater, 0) == null) {
            event(HateListListener.class).dispatch().onHaterAdded(entity, hater);
        }
        haters.compute(hater, (e, hate) -> hate + amount);
    }

    public void removeHater(Entity hater) {
        hateOwners.forEach(e -> removeHater(e, hater));
    }

    public void removeHater(Entity entity, Entity hater) {
        HateList hateList = hateListCm.get(entity);
        Map<Entity, Integer> haters = hateList.haters;

        if (haters.isEmpty()) {
            return;
        }

        if (haters.remove(hater) != null) {
            event(HateListListener.class).dispatch().onHaterRemoved(entity, hater);
        }
        if (haters.isEmpty()) {
            event(HateListListener.class).dispatch().onHatersEmpty(entity);
        }
    }

    public void clearHaters(Entity entity) {
        HateList hateList = hateListCm.get(entity);
        Set<Entity> haters = hateList.haters.keySet();

        if (haters.isEmpty()) {
            return;
        }

        Set<Entity> h = new HashSet<>();
        h.addAll(haters);

        haters.clear();

        HateListListener hateListListener = event(HateListListener.class).dispatch();
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
