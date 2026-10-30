package org.jzy3d.ui.editors;

import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.lights.Light;


public class LightEditor extends JPanel {


  public LightEditor(Chart chart) {
    this.chart = chart;

    BoundingBox3d box = new BoundingBox3d(chart.getView().getBounds());
    box.scale(new Coord3d(3, 3, 3));
    box.getXRange().createEnlarge(2);
    ambiantColorControl = new ColorEditor("Ambiant");
    diffuseColorControl = new ColorEditor("Diffuse");
    specularColorControl = new ColorEditor("Specular");
    positionControl = new Coord3dEditor("Position", box.getXRange().createEnlarge(2),
        box.getYRange().createEnlarge(2), box.getZRange().createEnlarge(2));

    setLayout(new GridLayout(4, 1));
    add(ambiantColorControl);
    add(diffuseColorControl);
    add(specularColorControl);
    add(positionControl);
  }

  protected void registerColorControl(ColorEditor colorControl, final Color color) {
    final JSlider slider0 = colorControl.getSlider(0);
    slider0.setValue((int) color.r * slider0.getMaximum());
    slider0.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        color.r = getPercent(slider0);
        chart.render();
      }
    });
    final JSlider slider1 = colorControl.getSlider(1);
    slider1.setValue((int) color.g * slider1.getMaximum());
    slider1.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        color.g = getPercent(slider1);
        chart.render();
      }
    });
    final JSlider slider2 = colorControl.getSlider(2);
    slider2.setValue((int) color.b * slider2.getMaximum());
    slider2.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        color.b = getPercent(slider2);
        chart.render();
      }
    });
    /*
     * final JSlider slider3 = colorControl.getSlider(3); slider3.setValue( (int)color.a *
     * slider3.getMaximum() ); slider3.addChangeListener(new ChangeListener() { public void
     * stateChanged(ChangeEvent e) { color.a = getPercent(slider3); chart.render(); } });
     */
  }

  protected void registerCoord3dControl(Coord3dEditor colorControl, final Coord3d coord) {
    final JSlider slider0 = colorControl.getSlider(0);
    slider0.setValue((int) coord.x /** slider0.getMaximum() */
    );
    slider0.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        coord.x = slider0.getValue();
        light.setPosition(coord);
        chart.render();
      }
    });
    final JSlider slider1 = colorControl.getSlider(1);
    slider1.setValue((int) coord.y /** slider1.getMaximum() */
    );
    slider1.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        coord.y = slider1.getValue();
        light.setPosition(coord);
        chart.render();
      }
    });
    final JSlider slider2 = colorControl.getSlider(2);
    slider2.setValue((int) coord.z /** slider2.getMaximum() */
    );
    slider2.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        coord.z = slider2.getValue();
        light.setPosition(coord);
        chart.render();
      }
    });
  }

  protected float getPercent(JSlider slider) {
    return ((float) slider.getValue()) / ((float) slider.getMaximum());
  }

  public void setTarget(Light light) {
    this.light = light;

    registerColorControl(ambiantColorControl, light.getAmbiantColor());
    registerColorControl(diffuseColorControl, light.getDiffuseColor());
    registerColorControl(specularColorControl, light.getSpecularColor());
    registerCoord3dControl(positionControl, light.getPosition());
  }

  protected ColorEditor ambiantColorControl;
  protected ColorEditor diffuseColorControl;
  protected ColorEditor specularColorControl;
  protected Coord3dEditor positionControl;

  protected Chart chart;
  protected Light light;

  private static final long serialVersionUID = 4903947408608903517L;
}
