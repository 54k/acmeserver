package com.acme.ecs.core;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a group of {@link Component}s. It is used to describe what {@link Entity} objects an {@link EntitySystem} should
 * process. Example: {@code aspect.all(PositionComponent.class, VelocityComponent.class).get()} Families can't be instantiated
 * directly but must be accessed via a builder ( start with {@code aspect.all()}, {@code aspect.one()} or {@code aspect.exclude()}
 * ), this is to avoid duplicate aspects that describe the same components.
 */
public class Aspect {

    private static int aspectIndex = 0;

    private static final Map<String, Aspect> aspects = new HashMap<>();
    private static final BitSet zeroBits = new BitSet();

    public static final Aspect ALL = Aspect.all().get();

    private final BitSet all;
    private final BitSet one;
    private final BitSet exclude;
    private final int index;

    /**
     * Private constructor
     */
    private Aspect(BitSet all, BitSet any, BitSet exclude) {
        this.all = all;
        this.one = any;
        this.exclude = exclude;
        this.index = aspectIndex++;
    }

    /**
     * @return This aspect's unique index
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * @return Whether the entities matches the aspect requirements or not
     */
    public boolean matches(Entity entity) {
        BitSet entityComponentBits = entity.getComponentBits();

        if (entityComponentBits.isEmpty()) {
            return false;
        }

        for (int i = all.nextSetBit(0); i >= 0; i = all.nextSetBit(i + 1)) {
            if (!entityComponentBits.get(i)) {
                return false;
            }
        }

        if (!one.isEmpty() && !one.intersects(entityComponentBits)) {
            return false;
        }

        if (!exclude.isEmpty() && exclude.intersects(entityComponentBits)) {
            return false;
        }

        return true;
    }

    /**
     * @param componentTypes entities will have to contain all of the specified components.
     * @return A Builder singleton instance to get a aspect
     */
    @SafeVarargs
    public static Builder all(Class<? extends Component>... componentTypes) {
        return new Builder().all(componentTypes);
    }

    /**
     * @param nodeClasses entities will have to contain all of the specified components.
     * @return A Builder singleton instance to get a aspect
     */
    @SafeVarargs
    public static Builder allNodes(Class<? extends Node>... nodeClasses) {
        return new Builder().allNodes(nodeClasses);
    }

    /**
     * @param componentTypes entities will have to contain at least one of the specified components.
     * @return A Builder singleton instance to get a aspect
     */
    @SafeVarargs
    public static Builder one(Class<? extends Component>... componentTypes) {
        return new Builder().one(componentTypes);
    }

    /**
     * @param nodeClasses entities will have to contain at least one of the specified nodes.
     * @return A Builder singleton instance to get a aspect
     */
    @SafeVarargs
    public static Builder oneNodes(Class<? extends Node>... nodeClasses) {
        return new Builder().oneNodes(nodeClasses);
    }

    /**
     * @param componentTypes entities cannot contain any of the specified components.
     * @return A Builder singleton instance to get a aspect
     */
    @SafeVarargs
    public static Builder exclude(Class<? extends Component>... componentTypes) {
        return new Builder().exclude(componentTypes);
    }

    /**
     * @param nodeClasses entities cannot contain any of the specified nodes.
     * @return A Builder singleton instance to get a aspect
     */
    @SafeVarargs
    public static Builder excludeNodes(Class<? extends Node>... nodeClasses) {
        return new Builder().excludeNodes(nodeClasses);
    }

    public static class Builder {
        private BitSet all = zeroBits;
        private BitSet one = zeroBits;
        private BitSet exclude = zeroBits;

        /**
         * Resets the builder instance
         *
         * @return A Builder singleton instance to get a aspect
         */
        public Builder reset() {
            all = zeroBits;
            one = zeroBits;
            exclude = zeroBits;
            return this;
        }

        /**
         * @param componentClasses entities will have to contain all of the specified components.
         */
        @SafeVarargs
        public final Builder all(Class<? extends Component>... componentClasses) {
            BitSet bits = ComponentType.getBitsFor(componentClasses);
            return all(bits);
        }

        /**
         * @param nodeClasses entities will have to contain all of the specified nodes.
         */
        @SafeVarargs
        public final Builder allNodes(Class<? extends Node>... nodeClasses) {
            BitSet bits = NodeFamily.getBitsFor(nodeClasses);
            return all(bits);
        }

        Builder all(BitSet bits) {
            bits.or(all);
            all = bits;
            return this;
        }

        /**
         * @param componentClasses entities will have to contain at least one of the specified components.
         */
        @SafeVarargs
        public final Builder one(Class<? extends Component>... componentClasses) {
            BitSet bits = ComponentType.getBitsFor(componentClasses);
            return one(bits);
        }

        /**
         * @param nodeClasses entities will have to contain at least one of the specified nodes.
         */
        @SafeVarargs
        public final Builder oneNodes(Class<? extends Node>... nodeClasses) {
            BitSet bits = NodeFamily.getBitsFor(nodeClasses);
            return one(bits);
        }

        Builder one(BitSet bits) {
            bits.or(one);
            one = bits;
            return this;
        }

        /**
         * @param componentClasses entities cannot contain any of the specified components.
         */
        @SafeVarargs
        public final Builder exclude(Class<? extends Component>... componentClasses) {
            BitSet bits = ComponentType.getBitsFor(componentClasses);
            return exclude(bits);
        }

        /**
         * @param nodeClasses entities cannot contain any of the specified nodes.
         */
        @SafeVarargs
        public final Builder excludeNodes(Class<? extends Node>... nodeClasses) {
            BitSet bits = NodeFamily.getBitsFor(nodeClasses);
            return exclude(bits);
        }

        Builder exclude(BitSet bits) {
            bits.or(exclude);
            exclude = bits;
            return this;
        }

        /**
         * @return A aspect for the configured component types
         */
        public Aspect get() {
            String hash = getHash(all, one, exclude);
            Aspect aspect = aspects.get(hash);
            if (aspect == null) {
                aspect = new Aspect(all, one, exclude);
                aspects.put(hash, aspect);
            }
            return aspect;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + all.hashCode();
        result = prime * result + one.hashCode();
        result = prime * result + exclude.hashCode();
        result = prime * result + index;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Aspect)) {
            return false;
        }
        Aspect other = (Aspect) obj;
        return index == other.index && all.equals(other.all) && one.equals(other.one) && exclude.equals(other.exclude);
    }

    private static String getHash(BitSet all, BitSet one, BitSet exclude) {
        StringBuilder builder = new StringBuilder();
        if (!all.isEmpty()) {
            builder.append("{all:").append(getBitsString(all)).append("}");
        }
        if (!one.isEmpty()) {
            builder.append("{one:").append(getBitsString(one)).append("}");
        }
        if (!exclude.isEmpty()) {
            builder.append("{exclude:").append(getBitsString(exclude)).append("}");
        }
        return builder.toString();
    }

    private static String getBitsString(BitSet bits) {
        StringBuilder builder = new StringBuilder();

        int numBits = bits.length();
        for (int i = 0; i < numBits; ++i) {
            builder.append(bits.get(i) ? "1" : "0");
        }

        return builder.toString();
    }
}
