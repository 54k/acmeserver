package com.acme.commons.collections;

import com.acme.ecs.core.Entity;

public interface Predicate {

    boolean matches(Entity entity);
}
