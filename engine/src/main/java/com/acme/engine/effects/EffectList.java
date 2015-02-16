package com.acme.engine.effects;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.HashMap;
import java.util.Map;

public class EffectList extends Component {

    public final Map<String, Entity> effectsByIdentity = new HashMap<>();
}
