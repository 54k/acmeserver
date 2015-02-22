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
        Class<?> objectClass = object.getClass();

        if (!objectClass.isAnnotationPresent(Wire.class)) {
            return;
        }

        while (objectClass != null) {
            Class<?>[] interfaces = objectClass.getInterfaces();
            for (Class<?> i : interfaces) {
                if (EventListener.class.isAssignableFrom(i)) {
                    Class<EventListener> listenerClass = (Class<EventListener>) i;
                    engine.event(listenerClass).add((EventListener) object);
                }
            }
            objectClass = objectClass.getSuperclass();
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
            if ((allMatch || field.isAnnotationPresent(Wire.class)) && !field.isFinal()) {
                matchingFields.add(field);
            }
        }
        return matchingFields;
    }

    private void bindField(Field field, Object object, Engine engine) {
        field.setAccessible(true);
        Object bindValue = getBindValue(field, engine);
        if (bindValue != null) {
            field.set(object, bindValue);
        }
    }

    @SuppressWarnings("unchecked")
    private Object getBindValue(Field field, Engine engine) {
        Class type = field.getType();
        if (Engine.class.isAssignableFrom(type)) {
            return engine;
        } else if (EntitySystem.class.isAssignableFrom(type)) {
            EntitySystem system = engine.getSystem(type);
            if (system == null) {
                throw new NullPointerException("Cannot bind system type " + type.getName() + " for field " + field.getName());
            }
            return system;
        } else if (ComponentMapper.class.isAssignableFrom(type)) {
            Class<? extends Component> elementType = (Class<? extends Component>) field.getElementType(0);
            return ComponentMapper.getFor(elementType);
        }
        return null;
    }
}
