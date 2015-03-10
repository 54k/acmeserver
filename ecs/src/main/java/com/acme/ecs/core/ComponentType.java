package com.acme.ecs.core;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class ComponentType {

    private static final Map<Class<? extends Component>, ComponentType> componentTypes = new HashMap<>();
    private static int typeIndex = 0;

    private final int index;

    private ComponentType() {
        index = typeIndex++;
    }

    /**
     * @return This ComponentType's unique index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param componentClass The {@link Component} class
     * @return A ComponentType matching the Component Class
     */
    public static ComponentType getFor(Class<? extends Component> componentClass) {
        ComponentType type = componentTypes.get(componentClass);

        if (type == null) {
            type = new ComponentType();
            componentTypes.put(componentClass, type);
        }

        return type;
    }

    /**
     * Quick helper method.
     *
     * @param componentClass The {@link Component} class
     * @return The index for the specified {@link Component} Class
     */
    public static int getIndexFor(Class<? extends Component> componentClass) {
        return getFor(componentClass).getIndex();
    }

    /**
     * @param componentClasses list of {@link Component} classes
     * @return Bits representing the collection of components for quick comparison and matching.
     */
    @SafeVarargs
    public static BitSet getBitsFor(Class<? extends Component>... componentClasses) {
        BitSet bits = new BitSet();

        for (Class<? extends Component> componentClass : componentClasses) {
            bits.set(ComponentType.getIndexFor(componentClass));
        }

        return bits;
    }

    @Override
    public int hashCode() {
        return index;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ComponentType other = (ComponentType) obj;
        return index == other.index;
    }
}
