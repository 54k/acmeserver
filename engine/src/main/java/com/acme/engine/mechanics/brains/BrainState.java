package com.acme.engine.mechanics.brains;

public interface BrainState<E> {

    void enter(BrainStateMachine<E> brainStateMachine);

    void update(BrainStateMachine<E> brainStateMachine, float deltaTime);

    void exit(BrainStateMachine<E> brainStateMachine);
}
