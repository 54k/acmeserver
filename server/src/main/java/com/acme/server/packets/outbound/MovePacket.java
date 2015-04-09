package com.acme.server.packets.outbound;

import com.acme.commons.network.OutboundPacket;
import com.acme.ecs.core.ComponentMapper;
import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Wire;
import com.acme.server.model.component.PositionComponent;
import com.acme.server.packets.OpCodes;

@Wire
public class MovePacket extends OutboundPacket {

	private ComponentMapper<PositionComponent> pcm;

	private Entity entity;

	public MovePacket(Entity entity) {
		this.entity = entity;
	}

	@Override
	public void write() {
		writeInt(OpCodes.MOVE);
		writeLong(entity.getId());
		PositionComponent transform = pcm.get(entity);
		writeInt(transform.position.getX());
		writeInt(transform.position.getY());
	}
}
