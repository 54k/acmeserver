package com.acme.commons.ai;

import com.badlogic.ashley.core.Entity;

public interface BrainState {

    default void enter(Entity entity) {
    }

    default void update(Entity entity, float deltaTime) {
    }

    default void exit(Entity entity) {
    }
}
