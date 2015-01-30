package com.acme.server;

import com.acme.commons.application.Context;
import com.acme.commons.application.ContextBuilder;
import org.testng.annotations.Test;

public class ServerStartupTest {

    @Test
    public void testStartup() throws Exception {
        Context ctx = new ContextBuilder(new BrowserQuest()).build();
        ctx.dispose();
        ctx.waitForDispose(0);
    }
}
