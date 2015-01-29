package com.acme.commons.event;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class EventManagerTest extends Assert {

    private EventManager eventManager;
    private SimpleListener mock;
    private SimpleListener listener;

    @BeforeMethod
    public void setUp() throws Exception {
        eventManager = new EventManager();
        mock = mock(SimpleListener.class);
        listener = new SimpleListener(mock);
    }

    @Test
    public void testEventManager() throws Exception {
        eventManager.register(listener);

        Event1 event1 = eventManager.post(Event1.class);
        Event2 event2 = eventManager.post(Event2.class);
        event1.onEvent1();
        event2.onEvent2();

        verify(mock).onEvent1();
        verify(mock).onEvent2();

        eventManager.unregister(listener);

        event1.onEvent1();
        event2.onEvent2();

        verifyNoMoreInteractions(mock);
    }

    @Event
    interface Event1 {
        void onEvent1();
    }

    @Event
    interface Event2 {
        void onEvent2();
    }

    static class SimpleListener implements Event1, Event2 {
        final SimpleListener mock;

        SimpleListener(SimpleListener mock) {
            this.mock = mock;
        }

        @Override
        public void onEvent1() {
            mock.onEvent1();
        }

        @Override
        public void onEvent2() {
            mock.onEvent2();
        }
    }
}
