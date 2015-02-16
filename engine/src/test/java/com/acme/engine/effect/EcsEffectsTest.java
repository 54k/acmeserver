package com.acme.engine.effect;

import com.acme.engine.application.NullContext;
import com.acme.engine.ashley.EntityEngine;
import com.acme.engine.effects.EffectList;
import com.acme.engine.effects.EffectManager;
import com.acme.engine.effects.Impact;
import com.acme.engine.effects.ImpactSystem;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EcsEffectsTest extends Assert {

    private ComponentMapper<FireImpact> fcm;

    private EntityEngine entityEngine;
    private EffectManager effectManager;
    private Entity bjorn;
    private Entity fireEffect;

    @BeforeMethod
    public void setUp() throws Exception {
        entityEngine = new EntityEngine(new NullContext(), new Engine());
        effectManager = new EffectManager();
        entityEngine.addSystem(effectManager);
        entityEngine.addSystem(new FireImpactSystem());
        entityEngine.initialize();

        bjorn = new Entity()
                .add(new EffectList());

        fireEffect = new Entity()
                .add(new FireImpact());

        entityEngine.addEntity(bjorn);
        entityEngine.addEntity(fireEffect);

        fcm = ComponentMapper.getFor(FireImpact.class);
    }

    @Test
    public void testName() throws Exception {
        effectManager.applyEffect(fireEffect, bjorn);
        assertTrue(fcm.has(bjorn));
        entityEngine.update(0);
        entityEngine.update(0);

        assertFalse(fcm.has(bjorn));
    }

    public static class FireImpact extends Impact {

        public FireImpact() {
            remainingTicks = 1;
            timeToNextTick = 0;
        }
    }

    public static class FireImpactSystem extends ImpactSystem {

        public FireImpactSystem() {
            super(FireImpact.class);
        }

        @Override
        protected void effectReady(Entity effect, Entity target) {
            System.out.println("Ready");
//            removeEffect(effect, target);
        }

        @Override
        public void effectApplied(Entity effect, Entity target) {
            target.add(new FireImpact());
        }

        @Override
        public void effectRemoved(Entity effect, Entity target) {
            target.remove(FireImpact.class);
        }
    }
}
