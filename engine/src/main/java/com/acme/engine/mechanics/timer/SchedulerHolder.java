package com.acme.engine.mechanics.timer;

import com.acme.engine.ecs.core.Component;

public final class SchedulerHolder extends Component {

    final Scheduler scheduler = new Scheduler();
}
