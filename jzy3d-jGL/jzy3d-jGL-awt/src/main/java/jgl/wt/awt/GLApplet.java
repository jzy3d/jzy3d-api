/*
 * @(#)GLApplet.java 0.1 01/04/19
 *
 * jGL 3-D graphics library for Java Copyright (c) 2001 Robin Bing-Yu Chen (robin@csie.ntu.edu.tw)
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or any later version. the GNU Lesser General Public License should be
 * included with this distribution in the file LICENSE.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */

package jgl.wt.awt;

import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.Graphics;

import jgl.wt.awt.GL;

/**
 * GLApplet is the applet class of jGL 2.4.
 *
 * @version 0.1, 19 Apr 2001
 * @author Robin Bing-Yu Chen
 */

public class GLApplet extends Applet {

	protected GL myGL = new GL();
  protected GLU myGLU = new GLU(myGL);
  protected GLUT myUT = new GLUT(myGL);

  public void processEvent(AWTEvent e) {
    myUT.processEvent(e);
    super.processEvent(e);
  }

  public void glut_enable_events(long cap, boolean state) {
    if (state)
      enableEvents(cap);
    else
      disableEvents(cap);
  }

  public void update(Graphics g) {
    paint(g);
  }

  public void paint(Graphics g) {
    myGL.glXSwapBuffers(g, this);
  }

}
