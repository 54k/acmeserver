package com.acme.engine.ecs.core;

import com.acme.engine.ecs.utils.reflection.ClassReflection;
import com.acme.engine.ecs.utils.reflection.Method;

import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.Map;

public class NodeFamily<T extends Node> {

    private static final Map<Class<? extends Node>, NodeFamily> nodeMappers = new HashMap<>();

    private final Class<T> nodeClass;
    private final Family family;

    private NodeFamily(Class<T> nodeClass) {
        this.nodeClass = nodeClass;
        family = getFamilyFor(nodeClass);
    }

    private static Family getFamilyFor(Class<? extends Node> nodeClass) {
        Iterable<Class<Component>> components = ClassReflection.getComponentsFor(nodeClass);
        Family.Builder builder = new Family.Builder();
        for (Class<Component> component : components) {
            builder.all(component);
        }
        return builder.get();
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
        return family.getIndex();
    }

    public T get(Entity entity) {
        return ClassReflection.newProxyInstance(nodeClass, new NodeProxyHandler(entity));
    }

    public boolean matches(Entity entity) {
        return family.matches(entity);
    }

    @Override
    public int hashCode() {
        int result = nodeClass != null ? nodeClass.hashCode() : 0;
        result = 31 * result + (family != null ? family.hashCode() : 0);
        return result;
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

        if (family != null ? !family.equals(that.family) : that.family != null) {
            return false;
        }
        return !(nodeClass != null ? !nodeClass.equals(that.nodeClass) : that.nodeClass != null);
    }

    private static final class NodeProxyHandler implements InvocationHandler {

        private final Entity entity;

        public NodeProxyHandler(Entity entity) {
            this.entity = entity;
        }

        // TODO proxy equals method properly
        @SuppressWarnings("unchecked")
        @Override
        public Object invoke(Object proxy, java.lang.reflect.Method m, Object[] args) throws Throwable {
            Method method = new Method(m);

            if (method.isComponentMethod()) {
                Class<Component> componentClass = (Class<Component>) method.getReturnType();
                return ComponentMapper.getFor(componentClass).get(entity);
            } else if (method.isGetEntityMethod()) {
                return entity;
            } else if (method.isObjectMethod()) {
                return method.invoke(entity, args);
            }

            throw new RuntimeException("Unknown method " + method);
        }
    }
}
