package com.acme.engine.ecs.core;

import com.acme.engine.ecs.events.EventListener;
import com.acme.engine.ecs.utils.reflection.ClassReflection;
import com.acme.engine.ecs.utils.reflection.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class EngineProcessor implements Processor {

    @Override
    public void processObject(Object object, Engine engine) {
        bindEventListener(object, engine);
        bindSystem(object, engine);
    }

    @SuppressWarnings("unchecked")
    private void bindEventListener(Object object, Engine engine) {
        Class<?> systemClass = object.getClass();
        while (systemClass != null) {
            Class<?>[] interfaces = systemClass.getInterfaces();
            for (Class<?> i : interfaces) {
                if (EventListener.class.isAssignableFrom(i)) {
                    Class<EventListener> listenerClass = (Class<EventListener>) i;
                    engine.event(listenerClass).add((EventListener) object);
                }
            }
            systemClass = systemClass.getSuperclass();
        }
    }

    private void bindSystem(Object object, Engine engine) {
        Iterable<Field> fields = getFieldsForBinding(object);
        for (Field field : fields) {
            bindField(field, object, engine);
        }
    }

    @SuppressWarnings("unchecked")
    private Iterable<Field> getFieldsForBinding(Object object) {
        List<Field> matchingFields = new ArrayList<>();
        Class<?> objectClass = object.getClass();
        while (objectClass != null) {
            matchingFields.addAll(getFields(objectClass));
            objectClass = objectClass.getSuperclass();
        }
        return matchingFields;
    }

    private Collection<Field> getFields(Class<?> objectClass) {
        boolean allMatch = ClassReflection.isAnnotationPresent(objectClass, Wire.class);
        Field[] fields = ClassReflection.getDeclaredFields(objectClass);
        List<Field> matchingFields = new ArrayList<>();
        for (Field field : fields) {
            if (allMatch || field.isAnnotationPresent(Wire.class)) {
                matchingFields.add(field);
            }
        }
        return matchingFields;
    }

    private void bindField(Field field, Object object, Engine engine) {
        field.setAccessible(true);
        field.set(object, getBindValue(field, engine));
    }

    @SuppressWarnings("unchecked")
    private Object getBindValue(Field field, Engine engine) {
        Object value = null;
        Class type = field.getType();
        if (Engine.class.isAssignableFrom(type)) {
            value = engine;
        } else if (EntitySystem.class.isAssignableFrom(type)) {
            value = engine.getSystem(type);
        } else if (ComponentMapper.class.isAssignableFrom(type)) {
            Class<? extends Component> elementType = (Class<? extends Component>) field.getElementType(0);
            value = ComponentMapper.getFor(elementType);
        }
        if (value == null) {
            throw new NullPointerException("Cannot bind value type " + type.getName() + " for field " + field.getName());
        }
        return value;
    }
}
