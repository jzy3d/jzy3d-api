package org.jzy3d.plot3d.rendering.scene;

import java.util.ArrayList;
import java.util.List;

import org.jzy3d.plot3d.primitives.AbstractComposite;
import org.jzy3d.plot3d.primitives.AbstractDrawable;


public class Decomposition {
	public static ArrayList<AbstractDrawable> getDecomposition(List<AbstractDrawable> drawables){
		ArrayList<AbstractDrawable> monotypes = new ArrayList<AbstractDrawable>();
		
		for(AbstractDrawable c: drawables){
			if(c!=null && c.isDisplayed()){
				if(c instanceof AbstractComposite)
					monotypes.addAll(getDecomposition((AbstractComposite)c));				
				else if(c instanceof AbstractDrawable)
					monotypes.add(c);
			}
		}
		return monotypes;
	}
	
	/** Recursively expand all monotype Drawables from the given Composite.*/
	public static ArrayList<AbstractDrawable> getDecomposition(AbstractComposite input){
		ArrayList<AbstractDrawable> selection = new ArrayList<AbstractDrawable>();
		
		for(AbstractDrawable c: input.getDrawables()){
			if(c!=null && c.isDisplayed()){
				if(c instanceof AbstractComposite)
					selection.addAll(getDecomposition((AbstractComposite)c));				
				else if(c instanceof AbstractDrawable)
					selection.add(c);
			}
		}
		return selection;
	}
}
