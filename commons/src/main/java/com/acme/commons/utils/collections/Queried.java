package com.acme.commons.utils.collections;

import java.util.Collection;

public interface Queried<T> extends Collection<T> {

    /**
     * Queries and retrieves an entities, matched by the given predicate.
     *
     * @param predicate predicate
     * @return queried object with matched entities
     */
    Queried<T> query(Predicate predicate);

    /**
     * Queries and retrieves a single entity result, matched by the given predicate.
     *
     * @param predicate predicate
     * @return single result or null
     */
    T querySingle(Predicate predicate);
}
