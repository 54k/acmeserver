package com.acme.engine.processors;

import com.acme.engine.aegis.Component;
import com.acme.engine.aegis.ComponentMapper;
import com.acme.engine.aegis.Engine;
import com.acme.engine.aegis.EntitySystem;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Predicate;
import java.util.stream.Stream;

class Injector {

    //        private final Context context;
    private Engine engine;

    Injector(/*Context context, */Engine engine) {
        //            this.context = context;
        this.engine = engine;
    }

    public void wireObject(Object o) {
        getFieldsForWire(o).forEach(field -> {
            Object value = getComponentForField(field);
            if (value != null) {
                setFieldValue(o, field, value);
            }
        });
    }

    private Object getComponentForField(Field field) {
        Class type = field.getType();
        if (EntitySystem.class.isAssignableFrom(type)) {
            return engine.getSystem(type);
        } else if (ComponentMapper.class.isAssignableFrom(type)) {
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                Type componentType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
                //noinspection unchecked
                return ComponentMapper.getFor((Class<? extends Component>) componentType);
            }
        } else if (Engine.class.isAssignableFrom(type)) {
            return engine;
        }// else if (Context.class.isAssignableFrom(type)) {
        //                return context;
        //            }
        //            Wired wired = field.getAnnotation(Wired.class);
        //            if (wired != null) {
        //                return context.get(type, wired.name());
        //            }
        //            return context.get(type);
        return null;
    }

    public void cleanObject(Object o) {
        getFieldsForWire(o)
                .forEach(field -> setFieldValue(o, field, null));
    }

    private static Stream<Field> getFieldsForWire(Object o) {
        Class<?> c = o.getClass();
        return Stream.of(c.getDeclaredFields()).filter(getFieldsFilter(c));
    }

    private static Predicate<? super Field> getFieldsFilter(Class<?> o) {
        if (o.isAnnotationPresent(Wired.class)) {
            return Injector::isNotFinal;
        } else {
            return f -> f.isAnnotationPresent(Wired.class) && isNotFinal(f);
        }
    }

    private static boolean isNotFinal(Field f) {
        return !Modifier.isFinal(f.getModifiers());
    }

    private static void setFieldValue(Object o, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(o, value);
        } catch (IllegalAccessException ignore) {
        }
    }
}
