package com.acme.engine.effect;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class EffectTest extends Assert {

    private Bjorn bjorn;

    @BeforeMethod
    public void setUp() throws Exception {
        bjorn = new Bjorn();
        bjorn.effectList = new EffectList<>(bjorn);
    }

    @Test
    public void testEffectList() throws Exception {
        EffectList<Bjorn> effectList = bjorn.effectList;
        DragonBreath dragonBreath = spy(new DragonBreath());
        effectList.apply(dragonBreath);
        verify(dragonBreath).apply(eq(bjorn));

        while (!dragonBreath.isReady()) {
            effectList.update(100);
        }

        verify(dragonBreath).remove(eq(bjorn));
        assertEquals(bjorn.hp, 50);
        assertFalse(effectList.hasEffect(dragonBreath));
    }

    private static class Bjorn {
        int hp = 100;
        EffectList<Bjorn> effectList;
    }

    private static class DragonBreath extends TimedEffect<Bjorn> {
        int damage = 10;

        public DragonBreath() {
            super(100, 5);
        }

        @Override
        public void tick(Bjorn entity) {
            entity.hp -= damage;
        }

        @Override
        public void ready(Bjorn entity) {
            entity.effectList.remove(this);
        }
    }
}
