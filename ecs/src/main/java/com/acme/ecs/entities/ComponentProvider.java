package com.acme.ecs.entities;

import com.acme.ecs.core.Component;
import com.acme.ecs.core.ComponentType;

/**
 * This is the Interface for component providers. Component providers are used to supply components
 * for states within an {@link EntityStateMachine}.
 */
public interface ComponentProvider<T extends Component> {

    /**
     * Used to request a component from the provider.
     *
     * @return A component for use in the state that the entities is entering
     */
    T getComponent();

    /**
     * Returns an {@link ComponentType} that is used to determine whether two component providers will
     * return the equivalent components.
     * <p>
     * If an entities is changing state and the state it is leaving and the state is is
     * entering have components of the same type, then the {@link ComponentType}s of the component
     * providers are compared. If the two {@link ComponentType}s are the same then the component
     * is not removed. If they are different, the component from the old state is removed
     * and a component for the new state is added.</p>
     *
     * @return {@link ComponentType}
     */
    ComponentType getComponentType();
}
