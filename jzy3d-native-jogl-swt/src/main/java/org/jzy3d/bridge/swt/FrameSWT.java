package org.jzy3d.bridge.swt;

import java.awt.Component;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.IFrame;
import org.jzy3d.maths.Rectangle;

public class FrameSWT implements IFrame {

    public FrameSWT() {
    }

    public FrameSWT(Chart chart, Rectangle bounds, String title) {
        initialize(chart, bounds, title);
    }

    @Override
    public void initialize(Chart chart, Rectangle bounds, String title) {

        Display display = new Display();
        Shell shell = new Shell(display);

        shell.setLayout(new FillLayout());
        shell.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
        shell.setText(title + "[SWT]");
        Bridge.adapt(shell, (Component) chart.getCanvas());
        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        chart.dispose();
        display.dispose();
    }

    @Override
    public void initialize(Chart chart, Rectangle bounds, String title, String message) {
        initialize(chart, bounds, title);
    }
}
