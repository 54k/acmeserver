package com.acme.server.component;

import com.acme.engine.ashley.component.TimerComponent;

public class RegenerationComponent extends TimerComponent {

    public RegenerationComponent() {
        super(0, 1000);
    }
}
