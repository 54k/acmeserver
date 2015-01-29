package com.acme.commons.ashley;

import com.acme.commons.application.Context;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class EngineListenerTest extends Assert {

    private WiringEngine engine;

    @BeforeMethod
    public void setUp() throws Exception {
        engine = new WiringEngine(mock(Context.class), new Engine());
    }

    @Test
    public void testEngineInitialization() throws Exception {
        SimpleSystem spy = spy(new SimpleSystem());
        engine.addSystem(spy);
        engine.initialize();
        verify(spy).initialize();
    }

    static class SimpleSystem extends EntitySystem implements EngineListener {
        @Override
        public void initialize() {
        }
    }
}
