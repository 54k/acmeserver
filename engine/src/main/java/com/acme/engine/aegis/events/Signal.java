package com.acme.engine.aegis.events;

import java.util.ArrayList;
import java.util.List;

public final class Signal<T> {

    private List<SignalListener<T>> listeners;

    public Signal() {
        listeners = new ArrayList<>();
    }

    /**
     * Add a Listener to this Signal
     *
     * @param listener The Listener to be added
     */
    public void add(SignalListener<T> listener) {
        listeners.add(listener);
    }

    /**
     * Remove a listener from this Signal
     *
     * @param listener The Listener to remove
     */
    public void remove(SignalListener<T> listener) {
        listeners.remove(listener);
    }

    /**
     * Removes all listeners attached to this {@link Signal}.
     */
    public void removeAllListeners() {
        listeners.clear();
    }

    /**
     * Dispatches an event to all Listeners registered to this Signal
     *
     * @param object The object to send off
     */
    public void dispatch(T object) {
        for (SignalListener<T> listener : new ArrayList<>(listeners)) {
            listener.receive(this, object);
        }
    }
}
