package com.acme.engine.ecs.utils.reflection;

/**
 * Utilities for Class reflection.
 */
public final class ClassReflection {

    /**
     * Creates a new instance of the class represented by the supplied Class.
     */
    public static <T> T newInstance(Class<T> c) throws ReflectionException {
        try {
            return c.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ReflectionException("Could not instantiate instance of class: " + c.getName(), e);
        }
    }

    /**
     * Returns an array of {@link Field} objects reflecting all the fields declared by the supplied class.
     */
    public static Field[] getDeclaredFields(Class c) {
        java.lang.reflect.Field[] fields = c.getDeclaredFields();
        Field[] result = new Field[fields.length];
        for (int i = 0, j = fields.length; i < j; i++) {
            result[i] = new Field(fields[i]);
        }
        return result;
    }

    /**
     * Returns true if the supplied class includes an annotation of the given class type.
     */
    public static boolean isAnnotationPresent(Class c, Class<? extends java.lang.annotation.Annotation> annotationType) {
        return c.isAnnotationPresent(annotationType);
    }
}
