package com.acme.commons.utils.collections;

import com.acme.ecs.core.Aspect;
import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Node;
import com.acme.ecs.core.NodeFamily;

public final class Predicates {

    private static final Predicate all = new Predicate() {
        @Override
        public boolean matches(Entity entity) {
            return true;
        }
    };

    private Predicates() {
    }

    public static Predicate id(long id) {
        return new IdPredicate(id);
    }

    public static Predicate all(Predicate... predicates) {
        if (predicates.length == 0) {
            return all;
        } else if (predicates.length == 1) {
            return predicates[0];
        }
        return new AllPredicate(predicates);
    }

    public static Predicate not(Predicate predicate) {
        return new NotPredicate(predicate);
    }

    public static Predicate aspect(Aspect aspect) {
        return new AspectPredicate(aspect);
    }

    public static Predicate node(Class<? extends Node> nodeClass) {
        return new NodePredicate(NodeFamily.getFor(nodeClass));
    }

    private static final class AllPredicate implements Predicate {

        private final Predicate[] predicates;

        public AllPredicate(Predicate[] predicates) {
            this.predicates = predicates;
        }

        @Override
        public boolean matches(Entity entity) {
            for (Predicate predicate : predicates) {
                if (!predicate.matches(entity)) {
                    return false;
                }
            }
            return true;
        }
    }

    private static final class NotPredicate implements Predicate {

        private final Predicate predicate;

        public NotPredicate(Predicate predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean matches(Entity entity) {
            return !predicate.matches(entity);
        }
    }

    private static final class IdPredicate implements Predicate {

        private final long id;

        public IdPredicate(long id) {
            this.id = id;
        }

        @Override
        public boolean matches(Entity entity) {
            return entity.getId() == id;
        }
    }

    private static final class AspectPredicate implements Predicate {

        private final Aspect aspect;

        public AspectPredicate(Aspect aspect) {
            this.aspect = aspect;
        }

        @Override
        public boolean matches(Entity entity) {
            return aspect.matches(entity);
        }
    }

    private static final class NodePredicate implements Predicate {

        private final NodeFamily<?> family;

        public NodePredicate(NodeFamily<?> family) {
            this.family = family;
        }

        @Override
        public boolean matches(Entity entity) {
            return family.matches(entity);
        }
    }
}
