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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import org.jzy3d.chart.Chart;
import org.jzy3d.maths.Rectangle;

public class FrameAWT extends java.awt.Frame implements IFrame {
  private static final long serialVersionUID = -4482149010771554002L;

  protected Chart chart;

  // public constructor for easier construction by reflexion
  public FrameAWT() {super();}

  public FrameAWT(Chart chart, Rectangle bounds, String title) {
    initialize(chart, bounds, title);
  }

  public FrameAWT(Chart chart, Rectangle bounds, String title, String message) {
    initialize(chart, bounds, title, message);
  }

  @Override
  public void initialize(Chart chart, Rectangle bounds, String title) {
    initialize(chart, bounds, title, "[Awt]");
  }

  @Override
  public void initialize(Chart chart, Rectangle bounds, String title, String message) {
    this.chart = chart;
    if (message != null) {
      this.setTitle(title + message);
    } else {
      this.setTitle(title);
    }


    this.add((java.awt.Component) chart.getCanvas());
    this.pack();
    // this.setSize(bounds.width, bounds.height);
    this.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);

    this.setVisible(true);

    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        if(FrameAWT.this.chart!=null) {
          FrameAWT.this.remove((java.awt.Component) FrameAWT.this.chart.getCanvas());
          FrameAWT.this.chart.stopAllThreads();
          FrameAWT.this.chart.dispose();
          FrameAWT.this.chart = null;
          FrameAWT.this.dispose();
        }
      }
    });
  }
}