package org.jzy3d.svm.editors;

import java.awt.Component;
import javax.swing.JSlider;
import javax.swing.JTextField;

public class EditorToolkit {
  public static JSlider createSlider(final String title, int min, int max) {
    final JSlider slider = new JSlider();
    // slider.setBorder(BorderFactory.createTitledBorder(title));
    Component[] c = slider.getComponents();
    for (Component ci : c) {
      System.out.println(ci);
    }
    slider.setMinimum(min);
    slider.setMaximum(max);
    slider.setMajorTickSpacing(20);
    slider.setMinorTickSpacing(5);
    slider.setPaintTicks(true);
    slider.setPaintLabels(true);
    /*
     * slider.addChangeListener(new ChangeListener() {
     * 
     * @Override public void stateChanged(ChangeEvent e) { System.out.println(title + ": " +
     * slider.getValue()); }
     * 
     * });
     */
    return slider;
  }

  public static JTextField createTextField(String content) {
    JTextField field = new JTextField(content);
    return field;
  }
}
