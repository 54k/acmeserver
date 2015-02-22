package com.acme.engine.ecs.core;

public class ComponentMapper<T extends Component> {

    private final ComponentType componentType;

    private ComponentMapper(Class<T> componentClass) {
        componentType = ComponentType.getFor(componentClass);
    }

    /**
     * @param componentClass Component class to be retrieved by the mapper.
     * @return New instance that provides fast access to the {@link Component} of the specified class.
     */
    public static <T extends Component> ComponentMapper<T> getFor(Class<T> componentClass) {
        return new ComponentMapper<>(componentClass);
    }

    /**
     * @return The {@link Component} of the specified class belonging to entity.
     */
    public T get(Entity entity) {
        return entity.getComponent(componentType);
    }

    /**
     * @return Whether or not entity has the component of the specified class.
     */
    public boolean has(Entity entity) {
        return entity.hasComponent(componentType);
    }
}
