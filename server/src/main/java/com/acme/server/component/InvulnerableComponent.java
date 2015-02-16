package com.acme.server.component;

import com.acme.engine.timer.TimerComponent;

public class InvulnerableComponent extends TimerComponent {

    public InvulnerableComponent(float duration) {
        setTime(duration);
    }
}
