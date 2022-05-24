package org.jzy3d.svm.editors;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.instantsvm.Parameters;

public class RegressionParamsEditor extends JPanel {
  public RegressionParamsEditor(Parameters params) {
    setLayout(new GridLayout(3, 2));
    // int min = 0;
    // int max = 100;


    setParameters(params);
  }

  public void setParameters(Parameters params) {
    addField(0, "eps", Double.toString(params.getParam().eps));
    addField(1, "C", Double.toString(params.getParam().C));
    addField(2, "gamma", Double.toString(params.getParam().gamma));
  }

  protected void addField(int id, String name, String value) {
    fields[id] = EditorToolkit.createTextField(value);
    add(new JLabel(name));
    add(fields[id]);
  }

  public JTextField getField(int i) {
    return fields[i];
  }

  protected JTextField[] fields = new JTextField[4];

  private static final long serialVersionUID = 3090387949522460142L;

}
