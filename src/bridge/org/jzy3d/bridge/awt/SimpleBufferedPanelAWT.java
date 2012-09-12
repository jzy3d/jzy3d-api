package org.jzy3d.bridge.awt;

import java.awt.Graphics;
import java.awt.Panel;

import org.jzy3d.bridge.BufferedPanel;


public abstract class SimpleBufferedPanelAWT extends Panel implements BufferedPanel{
	
	public abstract void draw(Graphics g);
	
	/**********************************************************************/
	
	public void paint(Graphics g){
		super.paint(g);
		draw(g); 
	}
	
	public void update(Graphics g){
		paint(g);
	}

	private static final long serialVersionUID = 1820167756982938374L;
}
