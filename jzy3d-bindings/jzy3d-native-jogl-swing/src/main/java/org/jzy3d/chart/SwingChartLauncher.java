package org.jzy3d.chart;

import java.awt.Container;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.primitives.enlightables.AbstractEnlightable;
import org.jzy3d.ui.editors.LightEditor;
import org.jzy3d.ui.editors.MaterialEditor;
import org.jzy3d.ui.views.ImagePanel;

public class SwingChartLauncher extends ChartLauncher {

  public static void openLightEditors(Chart chart) {
    // Material editor
    MaterialEditor enlightableEditor = new MaterialEditor(chart);
    if (chart.getScene().getGraph().getAll().get(0) instanceof AbstractEnlightable)
      enlightableEditor
          .setTarget((AbstractEnlightable) chart.getScene().getGraph().getAll().get(0));
    LightEditor lightEditor = new LightEditor(chart);
    lightEditor.setTarget(chart.getScene().getLightSet().get(0));

    // Windows
    openPanel(lightEditor, new Rectangle(0, 0, 200, 900), "Light");
    openPanel(enlightableEditor, new Rectangle(200, 0, 200, 675), "Material");
  }

  public static void openImagePanel(Image image) {
    openImagePanel(image, new Rectangle(0, 800, 600, 600));
  }

  public static void openImagePanel(Image image, Rectangle bounds) {
    ImagePanel panel = new ImagePanel(image);
    JFrame frame = new JFrame();
    frame.getContentPane().add(panel);
    frame.pack();
    frame.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
    frame.setVisible(true);
  }

  public static void openPanel(JPanel panel, Rectangle bounds, String title) {
    JFrame frame = new JFrame(title);
    Container content = frame.getContentPane();
    // content.setBackground(Color.white);
    content.add(panel);
    frame.pack();
    frame.setVisible(true);
    frame.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent event) {
        System.exit(0);
      }
    });
  }
}
