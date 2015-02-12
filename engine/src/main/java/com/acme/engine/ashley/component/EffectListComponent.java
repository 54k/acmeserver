package com.acme.engine.ashley.component;

import com.acme.engine.effect.EffectList;
import com.badlogic.ashley.core.Component;

public class EffectListComponent extends Component {

    private EffectList effectList;

    public EffectList getEffectList() {
        return effectList;
    }

    public void setEffectList(EffectList effectList) {
        this.effectList = effectList;
    }
}
