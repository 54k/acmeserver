package com.acme.engine.ecs.core;

import com.acme.engine.ecs.utils.reflection.ClassReflection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeMapper<T extends Node> {

    private static final Map<Class<? extends Node>, NodeMapper> nodeMappers = new HashMap<>();

    private final Class<T> nodeClass;
    private final Family family;

    private NodeMapper(Class<T> nodeClass) {
        this.nodeClass = nodeClass;
        family = getFamilyFor(nodeClass);
    }

    private static Family getFamilyFor(Class<? extends Node> nodeClass) {
        List<Class<Component>> components = getComponents(nodeClass);
        Family.Builder builder = new Family.Builder();
        for (Class<Component> component : components) {
            builder.all(component);
        }
        return builder.get();
    }

    @SuppressWarnings("unchecked")
    private static List<Class<Component>> getComponents(Class<? extends Node> nodeClass) {
        List<Class<Component>> components = new ArrayList<>();
        Class<?>[] interfaces = nodeClass.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            for (Method method : anInterface.getDeclaredMethods()) {
                if (!isValidMethod(method)) {
                    throw new IllegalArgumentException();
                }
                Class<?> returnType = method.getReturnType();
                Class<Component> componentClass = (Class<Component>) returnType;
                components.add(componentClass);
            }
        }
        return components;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Node> NodeMapper<T> getFor(Class<T> nodeClass) {
        NodeMapper<T> nodeMapper = (NodeMapper<T>) nodeMappers.get(nodeClass);
        if (nodeMapper == null) {
            nodeMapper = new NodeMapper<>(nodeClass);
            nodeMappers.put(nodeClass, nodeMapper);
        }
        return nodeMapper;
    }

    public T get(Entity entity) {
        return ClassReflection.newProxyInstance(nodeClass, new NodeHandler(entity));
    }

    public boolean has(Entity entity) {
        return family.matches(entity);
    }

    private static boolean isValidMethod(Method method) {
        return isComponentMethod(method) || isGetEntityMethod(method) ||
                isEqualsMethod(method) || isHashCodeMethod(method) ||
                isToStringMethod(method);
    }

    private static boolean isComponentMethod(Method method) {
        return Component.class.isAssignableFrom(method.getReturnType());
    }

    private static boolean isGetEntityMethod(Method method) {
        return method.getName().equals("getEntity") &&
                method.getReturnType() == Entity.class;
    }

    private static boolean isEqualsMethod(Method method) {
        return method.getName().equals("equals") &&
                method.getReturnType() == boolean.class;
    }

    private static boolean isHashCodeMethod(Method method) {
        return method.getName().equals("hashCode") &&
                method.getReturnType() == int.class;
    }

    private static boolean isToStringMethod(Method method) {
        return method.getName().equals("toString") &&
                method.getReturnType() == String.class;
    }

    @Override
    public int hashCode() {
        int result = nodeClass != null ? nodeClass.hashCode() : 0;
        result = 31 * result + (family != null ? family.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NodeMapper that = (NodeMapper) o;

        if (family != null ? !family.equals(that.family) : that.family != null) return false;
        return !(nodeClass != null ? !nodeClass.equals(that.nodeClass) : that.nodeClass != null);
    }

    private static final class NodeHandler implements InvocationHandler {

        private final Entity entity;

        public NodeHandler(Entity entity) {
            this.entity = entity;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (isComponentMethod(method)) {
                Class<Component> componentClass = (Class<Component>) method.getReturnType();
                return ComponentMapper.getFor(componentClass).get(entity);
            } else if (isGetEntityMethod(method)) {
                return entity;
            } else if (isHashCodeMethod(method)) {
                return entity.hashCode();
            } else if (isEqualsMethod(method)) {
                return entity.equals(args[0]);
            } else if (isToStringMethod(method)) {
                return entity.toString();
            }

            throw new RuntimeException("Unknown method " + method);
        }
    }
}
