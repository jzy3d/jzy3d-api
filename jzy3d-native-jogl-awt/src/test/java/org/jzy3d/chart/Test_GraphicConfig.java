package org.jzy3d.chart;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsConfiguration;

public class Test_GraphicConfig {
	public static void main(String[] args) {
	    final GraphicsDevice awtDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	    System.out.println(awtDevice);
	    
	    for(GraphicsConfiguration c: awtDevice.getConfigurations()) {
	    	System.out.println(c);
	    }
	}
}
