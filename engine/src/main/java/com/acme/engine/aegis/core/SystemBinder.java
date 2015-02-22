package com.acme.engine.aegis.core;

import com.acme.engine.aegis.utils.ImmutableList;
import com.acme.engine.aegis.utils.reflection.ClassReflection;
import com.acme.engine.aegis.utils.reflection.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class SystemBinder implements Processor {

    @Override
    public void processSystems(ImmutableList<EntitySystem> systems, Engine engine) {
        for (EntitySystem system : systems) {
            processSystem(system, engine);
        }
    }

    private void processSystem(EntitySystem system, Engine engine) {
        Iterable<Field> fields = getFieldsForBinding(system);
        for (Field field : fields) {
            bindField(field, system, engine);
        }
    }

    @SuppressWarnings("unchecked")
    private Iterable<Field> getFieldsForBinding(EntitySystem system) {
        Class<?> systemClass = system.getClass();
        List<Field> matchingFields = new ArrayList<>();
        while (systemClass != null && EntitySystem.class.isAssignableFrom(systemClass)) {
            matchingFields.addAll(getFields((Class<? extends EntitySystem>) systemClass));
            systemClass = systemClass.getSuperclass();
        }
        return matchingFields;
    }

    private Collection<Field> getFields(Class<? extends EntitySystem> systemClass) {
        boolean allMatch = ClassReflection.isAnnotationPresent(systemClass, Wired.class);
        Field[] fields = ClassReflection.getDeclaredFields(systemClass);
        List<Field> matchingFields = new ArrayList<>();
        for (Field field : fields) {
            if (allMatch || field.isAnnotationPresent(Wired.class)) {
                matchingFields.add(field);
            }
        }
        return matchingFields;
    }

    private void bindField(Field field, EntitySystem system, Engine engine) {
        field.setAccessible(true);
        field.set(system, getBindValue(field, engine));
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
            throw new NullPointerException();
        }
        return value;
    }
}
