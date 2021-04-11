package org.jzy3d.ui.editors;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import org.jzy3d.maths.Range;


public class Coord3dEditor extends JPanel {
  public Coord3dEditor(String name, Range xscale, Range yscale, Range zscale) {
    setLayout(new GridLayout(4, 1));

    // sliders[0] = createSlider( "X", (int)xscale.getMin(), (int)xscale.getMax());

    sliders[0] = createSlider("X", (int) xscale.getMin(), (int) xscale.getMax());
    sliders[1] = createSlider("Y", (int) yscale.getMin(), (int) yscale.getMax());
    sliders[2] = createSlider("Z", (int) zscale.getMin(), (int) zscale.getMax());
    add(new JLabel(name));
    add(sliders[0]);
    add(sliders[1]);
    add(sliders[2]);
  }

  protected JSlider createSlider(final String title, int min, int max) {
    if (min == max) {
      min = -1;
      max = 2;
    }

    final JSlider slider = new JSlider();
    slider.setMinimum(min);
    slider.setMaximum(max);
    slider.setMajorTickSpacing((max - min) / 5);
    slider.setMinorTickSpacing((max - min));
    slider.setPaintTicks(true);
    slider.setPaintLabels(true);
    return slider;
  }

  public JSlider getSlider(int i) {
    return sliders[i];
  }

  protected JSlider[] sliders = new JSlider[3];

  private static final long serialVersionUID = 3090387949522460142L;

}
