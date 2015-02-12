package com.acme.engine.brain;

import com.badlogic.ashley.core.Entity;

public interface BrainState {

    void enter(Entity entity);

    void update(Entity entity, float deltaTime);

    void exit(Entity entity);
}
