package com.acme.engine.ashley;

import com.acme.engine.application.Context;
import com.acme.engine.event.Event;
import com.acme.engine.event.EventBus;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.utils.ImmutableArray;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class EntityEngine extends Engine {

    private final Signal<Void> injectSignal = new Signal<>();
    private final Map<EntitySystem, Listener<Void>> injectionListeners = new HashMap<>();

    private final Injector injector;
    private final Engine engine;

    private final EventBus eventBus = new EventBus();
    private final Set<WiredListener> initListeners = new LinkedHashSet<>();
    private boolean initialized;

    public EntityEngine(Context context, Engine engine) {
        this.engine = engine;
        injector = new Injector(context, this);
    }

    @Override
    public void addEntity(Entity entity) {
        engine.addEntity(entity);
    }

    @Override
    public void removeEntity(Entity entity) {
        engine.removeEntity(entity);
    }

    @Override
    public void removeAllEntities() {
        engine.removeAllEntities();
    }

    @Override
    public void addSystem(EntitySystem system) {
        engine.addSystem(system);
        Listener<Void> listener = ($1, $2) -> wireObject(system);
        injectionListeners.put(system, listener);
        injectSignal.add(listener);
        eventBus.register(system);
        tryRegisterEngineListener(system);

        if (initialized) {
            injectSignal.dispatch(null);
            if (system instanceof WiredListener) {
                ((WiredListener) system).wired();
            }
        }
    }

    private void tryRegisterEngineListener(EntitySystem system) {
        if (system instanceof WiredListener) {
            WiredListener listener = (WiredListener) system;
            initListeners.add(listener);
        }
        if (system instanceof EntityEngineListener) {
            ((EntityEngineListener) system).addedToEngine(this);
        }
    }

    @Override
    public void removeSystem(EntitySystem system) {
        engine.removeSystem(system);
        cleanObject(system);
        Listener<Void> listener = injectionListeners.get(system);
        injectSignal.remove(listener);
        eventBus.unregister(system);
        tryUnregisterEngineListener(system);

        if (initialized) {
            injectSignal.dispatch(null);
        }
    }

    private void tryUnregisterEngineListener(EntitySystem system) {
        if (system instanceof WiredListener) {
            WiredListener listener = (WiredListener) system;
            initListeners.remove(listener);
        }
        if (system instanceof EntityEngineListener) {
            ((EntityEngineListener) system).removedFromEngine(this);
        }
    }

    @Override
    public <T extends EntitySystem> T getSystem(Class<T> systemType) {
        return engine.getSystem(systemType);
    }

    @Override
    public ImmutableArray<EntitySystem> getSystems() {
        return engine.getSystems();
    }

    @Override
    public ImmutableArray<Entity> getEntitiesFor(Family family) {
        return engine.getEntitiesFor(family);
    }

    @Override
    public void addEntityListener(EntityListener listener) {
        engine.addEntityListener(listener);
    }

    @Override
    public void addEntityListener(Family family, EntityListener listener) {
        engine.addEntityListener(family, listener);
    }

    @Override
    public void removeEntityListener(EntityListener listener) {
        engine.removeEntityListener(listener);
    }

    @Override
    public void update(float deltaTime) {
        initialize();
        engine.update(deltaTime);
    }

    public void initialize() {
        if (!initialized) {
            initialized = true;
            injectSignal.dispatch(null);
            initListeners.forEach(WiredListener::wired);
            initListeners.clear();
        }
    }

    public void wireObject(Object o) {
        injector.wireObject(o);
    }

    public void cleanObject(Object o) {
        injector.cleanObject(o);
    }

    public <T extends Event> T post(Class<T> type) {
        return eventBus.post(type);
    }

    public void register(Object listener) {
        eventBus.register(listener);
    }

    public <T extends Event> void register(Class<T> type, T listener) {
        eventBus.register(type, listener);
    }

    public void unregister(Object listener) {
        eventBus.unregister(listener);
    }

    public <T extends Event> void unregister(Class<T> type, T listener) {
        eventBus.unregister(type, listener);
    }

    public <T extends Engine> T unwrap(Class<T> engineClass) {
        return engineClass.cast(engine);
    }

    private static final class Injector {

        private final Context context;
        private final Engine engine;

        Injector(Context context, Engine engine) {
            this.context = context;
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
            } else if (Context.class.isAssignableFrom(type)) {
                return context;
            }
            Wired wired = field.getAnnotation(Wired.class);
            if (wired != null) {
                return context.get(type, wired.name());
            }
            return context.get(type);
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
}
