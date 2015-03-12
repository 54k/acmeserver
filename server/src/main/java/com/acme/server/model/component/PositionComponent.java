package com.acme.server.model.component;

import com.acme.ecs.core.Component;
import com.acme.server.world.Orientation;
import com.acme.server.world.Position;
import com.acme.server.world.Region;

public class PositionComponent extends Component {

	public final Position position = new Position();
	public Orientation orientation = Orientation.BOTTOM;

	public Region region;
	public boolean spawned;
}
