package com.acme.commons.utils.script

class TestScript extends Behavior {

    @Override
    void update(float deltaTime) {
        owner.signal(TestScript).dispatch(this)
        owner.getBehavior(TestScript).update(12)
    }
}
