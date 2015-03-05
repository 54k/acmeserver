package com.acme.engine.ecs.core;

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
     * @param componentType The {@link Component} class
     * @return A ComponentType matching the Component Class
     */
    public static ComponentType getFor(Class<? extends Component> componentType) {
        ComponentType type = componentTypes.get(componentType);

        if (type == null) {
            type = new ComponentType();
            componentTypes.put(componentType, type);
        }

        return type;
    }

    /**
     * Quick helper method.
     *
     * @param componentType The {@link Component} class
     * @return The index for the specified {@link Component} Class
     */
    public static int getIndexFor(Class<? extends Component> componentType) {
        return getFor(componentType).getIndex();
    }

    /**
     * @param componentTypes list of {@link Component} classes
     * @return Bits representing the collection of components for quick comparison and matching.
     */
    @SafeVarargs
    public static BitSet getBitsFor(Class<? extends Component>... componentTypes) {
        BitSet bits = new BitSet();

        int typesLength = componentTypes.length;
        for (int i = 0; i < typesLength; i++) {
            bits.set(ComponentType.getIndexFor(componentTypes[i]));
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
