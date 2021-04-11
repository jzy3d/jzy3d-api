package org.jzy3d.analysis;

import java.awt.Component;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jzy3d.bridge.swt.SWT_AWT_Bridge;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.Settings;

public class SWTAnalysisLauncher extends AnalysisLauncher {

  public static void openStaticSWT(IAnalysis demo) throws Exception {
    Settings.getInstance().setHardwareAccelerated(true);
    Chart chart = demo.getChart();

    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    SWT_AWT_Bridge.adapt(shell, (Component) chart.getCanvas());

    shell.setText(demo.getName());
    shell.setSize(800, 600);
    shell.open();

    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    display.dispose();
  }
}
