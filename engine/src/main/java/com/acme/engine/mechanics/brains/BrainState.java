package com.acme.engine.mechanics.brains;

public interface BrainState<E> {

    void enter(E entity);

    void update(E entity, float deltaTime);

    void exit(E entity);
}
