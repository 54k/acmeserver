package com.acme.engine.ashley;

import com.acme.engine.aegis.WiredListener;
import com.acme.engine.application.Context;
import com.acme.engine.event.Event;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class EntityEngineTest extends Assert {

    private EntityEngine engine;

    @BeforeMethod
    public void setUp() throws Exception {
        engine = new EntityEngine(mock(Context.class), new Engine());
    }

    @Test
    public void testEngineListener() throws Exception {
        EventProducer spy = spy(new EventProducer());
        engine.addSystem(spy);
        verify(spy).addedToEngine(eq(engine));
        engine.initialize();
        verify(spy).wired();
        engine.removeSystem(spy);
        verify(spy).removedFromEngine(eq(engine));
    }

    @Test
    public void testEngineEvents() throws Exception {
        EventProducer producer = new EventProducer();
        Events spy = mock(Events.class);
        EventReceiver receiver = new EventReceiver(spy);
        engine.addSystem(producer);
        engine.addSystem(receiver);
        engine.initialize();
        producer.sendEvent();
        verify(spy).event();
        engine.removeSystem(receiver);
        producer.sendEvent();
        verifyNoMoreInteractions(spy);
    }

    public interface Events extends Event {
        void event();
    }

    static class EventProducer extends EntitySystem implements EntityEngineListener, WiredListener {
        EntityEngine engine;

        public void sendEvent() {
            engine.post(Events.class).event();
        }

        @Override
        public void wired() {
        }

        @Override
        public void addedToEngine(EntityEngine engine) {
            this.engine = engine;
        }

        @Override
        public void removedFromEngine(EntityEngine engine) {
            this.engine = null;
        }
    }

    static class EventReceiver extends EntitySystem implements Events {
        final Events spy;

        EventReceiver(Events spy) {
            this.spy = spy;
        }

        @Override
        public void event() {
            spy.event();
        }
    }
}
