package com.acme.core.application;

import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class ContextTest {

    @Test
    public void testContextListener() throws Exception {
        ContextListener listener = mock(ContextListener.class);
        Application application = spy(new Application());
        Context ctx = new ContextBuilder(application)
                .addContextListener(listener)
                .build();
        ctx.waitForDispose(0);

        verify(application).create(eq(ctx));
        verify(listener).created();
        verify(application).update();
        verify(application).dispose();
        verify(listener).disposed();
    }

    static class Application extends ApplicationAdapter {
        @Override
        public void update() {
            getContext().dispose();
        }
    }
}
