package com.acme.engine.aegis;

import com.acme.engine.aegis.Pool.Disposable;
import com.acme.engine.event.EventBus;
import com.acme.engine.event.Listener;
import com.acme.engine.event.Signal;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Engine {

    private static final SystemComparator systemsComparator = new SystemComparator();

    private List<Entity> entities;
    private ImmutableList<Entity> immutableEntities;
    private Map<Long, Entity> entitiesById;

    private List<EntityOperation> entityOperations;
    private EntityOperationPool entityOperationPool;

    private List<EntitySystem> systems;
    private ImmutableList<EntitySystem> immutableSystems;
    private Map<Class<? extends EntitySystem>, EntitySystem> systemsByClass;

    private Map<Family, List<Entity>> families;
    private Map<Family, ImmutableList<Entity>> immutableFamilies;

    private List<EntityListener> listeners;
    private Map<Family, List<EntityListener>> familyListeners;

    private Listener<Entity> componentAdded;
    private Listener<Entity> componentRemoved;

    private boolean updating;
    private boolean notifying;

    /**
     * Mechanism to delay component addition/removal to avoid affecting system processing
     */
    private ComponentOperationPool componentOperationsPool;
    private List<ComponentOperation> componentOperations;
    private ComponentOperationHandler componentOperationHandler;

    private long nextEntityId = 1;

    private Map<Class<?>, Signal<?>> signals;
    private EventBus eventBus = new EventBus();

    private Injector injector;
    private Signal<Void> injectSignal;
    private Map<EntitySystem, Listener<Void>> injectionListeners;
    private List<WiredListener> initListeners = new ArrayList<>(16);
    private boolean initialized;

    public Engine() {
        entities = new ArrayList<>();
        immutableEntities = new ImmutableList<>(entities);
        entitiesById = new HashMap<>();
        entityOperations = new ArrayList<>();
        entityOperationPool = new EntityOperationPool();
        systems = new ArrayList<>(16);
        immutableSystems = new ImmutableList<>(systems);
        systemsByClass = new HashMap<>();
        families = new HashMap<>();
        immutableFamilies = new HashMap<>();
        listeners = new ArrayList<>(16);
        familyListeners = new HashMap<>();

        componentAdded = new ComponentListener(this);
        componentRemoved = new ComponentListener(this);

        updating = false;
        notifying = false;

        componentOperationsPool = new ComponentOperationPool();
        componentOperations = new ArrayList<>();
        componentOperationHandler = new ComponentOperationHandler(this);

        injector = new Injector(this);
    }

    private long obtainEntityId() {
        return nextEntityId++;
    }

    /**
     * Adds an entity to this Engine.
     */
    public void addEntity(Entity entity) {
        entity.uuid = obtainEntityId();
        if (notifying) {
            EntityOperation operation = entityOperationPool.obtain();
            operation.entity = entity;
            operation.type = EntityOperation.Type.Add;
            entityOperations.add(operation);
        } else {
            addEntityInternal(entity);
        }
    }

    /**
     * Removes an entity from this Engine.
     */
    public void removeEntity(Entity entity) {
        if (updating || notifying) {
            if (entity.scheduledForRemoval) {
                return;
            }
            entity.scheduledForRemoval = true;
            EntityOperation operation = entityOperationPool.obtain();
            operation.entity = entity;
            operation.type = EntityOperation.Type.Remove;
            entityOperations.add(operation);
        } else {
            removeEntityInternal(entity);
        }
    }

    /**
     * Removes all entities registered with this Engine.
     */
    public void removeAllEntities() {
        if (updating || notifying) {
            for (Entity entity : entities) {
                entity.scheduledForRemoval = true;
            }
            EntityOperation operation = entityOperationPool.obtain();
            operation.type = EntityOperation.Type.RemoveAll;
            entityOperations.add(operation);
        } else {
            while (entities.size() > 0) {
                removeEntity(entities.get(0));
            }
        }
    }

    public Entity getEntity(long id) {
        return entitiesById.get(id);
    }

    public ImmutableList<Entity> getEntities() {
        return immutableEntities;
    }

    /**
     * Adds the {@link EntitySystem} to this Engine.
     */
    public void addSystem(EntitySystem system) {
        Class<? extends EntitySystem> systemType = system.getClass();

        if (!systemsByClass.containsKey(systemType)) {
            systems.add(system);
            systemsByClass.put(systemType, system);
            system.addedToEngine(this);
            Collections.sort(systems, systemsComparator);
        }
    }

    /**
     * Removes the {@link EntitySystem} from this Engine.
     */
    public void removeSystem(EntitySystem system) {
        if (systems.remove(system)) {
            systemsByClass.remove(system.getClass());
            system.removedFromEngine(this);
        }
    }

    /**
     * Quick {@link EntitySystem} retrieval.
     */
    @SuppressWarnings("unchecked")
    public <T extends EntitySystem> T getSystem(Class<T> systemType) {
        return (T) systemsByClass.get(systemType);
    }

    /**
     * @return immutable array of all entity systems managed by the {@link Engine}.
     */
    public ImmutableList<EntitySystem> getSystems() {
        return immutableSystems;
    }

    /**
     * Returns immutable collection of entities for the specified {@link Family}. Will return the same instance every time.
     */
    public ImmutableList<Entity> getEntitiesFor(Family family) {
        return registerFamily(family);
    }

    /**
     * Adds an {@link EntityListener}.
     * <p/>
     * The listener will be notified every time an entity is added/removed to/from the engine.
     */
    public void addEntityListener(EntityListener listener) {
        listeners.add(listener);
    }

    /**
     * Adds an {@link EntityListener} for a specific {@link Family}.
     * <p/>
     * The listener will be notified every time an entity is added/removed to/from the given family.
     */
    public void addEntityListener(Family family, EntityListener listener) {
        registerFamily(family);
        List<EntityListener> listeners = familyListeners.get(family);

        if (listeners == null) {
            listeners = new ArrayList<>(16);
            familyListeners.put(family, listeners);
        }

        listeners.add(listener);
    }

    /**
     * Removes an {@link EntityListener}
     */
    public void removeEntityListener(EntityListener listener) {
        listeners.remove(listener);

        for (List<EntityListener> familyListenerArray : familyListeners.values()) {
            familyListenerArray.remove(listener);
        }
    }

    /**
     * Removes an {@link EntityListener} for a specific {@link Family}.
     */
    public void removeEntityListener(Family family, EntityListener listener) {
        List<EntityListener> listeners = familyListeners.get(family);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    /**
     * Updates all the systems in this Engine.
     *
     * @param deltaTime The time passed since the last frame.
     */
    public void update(float deltaTime) {
        updating = true;
        for (EntitySystem system : systems) {
            if (system.checkProcessing()) {
                system.update(deltaTime);
            }

            processComponentOperations();
            processPendingEntityOperations();
        }

        updating = false;
    }

    private void updateFamilyMembership(Entity entity) {
        for (Entry<Family, List<Entity>> entry : families.entrySet()) {
            Family family = entry.getKey();
            List<Entity> familyEntities = entry.getValue();
            int familyIndex = family.getIndex();

            boolean belongsToFamily = entity.getFamilyBits().get(familyIndex);
            boolean matches = family.matches(entity);

            if (!belongsToFamily && matches) {
                familyEntities.add(entity);
                entity.getFamilyBits().set(familyIndex);

                notifyFamilyListenersAdd(family, entity);
            } else if (belongsToFamily && !matches) {
                familyEntities.remove(entity);
                entity.getFamilyBits().clear(familyIndex);

                notifyFamilyListenersRemove(family, entity);
            }
        }
    }

    protected void removeEntityInternal(Entity entity) {
        entity.scheduledForRemoval = false;
        entities.remove(entity);
        entitiesById.remove(entity.getId());

        if (!entity.getFamilyBits().isEmpty()) {
            for (Entry<Family, List<Entity>> entry : families.entrySet()) {
                Family family = entry.getKey();
                List<Entity> familyEntities = entry.getValue();

                if (family.matches(entity)) {
                    familyEntities.remove(entity);
                    entity.getFamilyBits().clear(family.getIndex());
                    notifyFamilyListenersRemove(family, entity);
                }
            }
        }

        entity.componentAdded.remove(componentAdded);
        entity.componentRemoved.remove(componentRemoved);
        entity.componentOperationHandler = null;

        notifying = true;
        for (EntityListener listener : new ArrayList<>(listeners)) {
            listener.entityRemoved(entity);
        }
        notifying = false;
    }

    protected void addEntityInternal(Entity entity) {
        entities.add(entity);
        entitiesById.put(entity.getId(), entity);

        updateFamilyMembership(entity);

        entity.componentAdded.add(componentAdded);
        entity.componentRemoved.add(componentRemoved);
        entity.componentOperationHandler = componentOperationHandler;

        notifying = true;
        for (EntityListener listener : new ArrayList<>(listeners)) {
            listener.entityAdded(entity);
        }
        notifying = false;
    }

    private void notifyFamilyListenersAdd(Family family, Entity entity) {
        List<EntityListener> listeners = familyListeners.get(family);

        if (listeners != null) {
            notifying = true;
            for (EntityListener listener : new ArrayList<>(listeners)) {
                listener.entityAdded(entity);
            }
            notifying = false;
        }
    }

    private void notifyFamilyListenersRemove(Family family, Entity entity) {
        List<EntityListener> listeners = familyListeners.get(family);

        if (listeners != null) {
            notifying = true;
            for (EntityListener listener : new ArrayList<>(listeners)) {
                listener.entityRemoved(entity);
            }
            notifying = false;
        }
    }

    private ImmutableList<Entity> registerFamily(Family family) {
        ImmutableList<Entity> immutableEntities = immutableFamilies.get(family);

        if (immutableEntities == null) {
            List<Entity> familyEntities = new ArrayList<>(16);
            immutableEntities = new ImmutableList<>(familyEntities);
            families.put(family, familyEntities);
            immutableFamilies.put(family, immutableEntities);

            for (Entity e : this.entities) {
                if (family.matches(e)) {
                    familyEntities.add(e);
                    e.getFamilyBits().set(family.getIndex());
                }
            }
        }

        return immutableEntities;
    }

    private void processPendingEntityOperations() {
        while (entityOperations.size() > 0) {
            EntityOperation operation = entityOperations.remove(entityOperations.size() - 1);

            switch (operation.type) {
                case Add:
                    addEntityInternal(operation.entity);
                    break;
                case Remove:
                    removeEntityInternal(operation.entity);
                    break;
                case RemoveAll:
                    while (entities.size() > 0) {
                        removeEntityInternal(entities.get(0));
                    }
                    break;
            }

            entityOperationPool.free(operation);
        }

        entityOperations.clear();
    }

    private void processComponentOperations() {
        for (ComponentOperation operation : componentOperations) {
            switch (operation.type) {
                case Add:
                    operation.entity.addInternal(operation.component);
                    break;
                case Remove:
                    operation.entity.removeInternal(operation.componentClass);
                    break;
            }

            componentOperationsPool.free(operation);
        }

        componentOperations.clear();
    }

    private static class ComponentListener implements Listener<Entity> {
        private Engine engine;

        public ComponentListener(Engine engine) {
            this.engine = engine;
        }

        @Override
        public void receive(Signal<Entity> signal, Entity object) {
            engine.updateFamilyMembership(object);
        }
    }

    static class ComponentOperationHandler {
        private Engine engine;

        public ComponentOperationHandler(Engine engine) {
            this.engine = engine;
        }

        public void add(Entity entity, Component component) {
            if (engine.updating) {
                ComponentOperation operation = engine.componentOperationsPool.obtain();
                operation.makeAdd(entity, component);
                engine.componentOperations.add(operation);
            } else {
                entity.addInternal(component);
            }
        }

        public void remove(Entity entity, Class<? extends Component> componentClass) {
            if (engine.updating) {
                ComponentOperation operation = engine.componentOperationsPool.obtain();
                operation.makeRemove(entity, componentClass);
                engine.componentOperations.add(operation);
            } else {
                entity.removeInternal(componentClass);
            }
        }
    }

    private static class ComponentOperation implements Disposable {
        public enum Type {
            Add,
            Remove,
        }

        public Type type;
        public Entity entity;
        public Component component;
        public Class<? extends Component> componentClass;

        public void makeAdd(Entity entity, Component component) {
            this.type = Type.Add;
            this.entity = entity;
            this.component = component;
            this.componentClass = null;
        }

        public void makeRemove(Entity entity, Class<? extends Component> componentClass) {
            this.type = Type.Remove;
            this.entity = entity;
            this.component = null;
            this.componentClass = componentClass;
        }

        @Override
        public void dispose() {
            entity = null;
            component = null;
        }
    }

    private static class ComponentOperationPool extends Pool<ComponentOperation> {
        @Override
        protected ComponentOperation newObject() {
            return new ComponentOperation();
        }
    }

    private static class SystemComparator implements Comparator<EntitySystem> {
        @Override
        public int compare(EntitySystem a, EntitySystem b) {
            return a.priority > b.priority ? 1 : (a.priority == b.priority) ? 0 : -1;
        }
    }

    private static class EntityOperation implements Disposable {
        public enum Type {
            Add,
            Remove,
            RemoveAll
        }

        public Type type;
        public Entity entity;

        @Override
        public void dispose() {
            entity = null;
        }
    }

    private static class EntityOperationPool extends Pool<EntityOperation> {
        @Override
        protected EntityOperation newObject() {
            return new EntityOperation();
        }
    }

    private static final class Injector {

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
}
