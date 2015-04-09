package com.acme.ecs.core;

import com.acme.ecs.events.Event;
import com.acme.ecs.events.EventListener;
import com.acme.ecs.events.Signal;
import com.acme.ecs.utils.ImmutableList;
import com.acme.ecs.utils.Pool;
import com.acme.ecs.utils.Pool.Disposable;

import java.util.*;
import java.util.Map.Entry;

public class Engine {

    private static final SystemComparator systemsComparator = new SystemComparator();
    private static final Processor engineProcessor = new EngineProcessor();

    private List<Entity> entities;
    private ImmutableList<Entity> immutableEntities;
    private Map<Long, Entity> entitiesById;

    private Queue<EntityOperation> entityOperations;
    private EntityOperationPool entityOperationPool;

    private List<EntitySystem> systems;
    private ImmutableList<EntitySystem> immutableSystems;
    private Map<Class<? extends EntitySystem>, EntitySystem> systemsByClass;

    private Map<Aspect, List<Entity>> aspects;
    private Map<Aspect, ImmutableList<Entity>> immutableAspects;

    private List<EntityListener> entityListeners;
    private Map<Aspect, List<EntityListener>> aspectListeners;

    private Map<NodeFamily, List<Node>> nodes;
    private Map<NodeFamily, ImmutableList<Node>> immutableNodes;
    private Map<NodeFamily, List<NodeListener>> nodeListeners;

    private ComponentListener componentListener;

    private boolean updating;
    private boolean notifying;

    /**
     * Mechanism to delay component addition/removal to avoid affecting system processing
     */
    private ComponentOperationPool componentOperationsPool;
    private Queue<ComponentOperation> componentOperations;
    private ComponentOperationHandler componentOperationHandler;

    private long nextEntityId = 1;

    private Map<Class<?>, Signal<?>> signals;
    private Map<Class<? extends EventListener>, Event<? extends EventListener>> events;

    private List<Processor> processors;

    private boolean initialized;

    public Engine() {
        entities = new ArrayList<>();
        immutableEntities = new ImmutableList<>(entities);
        entitiesById = new HashMap<>();
        entityOperations = new LinkedList<>();
        entityOperationPool = new EntityOperationPool();

        systems = new ArrayList<>(16);
        immutableSystems = new ImmutableList<>(systems);
        systemsByClass = new HashMap<>();

        aspects = new HashMap<>();
        immutableAspects = new HashMap<>();
        entityListeners = new ArrayList<>(16);
        aspectListeners = new HashMap<>();

        nodes = new HashMap<>();
        immutableNodes = new HashMap<>();
        nodeListeners = new HashMap<>();

        componentListener = new MembershipUpdater(this);

        updating = false;
        notifying = false;

        componentOperationsPool = new ComponentOperationPool();
        componentOperations = new LinkedList<>();
        componentOperationHandler = new ComponentOperationHandler(this);

        signals = new HashMap<>();
        events = new HashMap<>();

        processors = new ArrayList<>();
        processors.add(engineProcessor);

        initialized = false;
    }

    private long obtainEntityId() {
        return nextEntityId++;
    }

    /**
     * Processes object with registered {@link Processor}s
     */
    public void processObject(Object object) {
        for (Processor processor : processors) {
            processor.processObject(object, this);
        }
    }

    /**
     * Adds an entities to this Engine
     */
    public void addEntity(Entity entity) {
        entity.id = obtainEntityId();
        if (updating || notifying) {
            EntityOperation operation = entityOperationPool.obtain();
            operation.entity = entity;
            operation.type = EntityOperation.Type.Add;
            entityOperations.add(operation);
        } else {
            addEntityInternal(entity);
        }
    }

    /**
     * Removes an entities from this Engine
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
     * Removes all entities registered with this Engine
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
     * Adds the {@link EntitySystem} to this Engine
     *
     * @throws java.lang.IllegalStateException if engine has been initialized
     */
    public void addSystem(EntitySystem system) {
        checkInitialized();
        Class<? extends EntitySystem> systemType = system.getClass();
        if (!systemsByClass.containsKey(systemType)) {
            systems.add(system);
            systemsByClass.put(systemType, system);
            system.engine = this;
            system.addedToEngine(this);
            Collections.sort(systems, systemsComparator);
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
     * @return immutable array of all entities systems managed by the {@link Engine}
     */
    public ImmutableList<EntitySystem> getSystems() {
        return immutableSystems;
    }

    /**
     * Returns immutable collection of entities for the specified {@link Aspect}. Will return the same instance every time
     */
    public ImmutableList<Entity> getEntitiesFor(Aspect aspect) {
        return registerFamily(aspect);
    }

    public <T extends Node> ImmutableList<T> getNodesFor(Class<T> nodeClass) {
        return registerNode(NodeFamily.getFor(nodeClass));
    }

    /**
     * Adds an {@link EntityListener}
     * <p>
     * The listener will be notified every time an entities is added/removed to/from the engine
     */
    public void addEntityListener(EntityListener listener) {
        entityListeners.add(listener);
    }

    /**
     * Adds an {@link EntityListener} for a specific {@link Aspect}
     * <p>
     * The listener will be notified every time an entities is added/removed to/from the given family
     */
    public void addEntityListener(Aspect aspect, EntityListener listener) {
        registerFamily(aspect);
        List<EntityListener> listeners = aspectListeners.get(aspect);

        if (listeners == null) {
            listeners = new ArrayList<>(16);
            aspectListeners.put(aspect, listeners);
        }

        listeners.add(listener);
    }

    /**
     * Removes an {@link EntityListener}
     */
    public void removeEntityListener(EntityListener listener) {
        entityListeners.remove(listener);
        for (List<EntityListener> familyListenerArray : aspectListeners.values()) {
            familyListenerArray.remove(listener);
        }
    }

    public void addNodeListener(Class<? extends Node> node, NodeListener listener) {
        NodeFamily nodeFamily = NodeFamily.getFor(node);
        registerNode(nodeFamily);
        List<NodeListener> listeners = nodeListeners.get(nodeFamily);

        if (listeners == null) {
            listeners = new ArrayList<>(16);
            nodeListeners.put(nodeFamily, listeners);
        }

        listeners.add(listener);
    }

    public void removeNodeListener(NodeListener listener) {
        for (List<NodeListener> listeners : nodeListeners.values()) {
            listeners.remove(listener);
        }
    }

    /**
     * Adds the {@link Processor} to this Engine
     *
     * @throws java.lang.IllegalStateException if engine has been initialized
     */
    public void addProcessor(Processor processor) {
        checkInitialized();
        processors.add(processor);
    }

    private void checkInitialized() {
        if (initialized) {
            throw new IllegalStateException("Engine has been initialized");
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Signal<T> signal(Class<T> type) {
        Signal<?> signal = signals.get(type);
        if (signal == null) {
            signal = new Signal<>();
            signals.put(type, signal);
        }
        return (Signal<T>) signal;
    }

    @SuppressWarnings("unchecked")
    public <T extends EventListener> Event<T> event(Class<T> listenerType) {
        Event<? extends EventListener> event = events.get(listenerType);
        if (event == null) {
            event = new Event<>(listenerType);
            events.put(listenerType, event);
        }
        return (Event<T>) event;
    }

    /**
     * Updates all the systems in this Engine
     *
     * @param deltaTime The time passed since the last frame
     */
    public void update(float deltaTime) {
        initialize();
        updating = true;
        for (EntitySystem system : systems) {
            if (system.isEnabled()) {
                system.update(deltaTime);
            }
            processComponentOperations();
            processPendingEntityOperations();
        }
        updating = false;
    }

    /**
     * Initializes this Engine
     */
    public void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        processSystems(immutableSystems);
        for (EntitySystem system : systems) {
            system.initialized();
        }
    }

    private void processSystems(ImmutableList<EntitySystem> systems) {
        for (Processor processor : processors) {
            for (EntitySystem system : systems) {
                processor.processObject(system, this);
            }
        }
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
                case RemoveAll:
                    operation.entity.removeAllInternal();
                    break;
            }
            componentOperationsPool.free(operation);
        }
        componentOperations.clear();
    }

    private void processPendingEntityOperations() {
        while (!entityOperations.isEmpty()) {
            EntityOperation operation = entityOperations.poll();
            Entity entity = operation.entity;

            switch (operation.type) {
                case Add:
                    addEntityInternal(entity);
                    break;
                case Remove:
                    removeEntityInternal(entity);
                    break;
                case RemoveAll:
                    while (entities.size() > 0) {
                        removeEntityInternal(entities.get(0));
                    }
                    break;
            }
            entityOperationPool.free(operation);
        }
    }

    protected void addEntityInternal(Entity entity) {
        entities.add(entity);
        entitiesById.put(entity.getId(), entity);
        updateMembership(entity);

        entity.addComponentListener(componentListener);
        entity.componentOperationHandler = componentOperationHandler;

        notifying = true;
        for (EntityListener listener : new ArrayList<>(entityListeners)) {
            listener.entityAdded(entity);
        }
        notifying = false;
    }

    protected void removeEntityInternal(Entity entity) {
        entity.scheduledForRemoval = false;
        entities.remove(entity);
        entitiesById.remove(entity.getId());

        if (!entity.getFamilyBits().isEmpty()) {
            for (Entry<Aspect, List<Entity>> entry : aspects.entrySet()) {
                Aspect aspect = entry.getKey();
                List<Entity> familyEntities = entry.getValue();

                if (aspect.matches(entity)) {
                    familyEntities.remove(entity);
                    entity.getFamilyBits().clear(aspect.getIndex());
                    notifyFamilyListenersRemove(aspect, entity);
                }
            }
        }

        if (!entity.getNodeBits().isEmpty()) {
            for (Entry<NodeFamily, List<Node>> entry : nodes.entrySet()) {
                NodeFamily nodeFamily = entry.getKey();
                List<Node> nodeEntities = entry.getValue();

                if (nodeFamily.matches(entity)) {
                    for (int i = nodeEntities.size() - 1; i >= 0; i--) {
                        Node node = nodeEntities.get(i);
                        if (node.getEntity() == entity) {
                            nodeEntities.remove(i);
                            entity.getNodeBits().clear(nodeFamily.getIndex());
                            notifyNodeListenersRemove(nodeFamily, node);
                            break;
                        }
                    }
                }
            }
        }

        entity.removeComponentListener(componentListener);
        entity.componentOperationHandler = null;

        notifying = true;
        for (EntityListener listener : new ArrayList<>(entityListeners)) {
            listener.entityRemoved(entity);
        }
        notifying = false;
    }

    private void updateMembership(Entity entity) {
        updateFamilyMembership(entity);
        updateNodeMembership(entity);
    }

    private void updateFamilyMembership(Entity entity) {
        for (Entry<Aspect, List<Entity>> entry : aspects.entrySet()) {
            Aspect aspect = entry.getKey();
            List<Entity> familyEntities = entry.getValue();
            int familyIndex = aspect.getIndex();

            boolean belongsToFamily = entity.getFamilyBits().get(familyIndex);
            boolean matches = aspect.matches(entity);

            if (!belongsToFamily && matches) {
                familyEntities.add(entity);
                entity.getFamilyBits().set(familyIndex);

                notifyFamilyListenersAdd(aspect, entity);
            } else if (belongsToFamily && !matches) {
                familyEntities.remove(entity);
                entity.getFamilyBits().clear(familyIndex);

                notifyFamilyListenersRemove(aspect, entity);
            }
        }
    }

    private void notifyFamilyListenersAdd(Aspect aspect, Entity entity) {
        List<EntityListener> listeners = aspectListeners.get(aspect);

        if (listeners != null) {
            notifying = true;
            for (EntityListener listener : new ArrayList<>(listeners)) {
                listener.entityAdded(entity);
            }
            notifying = false;
        }
    }

    private void notifyFamilyListenersRemove(Aspect aspect, Entity entity) {
        List<EntityListener> listeners = aspectListeners.get(aspect);

        if (listeners != null) {
            notifying = true;
            for (EntityListener listener : new ArrayList<>(listeners)) {
                listener.entityRemoved(entity);
            }
            notifying = false;
        }
    }

    private void updateNodeMembership(Entity entity) {
        for (Entry<NodeFamily, List<Node>> entry : nodes.entrySet()) {
            NodeFamily nodeFamily = entry.getKey();
            List<Node> nodeEntities = entry.getValue();
            int nodeIndex = nodeFamily.getIndex();

            boolean belongsToNode = entity.getNodeBits().get(nodeIndex);
            boolean matches = nodeFamily.matches(entity);

            if (!belongsToNode && matches) {
                Node node = nodeFamily.get(entity);
                nodeEntities.add(node);
                entity.getNodeBits().set(nodeIndex);
                notifyNodeListenersAdd(nodeFamily, node);
            } else if (belongsToNode && !matches) {
                for (int i = nodeEntities.size() - 1; i >= 0; i--) {
                    Node node = nodeEntities.get(i);
                    if (node.getEntity() == entity) {
                        nodeEntities.remove(i);
                        entity.getNodeBits().clear(nodeIndex);
                        notifyNodeListenersRemove(nodeFamily, node);
                        break;
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void notifyNodeListenersAdd(NodeFamily nodeFamily, Node node) {
        List<NodeListener> listeners = nodeListeners.get(nodeFamily);

        if (listeners != null) {
            notifying = true;
            for (NodeListener listener : new ArrayList<>(listeners)) {
                listener.nodeAdded(node);
            }
            notifying = false;
        }
    }

    @SuppressWarnings("unchecked")
    private void notifyNodeListenersRemove(NodeFamily nodeFamily, Node node) {
        List<NodeListener> listeners = nodeListeners.get(nodeFamily);

        if (listeners != null) {
            notifying = true;
            for (NodeListener listener : new ArrayList<>(listeners)) {
                listener.nodeRemoved(node);
            }
            notifying = false;
        }
    }

    private ImmutableList<Entity> registerFamily(Aspect aspect) {
        ImmutableList<Entity> immutableEntities = immutableAspects.get(aspect);

        if (immutableEntities == null) {
            List<Entity> familyEntities = new ArrayList<>(16);
            immutableEntities = new ImmutableList<>(familyEntities);
            aspects.put(aspect, familyEntities);
            immutableAspects.put(aspect, immutableEntities);

            for (Entity e : entities) {
                if (aspect.matches(e)) {
                    familyEntities.add(e);
                    e.getFamilyBits().set(aspect.getIndex());
                }
            }
        }

        return immutableEntities;
    }

    @SuppressWarnings("unchecked")
    private <T extends Node> ImmutableList<T> registerNode(NodeFamily<T> nodeFamily) {
        ImmutableList<T> immutableNodeEntities = (ImmutableList<T>) immutableNodes.get(nodeFamily);
        if (immutableNodeEntities == null) {
            List<T> nodeEntities = new ArrayList<>(16);
            immutableNodeEntities = new ImmutableList<>(nodeEntities);
            nodes.put(nodeFamily, (List<Node>) nodeEntities);
            immutableNodes.put(nodeFamily, (ImmutableList<Node>) immutableNodeEntities);

            for (Entity e : entities) {
                if (nodeFamily.matches(e)) {
                    nodeEntities.add(nodeFamily.get(e));
                    e.getNodeBits().set(nodeFamily.getIndex());
                }
            }
        }
        return immutableNodeEntities;
    }

    private static class SystemComparator implements Comparator<EntitySystem> {
        @Override
        public int compare(EntitySystem a, EntitySystem b) {
            return a.priority > b.priority ? 1 : (a.priority == b.priority) ? 0 : -1;
        }
    }

    private static class MembershipUpdater implements ComponentListener {
        private Engine engine;

        public MembershipUpdater(Engine engine) {
            this.engine = engine;
        }

        @Override
        public void componentAdded(Entity entity, Component component) {
            engine.updateMembership(entity);
        }

        @Override
        public void componentRemoved(Entity entity, Component component) {
            engine.updateMembership(entity);
        }
    }

    static class ComponentOperationHandler {
        private Engine engine;

        private ComponentOperationHandler(Engine engine) {
            this.engine = engine;
        }

        void add(Entity entity, Component component) {
            if (engine.updating) {
                ComponentOperation operation = engine.componentOperationsPool.obtain();
                operation.makeAdd(entity, component);
                engine.componentOperations.add(operation);
            } else {
                entity.addInternal(component);
            }
        }

        void remove(Entity entity, Class<? extends Component> componentClass) {
            if (engine.updating) {
                ComponentOperation operation = engine.componentOperationsPool.obtain();
                operation.makeRemove(entity, componentClass);
                engine.componentOperations.add(operation);
            } else {
                entity.removeInternal(componentClass);
            }
        }

        void removeAll(Entity entity) {
            if (engine.updating) {
                ComponentOperation operation = engine.componentOperationsPool.obtain();
                operation.makeRemoveAll(entity);
                engine.componentOperations.add(operation);
            } else {
                entity.removeAllInternal();
            }
        }
    }

    private static class ComponentOperation implements Disposable {
        public enum Type {
            Add,
            Remove,
            RemoveAll
        }

        Type type;
        Entity entity;
        Component component;
        Class<? extends Component> componentClass;

        void makeAdd(Entity entity, Component component) {
            this.type = Type.Add;
            this.entity = entity;
            this.component = component;
            this.componentClass = null;
        }

        void makeRemove(Entity entity, Class<? extends Component> componentClass) {
            this.type = Type.Remove;
            this.entity = entity;
            this.component = null;
            this.componentClass = componentClass;
        }

        void makeRemoveAll(Entity entity) {
            this.type = Type.RemoveAll;
            this.entity = entity;
        }

        @Override
        public void dispose() {
            type = null;
            entity = null;
            component = null;
            componentClass = null;
        }
    }

    private static class ComponentOperationPool extends Pool<ComponentOperation> {
        @Override
        protected ComponentOperation newObject() {
            return new ComponentOperation();
        }
    }

    private static class EntityOperation implements Disposable {
        enum Type {
            Add,
            Remove,
            RemoveAll
        }

        Type type;
        Entity entity;

        @Override
        public void dispose() {
            type = null;
            entity = null;
        }
    }

    private static class EntityOperationPool extends Pool<EntityOperation> {
        @Override
        protected EntityOperation newObject() {
            return new EntityOperation();
        }
    }
}
