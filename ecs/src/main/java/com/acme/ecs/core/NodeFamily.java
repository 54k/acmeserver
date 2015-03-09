package com.acme.ecs.core;

import com.acme.ecs.utils.reflection.ClassReflection;
import com.acme.ecs.utils.reflection.Method;

import java.lang.reflect.InvocationHandler;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class NodeFamily<T extends Node> {

    private static final Map<Class<? extends Node>, NodeFamily> nodeMappers = new HashMap<>();
    private static int nodeIndex = 0;

    private final Class<T> nodeClass;
    private final BitSet nodeBits;

    private final int index;

    private NodeFamily(Class<T> nodeClass) {
        this.nodeClass = nodeClass;
        this.nodeBits = getBitsFor(nodeClass);
        index = nodeIndex++;
    }

    private static BitSet getBitsFor(Class<? extends Node> nodeClass) {
        BitSet bitSet = new BitSet();
        Iterable<Class<Component>> components = ClassReflection.getComponentsFor(nodeClass);
        for (Class<Component> component : components) {
            int index = ComponentType.getFor(component).getIndex();
            bitSet.set(index);
        }
        return bitSet;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Node> NodeFamily<T> getFor(Class<T> nodeClass) {
        NodeFamily<T> nodeFamily = (NodeFamily<T>) nodeMappers.get(nodeClass);
        if (nodeFamily == null) {
            nodeFamily = new NodeFamily<>(nodeClass);
            nodeMappers.put(nodeClass, nodeFamily);
        }
        return nodeFamily;
    }

    public int getIndex() {
        return index;
    }

    public T get(Entity entity) {
        return ClassReflection.newProxyInstance(nodeClass, new NodeProxyHandler(entity));
    }

    public boolean matches(Entity entity) {
        return entity.getComponentBits().intersects(nodeBits);
    }

    @Override
    public int hashCode() {
        return nodeClass.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NodeFamily that = (NodeFamily) o;
        return nodeClass.equals(that.nodeClass);
    }

    private static final class NodeProxyHandler implements InvocationHandler {

        private final Entity entity;

        public NodeProxyHandler(Entity entity) {
            this.entity = entity;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object invoke(Object proxy, java.lang.reflect.Method m, Object[] args) throws Throwable {
            Method method = new Method(m);

            if (method.isComponentMethod()) {
                Class<Component> componentClass = (Class<Component>) method.getReturnType();
                return entity.getComponent(ComponentType.getFor(componentClass));
            } else if (method.isGetEntityMethod()) {
                return entity;
            } else if (method.isEqualsMethod()) {
                return equals(args[0]);
            } else if (method.isHashCodeMethod()) {
                return hashCode();
            } else if (method.isToStringMethod()) {
                return entity.toString();
            }

            throw new RuntimeException("Unknown method " + method);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }

            if (o instanceof Entity) {
                return entity.equals(o);
            }
            Node that = (Node) o;
            return entity.equals(that.getEntity());
        }

        @Override
        public int hashCode() {
            return entity.hashCode();
        }
    }
}
