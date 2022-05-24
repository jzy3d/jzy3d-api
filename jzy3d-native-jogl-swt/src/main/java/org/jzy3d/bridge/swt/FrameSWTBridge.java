package org.jzy3d.bridge.swt;

import java.awt.Component;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.IFrame;
import org.jzy3d.demos.SurfaceDemoSWTAWTBridge;
import org.jzy3d.maths.Rectangle;

public class FrameSWTBridge implements IFrame {
  Display display;
  Shell shell;
  Chart chart;

  public FrameSWTBridge() {}

  public FrameSWTBridge(Chart chart, Rectangle bounds, String title) {
    initialize(chart, bounds, title);
  }

  @Override
  public void initialize(Chart chart, Rectangle bounds, String title) {
    this.chart = chart;

    display = new Display();
    shell = new Shell(display);
    shell.setLayout(new FillLayout());
    shell.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
    shell.setText(title + "[SWT]");

    SWT_AWT_Bridge.adapt(shell, (Component) chart.getCanvas());

    shell.open();

    Runnable r = new Runnable() {

      @Override
      public void run() {
        while (!shell.isDisposed()) {
          if (!display.readAndDispatch()) {
            display.sleep();
          }
        }
        dispose();
      }

    };
    // Thread t = new Thread(r);
    // t.start();
    Runnable r2 = new Runnable() {

      @Override
      public void run() {
        print("target/" + SurfaceDemoSWTAWTBridge.class.getSimpleName() + ".png");
      }
    };

    // Executors.newFixedThreadPool(1).execute(r2);
    r2.run();
    r.run();
    // Executors.callable(r);
  }

  public void dispose() {
    chart.stopAllThreads();
    chart.dispose();
    display.dispose();
  }

  @Override
  public void initialize(Chart chart, Rectangle bounds, String title, String message) {
    initialize(chart, bounds, title);
  }

  public void print(String file) {
    GC gc = new GC(display);
    final Image image = new Image(display, shell.getBounds());
    gc.copyArea(image, shell.getBounds().x, shell.getBounds().y);
    gc.dispose();

    ImageLoader saver = new ImageLoader();
    saver.data = new ImageData[] {image.getImageData()};
    saver.save(file, SWT.IMAGE_PNG);
    System.out.println("Print SWT chart to " + file);
  }
}
