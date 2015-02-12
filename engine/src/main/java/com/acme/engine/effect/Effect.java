package com.acme.engine.effect;

public interface Effect<E> {

    boolean stack(E entity, Effect<E> effect);

    void apply(E entity);

    void update(E entity, float deltaTime);

    void remove(E entity);
}
