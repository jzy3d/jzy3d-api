package org.jzy3d.factories;

import org.jzy3d.plot3d.rendering.ordering.AbstractOrderingStrategy;
import org.jzy3d.plot3d.rendering.ordering.BarycentreOrderingStrategy;

public class OrderingStrategyFactory {
	public AbstractOrderingStrategy getInstance() {
		return DEFAULT;
	}
	
	public static AbstractOrderingStrategy DEFAULT = new BarycentreOrderingStrategy();
}
