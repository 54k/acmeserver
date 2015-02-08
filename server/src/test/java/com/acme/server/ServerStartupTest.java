package com.acme.server;

import com.acme.engine.application.Context;
import com.acme.engine.application.ContextBuilder;
import org.testng.annotations.Test;

public class ServerStartupTest {

    @Test(enabled = false)
    public void testStartup() throws Exception {
        Context ctx = new ContextBuilder(new BrowserQuest()).build();
        ctx.dispose();
        ctx.waitForDispose(0);
    }
}
