package com.acme.engine.effect;

public class BaseEffect<E> implements Effect<E> {

    @Override
    public boolean stack(E entity, Effect<E> effect) {
        return true;
    }

    @Override
    public void apply(E entity) {
    }

    @Override
    public void update(E entity, float deltaTime) {
    }

    @Override
    public void remove(E entity) {
    }
}
