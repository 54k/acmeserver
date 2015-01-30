package com.acme.commons.ashley;

import com.acme.commons.application.Context;
import com.acme.commons.event.Event;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import static org.mockito.Matchers.eq;

public class WiringEngineTest extends Assert {

    private WiringEngine engine;

    @BeforeMethod
    public void setUp() throws Exception {
        engine = new WiringEngine(mock(Context.class), new Engine());
    }

    @Test
    public void testEngineListener() throws Exception {
        EventProducer spy = spy(new EventProducer());
        engine.addSystem(spy);
        verify(spy).addedToEngine(eq(engine));
        engine.initialize();
        verify(spy).initialize();
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

    static class EventProducer extends EntitySystem implements EngineListener {
        WiringEngine engine;

        public void sendEvent() {
            engine.post(Events.class).event();
        }

        @Override
        public void initialize() {
        }

        @Override
        public void addedToEngine(WiringEngine engine) {
            this.engine = engine;
        }

        @Override
        public void removedFromEngine(WiringEngine engine) {
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
