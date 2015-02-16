package com.acme.engine.effects;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.HashSet;
import java.util.Set;

public class EffectList extends Component {

    public final Set<Entity> effects = new HashSet<>();
}
