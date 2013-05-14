package org.jzy3d.junit.replay.trials;

import java.awt.Frame;

import org.apache.log4j.Level;
import org.jzy3d.bridge.IFrame;
import org.jzy3d.chart.Chart;
import org.jzy3d.junit.replay.EventRecorder;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;
import org.jzy3d.utils.LoggerUtils;

public class TrialRecord {
	
	public static void main(String[] args) {
		LoggerUtils.level(Level.INFO);
		
		Chart c = SimpleChartTest.chart("awt");
		c.getView().getCamera().setScreenGridDisplayed(true);
		record(c, "screen-assert");
	}
	
	protected static void record(Chart chart, String scenario){
		IFrame f = chart.display(new Rectangle(200,200,500,500), "Recording scenario '" + scenario + "'");
        chart.addMouseController();
        chart.addKeyController();
        
		new EventRecorder(scenario, (CanvasAWT)chart.getCanvas(), (Frame)f, chart);
	}
}
