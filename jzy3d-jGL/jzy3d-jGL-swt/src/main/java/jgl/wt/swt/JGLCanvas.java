/*******************************************************************************
 * (c) 2021 Läubisoft GmbH, Christoph Läubrich
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
package jgl.wt.swt;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class JGLCanvas extends Canvas {

	protected GL gl;

	public JGLCanvas(Composite parent, int style, GLData data) {

		super(parent, style);
		gl = new GL(parent.getDisplay());
		parent.addDisposeListener(e -> gl.dispose());
		gl.setAutoAdaptToHiDPI(false);
		gl.glViewport(0, 0, 0, 0);
		addControlListener(new ControlListener() {

			@Override
			public void controlResized(ControlEvent e) {

				Point size = getSize();
				doResize(size.x, size.y);
			}

			@Override
			public void controlMoved(ControlEvent e) {

			}
		});
		addPaintListener(e -> {
			doPaint(e.gc);
		});
	}

	protected void doResize(int w, int h) {

		gl.glViewport(0, 0, w, h);
	}

	protected void doPaint(GC gc) {

		gc.setBackground(getBackground());
		Point size = getSize();
		gc.fillRectangle(0, 0, size.x, size.y);
		Image image = gl.getRenderedImage();
		if(image != null) {
			gc.drawImage(image, 0, 0);
		}
	}

	public GL getGL() {

		return gl;
	}

	public void setCurrent() {

		// no-opp just for compatibility reasons
	}

	public void swapBuffers() {

		gl.glFlush();
		redraw();
	}
}
