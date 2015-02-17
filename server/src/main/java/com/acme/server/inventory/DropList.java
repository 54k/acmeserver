package com.acme.server.inventory;

import com.acme.server.entity.Type;
import com.badlogic.ashley.core.Component;

import java.util.ArrayList;
import java.util.List;

public final class DropList extends Component {

    final List<Drop> drops = new ArrayList<>();

    public List<Drop> getDrops() {
        return drops;
    }

    public static final class Drop {

        private Type type;
        private int chance;

        public Drop(Type type, int chance) {
            this.type = type;
            this.chance = chance;
        }

        public Type getType() {
            return type;
        }

        public int getChance() {
            return chance;
        }
    }
}
