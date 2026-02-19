/*******************************************************************************
 * Copyright (c) 2022, 2023 Martin Pernollet & contributors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 *******************************************************************************/
package org.jzy3d.chart.factories;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jzy3d.chart.Chart;
import org.jzy3d.maths.Rectangle;

public class FrameSwing extends JFrame implements IFrame {
  public static void main(String[] args) {
    FrameSwing s = new FrameSwing();
    s.pack();
    s.setSize(500, 500);
    s.setVisible(true);
    s.pack();
  }
  // public constructor for easier construction by reflexion
  public FrameSwing() {}

  public FrameSwing(Chart chart, Rectangle bounds, String title) {
    initialize(chart, bounds, title);
  }

  @Override
  public void initialize(Chart chart, Rectangle bounds, String title) {
    this.chart = chart;

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Container contentPane = getContentPane();
    BorderLayout layout = new BorderLayout();
    contentPane.setLayout(layout);

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        FrameSwing.this.remove((java.awt.Component) FrameSwing.this.chart.getCanvas());
        FrameSwing.this.chart.stopAllThreads();
        FrameSwing.this.chart.dispose();
        FrameSwing.this.chart = null;
        FrameSwing.this.dispose();
      }
    });

    JPanel panel3d = new JPanel();
    panel3d.setLayout(new java.awt.BorderLayout());
    panel3d.add((JComponent) chart.getCanvas());

    /*EventQueue.invokeLater(new Runnable() {
        @Override
        public void run() {
	            ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "My Action");
           EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue();
           queue.postEvent(event);
           

        }
     });*/

    setVisible(true);
    setTitle(title + "[Swing]");
    setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
    contentPane.add((JComponent) chart.getCanvas(), BorderLayout.CENTER);
    pack();

  }

  private Chart chart;
  private static final long serialVersionUID = 6474157681794629264L;

  @Override
  public void initialize(Chart chart, Rectangle bounds, String title, String message) {
    initialize(chart, bounds, title);
  }

  public void print(String file) throws IOException {
    BufferedImage i = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
    // paint(i.getGraphics());
    print(i.getGraphics());
    ImageIO.write(i, "png", new File(file));
    System.out.println("Print Swing Frame to " + file);
  }

}