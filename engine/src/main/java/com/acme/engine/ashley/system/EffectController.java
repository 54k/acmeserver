package com.acme.engine.ashley.system;

import com.acme.engine.ashley.component.EffectListComponent;
import com.acme.engine.effect.Effect;
import com.acme.engine.effect.EffectList;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

public abstract class EffectController extends ManagerSystem implements Effect {

    private static final ComponentMapper<EffectListComponent> ecm = ComponentMapper.getFor(EffectListComponent.class);

    public EffectList getEffectList(Entity entity) {
        return ecm.get(entity).getEffectList();
    }

    public void applyEffect(Entity entity) {
        getEffectList(entity).apply(this);
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
