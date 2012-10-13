package org.jzy3d.bridge.swt;

import java.awt.Component;
import java.awt.Rectangle;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jzy3d.bridge.IFrame;
import org.jzy3d.chart.Chart;

public class FrameSWT  implements IFrame{
	public FrameSWT(Chart chart, Rectangle bounds, String title){
		this.chart = chart;
		
		Display display = new Display();
		Shell shell = new Shell(display);

		shell.setLayout(new FillLayout());		
		shell.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
		shell.setText(title + "[SWT]");
		Bridge.adapt(shell, (Component)chart.getCanvas());
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		this.chart.dispose();
		this.chart = null;
		display.dispose();
	}
	
	private Chart chart;
}
