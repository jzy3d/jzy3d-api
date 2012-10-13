package org.jzy3d.replay.old;

import java.awt.Rectangle;

import org.jzy3d.chart.Chart;
import org.jzy3d.replay.trials.SimpleChartTest;

public class MouseTest {
	public static void main(String[] args){
		Chart c = SimpleChartTest.chart("awt");
		c.display(new Rectangle(0,0,500,500), "mouse test");
        c.addMouseController();

		MouseAWT m = new MouseAWT();
		m.mouse(c);
	}
}
