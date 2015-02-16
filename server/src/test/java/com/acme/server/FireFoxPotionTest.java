package com.acme.server;

import com.acme.engine.application.Context;
import com.acme.engine.ashley.EntityEngine;
import com.acme.engine.ashley.Wired;
import com.acme.engine.effects.EffectList;
import com.acme.engine.effects.EffectSystem;
import com.acme.server.component.InvulnerableComponent;
import com.acme.server.effects.EffectFactory;
import com.acme.server.effects.InvulImpactController;
import com.acme.server.entities.EntityFactory;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;

@Wired
public class FireFoxPotionTest extends Assert {

    private ComponentMapper<InvulnerableComponent> invulCm;
    private EntityEngine engine;
    private EffectSystem effectSystem;

    private Entity bjorn;
    private Entity fireFoxPotionEffect;

    @BeforeMethod
    public void setUp() throws Exception {
        EntityFactory entityFactory = new EntityFactory(new HashMap<>());
        EffectFactory effectFactory = new EffectFactory();
        EntityEngine engine = new EntityEngine(Mockito.mock(Context.class), new Engine());
        engine.addSystem(entityFactory);
        engine.addSystem(effectFactory);
        engine.addSystem(new EffectSystem());
        InvulImpactController invulImpactController = Mockito.spy(new InvulImpactController());
        Mockito.doAnswer(invocationOnMock -> {
            bjorn.add(new InvulnerableComponent(0));
            return null;
        }).when(invulImpactController).applied(Mockito.any(Entity.class), Mockito.any(Entity.class));
        Mockito.doAnswer(invocationOnMock -> {
            bjorn.remove(InvulnerableComponent.class);
            return null;
        }).when(invulImpactController).removed(Mockito.any(Entity.class), Mockito.any(Entity.class));
        engine.addSystem(invulImpactController);
        engine.initialize();
        bjorn = new Entity().add(new EffectList());
        engine.addEntity(bjorn);
        fireFoxPotionEffect = effectFactory.createFireFoxPotionEffect(10000);
        engine.wireObject(this);
    }

    @Test
    public void testName() throws Exception {
        effectSystem.applyEffect(fireFoxPotionEffect, bjorn);
        assertTrue(invulCm.has(bjorn));
        engine.update(10000);
        assertFalse(invulCm.has(bjorn));
    }
}
