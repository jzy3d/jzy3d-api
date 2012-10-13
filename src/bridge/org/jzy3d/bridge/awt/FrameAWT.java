package org.jzy3d.bridge.awt;

import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.jzy3d.bridge.IFrame;
import org.jzy3d.chart.Chart;


public class FrameAWT extends java.awt.Frame implements IFrame{
	public FrameAWT(Chart chart, Rectangle bounds, String title){
		this(chart, bounds, title, "[Awt]");
	}
	
	public FrameAWT(Chart chart, Rectangle bounds, String title, String message){
		this.chart = chart;
		this.setTitle(title + message);
		this.add((java.awt.Component)chart.getCanvas());
		this.pack();
		this.setBounds(bounds);
		this.setVisible(true);
		
		this.addWindowListener(new WindowAdapter() { 
			public void windowClosing(WindowEvent e) {
				FrameAWT.this.remove((java.awt.Component)FrameAWT.this.chart.getCanvas());
				FrameAWT.this.chart.dispose();
				FrameAWT.this.chart = null;
				FrameAWT.this.dispose();
			}
		});
	}
	private Chart chart;
	private static final long serialVersionUID = 1L;
}
