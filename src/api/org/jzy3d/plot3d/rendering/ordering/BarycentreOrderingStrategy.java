package org.jzy3d.plot3d.rendering.ordering;

import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.rendering.view.Camera;


/** The {@link BarycentreOrderingStrategy} compare two {@link AbstractDrawable}s by computing
 * their respective distances to the {@link Camera}, which must be referenced prior to any
 * comparison.
 * 
 * @author Martin Pernollet
 */
public class BarycentreOrderingStrategy extends AbstractOrderingStrategy{
	
	public int compare(AbstractDrawable d1, AbstractDrawable d2) {
		if(camera == null)
			throw new RuntimeException("No available camera for computing BarycentreOrderingStrategy");
		
		if(d1.equals(d2)) // reflexivity
			return 0;
		
		double dist1 = d1.getDistance(camera);
		double dist2 = d2.getDistance(camera);
		
		return comparison(dist1, dist2);
	}
	/*
	 * Operation must be:
	 * symetric: compare(a,b)=-compare(b,a)
	 * transitive: ((compare(x, y)>0) && (compare(y, z)>0)) implies compare(x, z)>0    true if all Drawables and the Camera don't change position!
	 * consistency?: compare(x, y)==0  implies that sgn(compare(x, z))==sgn(compare(y, z))
	 */
}
