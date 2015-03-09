package com.acme.ecs.utils.reflection;

/**
 * Thrown when an exception occurs during reflection.
 *
 * @author nexsoftware
 */
public class ReflectionException extends RuntimeException {

    public ReflectionException() {
        super();
    }

    public ReflectionException(String message) {
        super(message);
    }

    public ReflectionException(Throwable cause) {
        super(cause);
    }

    public ReflectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
