package com.acme.commons.utils.collections;

import com.acme.ecs.core.Entity;

public interface Predicate {

    boolean matches(Entity entity);
}
