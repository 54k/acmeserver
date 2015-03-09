package com.acme.ecs.core;

import com.acme.ecs.utils.Bag;
import com.acme.ecs.utils.ImmutableList;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entity {

    long id;
    boolean scheduledForRemoval;
    Engine.ComponentOperationHandler componentOperationHandler;

    private List<ComponentListener> listeners;
    private Map<Class<? extends Component>, List<ComponentListener>> listenersByComponent;

    private Bag<Component> components;
    private List<Component> componentsArray;
    private ImmutableList<Component> immutableComponentsArray;
    private BitSet componentBits;
    private BitSet familyBits;
    private BitSet nodeBits;

    /**
     * Creates an empty Entity.
     */
    public Entity() {
        listeners = new ArrayList<>(16);
        listenersByComponent = new HashMap<>();

        components = new Bag<>();
        componentsArray = new ArrayList<>(16);
        immutableComponentsArray = new ImmutableList<>(componentsArray);
        componentBits = new BitSet();
        familyBits = new BitSet();
        nodeBits = new BitSet();
    }

    /**
     * @return The Entity's unique id.
     */
    public long getId() {
        return id;
    }

    public void addComponentListener(ComponentListener listener) {
        listeners.add(listener);
    }

    public void addComponentListener(Class<? extends Component> componentClass, ComponentListener listener) {
        List<ComponentListener> listeners = listenersByComponent.get(componentClass);

        if (listeners == null) {
            listeners = new ArrayList<>(16);
            listenersByComponent.put(componentClass, listeners);
        }

        listeners.add(listener);
    }

    public void removeComponentListener(ComponentListener listener) {
        listeners.remove(listener);

        for (List<ComponentListener> listeners : listenersByComponent.values()) {
            listeners.remove(listener);
        }
    }

    /**
     * Adds a {@link Component} to this Entity. If a {@link Component} of the same type already exists, it'll be replaced.
     *
     * @return The Entity for easy chaining
     */
    public Entity addComponent(Component component) {
        if (componentOperationHandler != null) {
            componentOperationHandler.add(this, component);
        } else {
            addInternal(component);
        }
        return this;
    }

    /**
     * Removes the {@link Component} of the specified type. Since there is only ever one component of one type, we don't need an
     * instance reference.
     *
     * @return The removed {@link Component}, or null if the Entity did no contain such a component.
     */
    public Component removeComponent(Class<? extends Component> componentClass) {
        ComponentType componentType = ComponentType.getFor(componentClass);
        int componentTypeIndex = componentType.getIndex();
        Component removeComponent = components.get(componentTypeIndex);

        if (componentOperationHandler != null) {
            componentOperationHandler.remove(this, componentClass);
        } else {
            removeInternal(componentClass);
        }

        return removeComponent;
    }

    /**
     * Removes all the {@link Component}'s from the Entity.
     */
    public void removeAll() {
        if (componentOperationHandler != null) {
            componentOperationHandler.removeAll(this);
        } else {
            removeAllInternal();
        }
    }

    void removeAllInternal() {
        while (componentsArray.size() > 0) {
            removeInternal(componentsArray.get(0).getClass());
        }
    }

    /**
     * @return immutable collection with all the Entity {@link Component}s.
     */
    public ImmutableList<Component> getComponents() {
        return immutableComponentsArray;
    }

    /**
     * Retrieve a component from this {@link Entity} by class. <em>Note:</em> the preferred way of retrieving {@link Component}s is
     * using {@link ComponentMapper}s. This method is provided for convenience; using a ComponentMapper provides O(1) access to
     * components while this method provides only O(logn).
     *
     * @param componentClass the class of the component to be retrieved.
     * @return the instance of the specified {@link Component} attached to this {@link Entity}, or null if no such
     * {@link Component} exists.
     */
    public <T extends Component> T getComponent(Class<T> componentClass) {
        return getComponent(ComponentType.getFor(componentClass));
    }

    /**
     * Internal use.
     *
     * @return The {@link Component} object for the specified class, null if the Entity does not have any components for that class.
     */
    @SuppressWarnings("unchecked")
    <T extends Component> T getComponent(ComponentType componentType) {
        int componentTypeIndex = componentType.getIndex();

        if (componentTypeIndex < components.getCapacity()) {
            return (T) components.get(componentType.getIndex());
        } else {
            return null;
        }
    }

    public boolean hasComponent(Class<? extends Component> componentClass) {
        return hasComponent(ComponentType.getFor(componentClass));
    }

    /**
     * Internal use.
     *
     * @return Whether or not the Entity has a {@link Component} for the specified class.
     */
    boolean hasComponent(ComponentType componentType) {
        return componentBits.get(componentType.getIndex());
    }

    /**
     * Internal use.
     *
     * @return This Entity's component bits, describing all the {@link Component}s it contains.
     */
    BitSet getComponentBits() {
        return componentBits;
    }

    /**
     * @return This Entity's {@link Family} bits, describing all the {@link EntitySystem}s it currently is being processed by.
     */
    BitSet getFamilyBits() {
        return familyBits;
    }

    BitSet getNodeBits() {
        return nodeBits;
    }

    Entity addInternal(Component component) {
        Class<? extends Component> componentClass = component.getClass();

        Component oldComponent = getComponent(componentClass);

        if (component == oldComponent) {
            return this;
        }

        if (oldComponent != null) {
            removeInternal(componentClass);
        }

        int componentTypeIndex = ComponentType.getIndexFor(componentClass);

        components.set(componentTypeIndex, component);
        componentsArray.add(component);
        componentBits.set(componentTypeIndex);
        notifyComponentAdd(component);
        return this;
    }

    Component removeInternal(Class<? extends Component> componentClass) {
        ComponentType componentType = ComponentType.getFor(componentClass);
        int componentTypeIndex = componentType.getIndex();
        Component removeComponent = components.get(componentTypeIndex);

        if (removeComponent != null) {
            components.set(componentTypeIndex, null);
            componentsArray.remove(removeComponent);
            componentBits.clear(componentTypeIndex);
            notifyComponentRemove(removeComponent);
        }

        return removeComponent;
    }

    private void notifyComponentAdd(Component component) {
        for (ComponentListener listener : listeners) {
            listener.componentAdded(this, component);
        }

        List<ComponentListener> listeners = listenersByComponent.get(component.getClass());
        if (listeners != null) {
            for (ComponentListener listener : listeners) {
                listener.componentAdded(this, component);
            }
        }
    }

    private void notifyComponentRemove(Component component) {
        for (ComponentListener listener : listeners) {
            listener.componentRemoved(this, component);
        }

        List<ComponentListener> listeners = listenersByComponent.get(component.getClass());
        if (listeners != null) {
            for (ComponentListener listener : listeners) {
                listener.componentRemoved(this, component);
            }
        }
    }

    public <T extends Node> T getNode(Class<T> nodeClass) {
        return NodeFamily.getFor(nodeClass).get(this);
    }

    public boolean matchesNode(Class<? extends Node> nodeClass) {
        return NodeFamily.getFor(nodeClass).matches(this);
    }

    /**
     * @return true if the entities is scheduled to be removed
     */
    public boolean isScheduledForRemoval() {
        return scheduledForRemoval;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Entity)) {
            return false;
        }
        Entity other = (Entity) obj;
        return id == other.id;
    }
}
