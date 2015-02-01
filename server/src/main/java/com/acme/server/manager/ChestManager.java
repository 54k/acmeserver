package com.acme.server.manager;

import com.acme.commons.ashley.ManagerSystem;
import com.acme.commons.ashley.Wired;
import com.acme.server.component.WorldComponent;
import com.acme.server.controller.DropController;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class ChestManager extends ManagerSystem {

    private ComponentMapper<WorldComponent> wcm;

    private WorldManager worldManager;
    private DropController dropController;

    public void openChest(Entity entity, long chestId) {
        WorldComponent worldComponent = wcm.get(entity);
        Entity chest = worldComponent.getInstance().findEntityById(chestId);
        dropController.dropItems(chest);
        worldManager.decay(chest);
    }
}
