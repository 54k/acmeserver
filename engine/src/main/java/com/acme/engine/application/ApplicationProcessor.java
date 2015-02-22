package com.acme.engine.application;

import com.acme.engine.ecs.core.Engine;
import com.acme.engine.ecs.core.Processor;
import com.acme.engine.ecs.utils.reflection.ClassReflection;
import com.acme.engine.ecs.utils.reflection.Field;

// TODO this should be deleted
class ApplicationProcessor implements Processor {

    private final Context context;

    public ApplicationProcessor(Context context) {
        this.context = context;
    }

    @Override
    public void processObject(Object object, Engine engine) {
        Class<?> objectClass = object.getClass();
        while (objectClass != null) {
            injectContext(object, objectClass);
            objectClass = objectClass.getSuperclass();
        }
    }

    private void injectContext(Object object, Class<?> objectClass) {
        Field[] fields = ClassReflection.getDeclaredFields(objectClass);
        for (Field field : fields) {
            if (Context.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                field.set(object, context);
            }
        }
    }
}
