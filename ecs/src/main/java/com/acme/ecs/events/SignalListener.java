package com.acme.ecs.events;

public interface SignalListener<T> {
    /**
     * @param signal The Signal that triggered event
     * @param object The object passed on dispatch
     */
    public void receive(Signal<T> signal, T object);
}