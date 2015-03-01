package com.acme.engine.ecs.fsm;

import com.acme.engine.ecs.core.Component;

/**
 * Used by the {@link EntityState} class to create the mappings of components to providers via a fluent interface.
 */
public class StateComponentMapping<T extends Component> {

    private EntityState creatingState;
    private Class<T> componentClass;

    /**
     * Used internally, the constructor creates a component mapping. The constructor
     * creates a {@link ComponentTypeProvider} as the default mapping, which will be replaced
     * by more specific mappings if other methods are called.
     *
     * @param creatingState  The EntityState that the mapping will belong to
     * @param componentClass The component class for the mapping
     */
    StateComponentMapping(EntityState creatingState, Class<T> componentClass) {
        this.creatingState = creatingState;
        this.componentClass = componentClass;
        withType(componentClass);
    }

    /**
     * Creates a mapping for the component type to a specific component instance. A
     * {@link ComponentInstanceProvider} is used for the mapping.
     *
     * @param component The component instance to use for the mapping
     * @return This ComponentMapping, so more modifications can be applied
     */
    public StateComponentMapping<T> withInstance(T component) {
        setProvider(new ComponentInstanceProvider<>(component));
        return this;
    }

    /**
     * Creates a mapping for the component type to new instances of the provided type.
     * The type should be the same as or extend the type for this mapping. A {@link ComponentTypeProvider}
     * is used for the mapping.
     *
     * @param componentClass The type of components to be created by this mapping
     * @return This ComponentMapping, so more modifications can be applied
     */
    public StateComponentMapping<T> withType(Class<T> componentClass) {
        setProvider(new ComponentTypeProvider<>(componentClass));
        return this;
    }

    /**
     * Creates a mapping for the component type to a single instance of the provided type.
     * The instance is not created until it is first requested. The type should be the same
     * as or extend the type for this mapping. A {@link ComponentSingletonProvider} is used for
     * the mapping.
     *
     * @param componentClass The type of the single instance to be created. If omitted, the type of the
     *                       mapping is used.
     * @return This ComponentMapping, so more modifications can be applied
     */
    public StateComponentMapping<T> withSingleton(Class<T> componentClass) {
        setProvider(new ComponentSingletonProvider<>(componentClass));
        return this;
    }

    /**
     * Creates a mapping for the component type to any {@link ComponentProvider}.
     *
     * @param provider The component provider to use.
     * @return This ComponentMapping, so more modifications can be applied.
     */
    public StateComponentMapping<T> withProvider(ComponentProvider<T> provider) {
        setProvider(provider);
        return this;
    }

    private void setProvider(ComponentProvider<T> provider) {
        creatingState.providers.put(componentClass, provider);
    }
}
