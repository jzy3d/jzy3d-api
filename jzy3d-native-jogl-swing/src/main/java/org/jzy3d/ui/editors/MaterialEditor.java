package org.jzy3d.ui.editors;

import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.plot3d.primitives.enlightables.AbstractEnlightable;
import org.jzy3d.ui.LookAndFeel;


public class MaterialEditor extends JPanel {


  public MaterialEditor(Chart chart) {
    LookAndFeel.apply();

    this.chart = chart;

    ambiantColorControl = new ColorEditor("Ambiant");
    diffuseColorControl = new ColorEditor("Diffuse");
    specularColorControl = new ColorEditor("Specular");
    setLayout(new GridLayout(3, 1));
    // add(new JLabel("Ambiant"));
    add(ambiantColorControl);
    add(diffuseColorControl);

    add(specularColorControl);
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

  protected float getPercent(JSlider slider) {
    return ((float) slider.getValue()) / ((float) slider.getMaximum());
  }

  public void setTarget(AbstractEnlightable enlightable) {
    this.enlightable = enlightable;
    registerColorControl(ambiantColorControl, enlightable.getMaterialAmbiantReflection());
    registerColorControl(diffuseColorControl, enlightable.getMaterialDiffuseReflection());
    registerColorControl(specularColorControl, enlightable.getMaterialSpecularReflection());
  }

  protected ColorEditor ambiantColorControl;
  protected ColorEditor diffuseColorControl;
  protected ColorEditor specularColorControl;

  protected Chart chart;
  protected AbstractEnlightable enlightable;

  private static final long serialVersionUID = 4903947408608903517L;
}
