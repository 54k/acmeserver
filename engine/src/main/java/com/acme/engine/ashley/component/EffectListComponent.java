package com.acme.engine.ashley.component;

import com.acme.engine.effect.EffectList;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class EffectListComponent extends Component {

    private EffectList<Entity> effectList;

    public EffectList<Entity> getEffectList() {
        return effectList;
    }

    public void setEffectList(EffectList<Entity> effectList) {
        this.effectList = effectList;
    }
}
