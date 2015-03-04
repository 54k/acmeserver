package com.acme.engine.ecs.utils.reflection;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class Field {

    private final java.lang.reflect.Field field;

    public Field(java.lang.reflect.Field field) {
        this.field = field;
    }

    /**
     * Returns the name of the field.
     */
    public String getName() {
        return field.getName();
    }

    /**
     * Returns a Class object that identifies the declared type for the field.
     */
    public Class getType() {
        return field.getType();
    }

    /**
     * Returns the Class object representing the class or interface that declares the field.
     */
    public Class getDeclaringClass() {
        return field.getDeclaringClass();
    }

    public void setAccessible(boolean accessible) {
        field.setAccessible(accessible);
    }

    /**
     * Return true if the field includes the {@code final} modifier.
     */
    public boolean isFinal() {
        return Modifier.isFinal(field.getModifiers());
    }

    /**
     * If the type of the field is parameterized, returns the Class object representing the parameter type at the specified index,
     * null otherwise.
     */
    public Class getElementType(int index) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            Type[] actualTypes = ((ParameterizedType) genericType).getActualTypeArguments();
            if (actualTypes.length - 1 >= index) {
                Type actualType = actualTypes[index];
                if (actualType instanceof Class) {
                    return (Class) actualType;
                } else if (actualType instanceof ParameterizedType) {
                    return (Class) ((ParameterizedType) actualType).getRawType();
                }
            }
        }
        return null;
    }

    /**
     * Returns the value of the field on the supplied object.
     */
    public Object get(Object obj) throws ReflectionException {
        try {
            return field.get(obj);
        } catch (IllegalArgumentException e) {
            throw new ReflectionException("Object is not an instance of " + getDeclaringClass(), e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException("Illegal access to field: " + getName(), e);
        }
    }

    /**
     * Sets the value of the field on the supplied object.
     */
    public void set(Object obj, Object value) throws ReflectionException {
        try {
            field.set(obj, value);
        } catch (IllegalArgumentException e) {
            throw new ReflectionException("Argument not valid for field: " + getName(), e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException("Illegal access to field: " + getName(), e);
        }
    }

    /**
     * Returns true if the field includes an annotation of the provided class type.
     */
    public boolean isAnnotationPresent(Class<? extends java.lang.annotation.Annotation> annotationType) {
        return field.isAnnotationPresent(annotationType);
    }
}