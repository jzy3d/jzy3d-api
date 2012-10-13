package org.jzy3d.replay.trials;

import java.awt.Frame;
import java.awt.Rectangle;

import org.apache.log4j.Level;
import org.jzy3d.bridge.IFrame;
import org.jzy3d.chart.Chart;
import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;
import org.jzy3d.replay.recorder.EventRecorder;
import org.jzy3d.utils.LoggerUtils;

public class TrialRecord {
	
	public static void main(String[] args) {
		LoggerUtils.level(Level.INFO);
		
		Chart c = SimpleChartTest.chart("awt");
		c.getView().getCamera().setScreenGridDisplayed(true);
		record(c, "replay");
	}
	
	protected static void record(Chart c, String scenario){
		IFrame f = c.display(new Rectangle(200,200,500,500), "Recording scenario '" + scenario + "'");
        c.addMouseController();
        c.addKeyController();
        
		new EventRecorder("replay", (CanvasAWT)c.getCanvas(), (Frame)f);
	}
}
