package com.acme.server;

import com.acme.commons.application.ApplicationAdapter;
import com.acme.commons.application.Context;
import com.acme.commons.application.ContextBuilder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ServerStartupTest {

    private Context context;
    private BrowserQuest application;

    @BeforeMethod
    public void setUp() throws Exception {
        context = new ContextBuilder(new ApplicationAdapter() {
            @Override
            public void update() {
            }
        }).build();
        application = new BrowserQuest();
    }

    @Test
    public void testStartup() throws Exception {
        application.create(context);
        application.dispose();
    }
}
