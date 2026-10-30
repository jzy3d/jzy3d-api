package org.jzy3d.ui.editors;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

public class ColorEditor extends JPanel {
  public ColorEditor(String name) {
    setLayout(new GridLayout(5, 1));

    int min = 0;
    int max = 100;

    sliders[0] = createSlider("R", min, max);
    sliders[1] = createSlider("G", min, max);
    sliders[2] = createSlider("B", min, max);
    // sliders[3] = createSlider( "A", min, max);
    add(new JLabel(name));
    add(sliders[0]);
    add(sliders[1]);
    add(sliders[2]);
    // add( sliders[3] );
  }

  protected JSlider createSlider(final String title, int min, int max) {
    final JSlider slider = new JSlider();
    slider.setMinimum(min);
    slider.setMaximum(max);
    slider.setMajorTickSpacing(20);
    slider.setMinorTickSpacing(5);
    slider.setPaintTicks(true);
    slider.setPaintLabels(true);
    return slider;
  }

  protected JTextField createTxtField(String value) {
    final JTextField field = new JTextField();
    field.setText(value);
    return field;
  }

  public JSlider getSlider(int i) {
    return sliders[i];
  }

  protected JSlider[] sliders = new JSlider[4];

  private static final long serialVersionUID = 3090387949522460142L;

}
