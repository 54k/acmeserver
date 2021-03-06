package com.acme.server.combat;

import com.acme.ecs.core.*;
import com.acme.ecs.systems.PassiveSystem;
import com.acme.ecs.utils.ImmutableList;
import com.acme.server.utils.EntityContainer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Wire
public class HateListSystem extends PassiveSystem implements EntityListener, CombatListener {

	private static final Aspect HATE_LIST_ASPECT = Aspect.all(HateList.class).get();

	private ComponentMapper<HateList> hateListCm;
	private ImmutableList<Entity> hateOwners;

	@Override
	public void addedToEngine(Engine engine) {
		engine.addEntityListener(HATE_LIST_ASPECT, this);
		hateOwners = engine.getEntitiesFor(HATE_LIST_ASPECT);

	}

	@Override
	public void onEntityDamaged(Entity attacker, Entity victim, int damage) {
		if (HATE_LIST_ASPECT.matches(victim)) {
			increaseHate(victim, attacker, damage);
		}
	}

	@Override
	public void onEntityKilled(Entity killer, Entity victim) {
		removeHater(victim);
		if (HATE_LIST_ASPECT.matches(victim)) {
			clearHaters(victim);
		}
	}

	@Override
	public void entityAdded(Entity entity) {
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
