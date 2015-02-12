package com.acme.engine.ashley.system;

import com.acme.engine.effect.Effect;
import com.badlogic.ashley.core.Entity;

public abstract class EffectController extends ManagerSystem implements Effect {

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
