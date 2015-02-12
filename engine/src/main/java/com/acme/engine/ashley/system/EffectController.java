package com.acme.engine.ashley.system;

import com.acme.engine.ashley.component.EffectListComponent;
import com.acme.engine.effect.Effect;
import com.acme.engine.effect.EffectList;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

public abstract class EffectController extends ManagerSystem implements Effect<Entity> {

    private static final ComponentMapper<EffectListComponent> ecm = ComponentMapper.getFor(EffectListComponent.class);

    public EffectList<Entity> getEffectList(Entity entity) {
        return ecm.get(entity).getEffectList();
    }

    public void applyEffect(Entity entity) {
        getEffectList(entity).apply(this);
    }

    @Override
    public boolean stack(Entity entity, Effect<Entity> effect) {
        return true;
    }

    @Override
    public void apply(Entity entity) {
    }

    @Override
    public void update(Entity entity, float deltaTime) {
    }

    @Override
    public void remove(Entity entity) {
    }
}
