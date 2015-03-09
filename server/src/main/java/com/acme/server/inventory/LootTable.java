package com.acme.server.inventory;

import com.acme.ecs.core.Component;
import com.acme.server.entities.Type;

import java.util.ArrayList;
import java.util.List;

public final class LootTable extends Component {

    final List<LootEntry> lootEntries = new ArrayList<>();

    public List<LootEntry> getLootEntries() {
        return lootEntries;
    }

    public static final class LootEntry {
        private Type type;
        private int weight;

        public LootEntry(Type type, int weight) {
            this.type = type;
            this.weight = weight;
        }

        public Type getType() {
            return type;
        }

        public int getWeight() {
            return weight;
        }
    }
}
