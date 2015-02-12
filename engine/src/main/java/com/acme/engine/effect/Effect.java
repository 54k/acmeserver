package com.acme.engine.effect;

import com.badlogic.ashley.core.Entity;

public interface Effect {

    void apply(Entity entity);

    void update(Entity entity, float deltaTime);

    void remove(Entity entity);
}
