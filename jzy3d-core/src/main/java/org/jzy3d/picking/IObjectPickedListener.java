package org.jzy3d.picking;

import java.util.List;

public interface IObjectPickedListener {
	public void objectPicked(List<? extends Object> vertex, PickingSupport picking);
}
