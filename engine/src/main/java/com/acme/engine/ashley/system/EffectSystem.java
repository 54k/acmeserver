package com.acme.engine.ashley.system;

import com.acme.engine.ashley.component.EffectListComponent;
import com.acme.engine.effect.EffectList;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class EffectSystem extends IteratingSystem {

    private static final Family EFFECTS_OWNERS_FAMILY = Family.all(EffectListComponent.class).get();
    private static final ComponentMapper<EffectListComponent> ecm = ComponentMapper.getFor(EffectListComponent.class);

    private Family family;

    public EffectSystem() {
        this(Family.all().get(), 0);
    }

    public EffectSystem(Family family, int priority) {
        super(EFFECTS_OWNERS_FAMILY, priority);
        this.family = family;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (family.matches(entity)) {
            updateEffectList(entity, deltaTime);
        }
    }

    private void updateEffectList(Entity entity, float deltaTime) {
        EffectList<Entity> effectList = ecm.get(entity).getEffectList();
        effectList.update(deltaTime);
    }
}
