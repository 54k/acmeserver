package com.acme.engine.effect;

import com.acme.engine.application.NullContext;
import com.acme.engine.ashley.EntityEngine;
import com.acme.engine.effects.Effect;
import com.acme.engine.effects.EffectList;
import com.acme.engine.effects.EffectSystem;
import com.acme.engine.effects.Impact;
import com.acme.engine.effects.ImpactController;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EcsEffectsTest extends Assert {

    private ComponentMapper<FireImpact> fcm;

    private EntityEngine entityEngine;
    private EffectSystem effectSystem;
    private Entity bjorn;
    private Entity fireEffect;

    @BeforeMethod
    public void setUp() throws Exception {
        entityEngine = new EntityEngine(new NullContext(), new Engine());
        effectSystem = new EffectSystem();
        entityEngine.addSystem(effectSystem);
        entityEngine.addSystem(new FireImpactController());
        entityEngine.initialize();

        bjorn = new Entity()
                .add(new EffectList());

        Effect effect = new Effect(1, 0);
        fireEffect = new Entity()
                .add(effect)
                .add(new FireImpact());

        entityEngine.addEntity(bjorn);
        entityEngine.addEntity(fireEffect);

        fcm = ComponentMapper.getFor(FireImpact.class);
    }

    @Test
    public void testName() throws Exception {
        effectSystem.applyEffect(fireEffect, bjorn);
        assertTrue(fcm.has(bjorn));
        entityEngine.update(0);
        assertFalse(fcm.has(bjorn));
    }

    public static class FireImpact extends Impact {
    }

    public static class FireImpactController extends ImpactController {

        public FireImpactController() {
            super(FireImpact.class);
        }

        @Override
        protected void ready(Entity effect, Entity target) {
            System.out.println("Ready");
            //            removeEffect(effect, target);
        }

        @Override
        public void applied(Entity effect, Entity target) {
            target.add(new FireImpact());
        }

        @Override
        public void removed(Entity effect, Entity target) {
            target.remove(FireImpact.class);
        }
    }
}
