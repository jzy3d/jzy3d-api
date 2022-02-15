package org.jzy3d.awt;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.awt.Component;
import org.jzy3d.maths.Coord2d;

public class AWTHelper {
	public static Coord2d getPixelScale(Component component) {
		return getPixelScale((Graphics2D)component.getGraphics());
	}
	
	public static Coord2d getPixelScale(Graphics2D g2d) {
		AffineTransform globalTransform = g2d.getTransform();
		return new Coord2d(globalTransform.getScaleX(), globalTransform.getScaleY());
	}

	public static double getPixelScaleX(Component component) {
		return getPixelScaleX((Graphics2D)component.getGraphics());
	}
	
	public static double getPixelScaleX(Graphics2D g2d) {
		if(g2d==null)
			return 1;
		AffineTransform globalTransform = g2d.getTransform();
		return globalTransform.getScaleX();
	}

	public static double getPixelScaleY(Component component) {
		return getPixelScaleY((Graphics2D)component.getGraphics());
	}
	
	public static double getPixelScaleY(Graphics2D g2d) {
		if(g2d==null)
			return 1;
		AffineTransform globalTransform = g2d.getTransform();
		return globalTransform.getScaleY();
	}
		
}
