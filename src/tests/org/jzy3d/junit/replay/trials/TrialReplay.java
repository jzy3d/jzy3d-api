package org.jzy3d.junit.replay.trials;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Rectangle;

import org.jzy3d.bridge.IFrame;
import org.jzy3d.chart.Chart;
import org.jzy3d.junit.replay.EventReplay;

public class TrialReplay {
	public static void main(String[] args) throws Exception {
		Chart c = SimpleChartTest.chart("awt");
		c.getView().getCamera().setScreenGridDisplayed(true);
		c.addMouseController();
		c.addKeyController();
		
		IFrame f = c.display(new Rectangle(0,0,500,500), "mouse test");
        Frame ff = (Frame)f;
        
        EventReplay replay = new EventReplay((Component)c.getCanvas(),ff);
        replay.replay("replay");
        ff.dispose();
	}

}
