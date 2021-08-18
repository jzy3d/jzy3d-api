/*******************************************************************************
 * Copyright (c) 2000, 2021 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 * Christoph Läubrich - adapt example to use jGL
 *******************************************************************************/
package org.jzy3d.swt;

/*
 * SWT OpenGL snippet: use LWJGL to draw to an SWT GLCanvas
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 *
 * @since 3.2
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import jgl.wt.swt.GL;
import jgl.wt.swt.JGLCanvas;

public class Snippet195 {

	static void drawTorus(GL gl, float r, float R, int nsides, int rings) {
		float ringDelta = 2.0f * (float) Math.PI / rings;
		float sideDelta = 2.0f * (float) Math.PI / nsides;
		float theta = 0.0f, cosTheta = 1.0f, sinTheta = 0.0f;
		for (int i = rings - 1; i >= 0; i--) {
			float theta1 = theta + ringDelta;
			float cosTheta1 = (float) Math.cos(theta1);
			float sinTheta1 = (float) Math.sin(theta1);
			gl.glBegin(GL.GL_QUAD_STRIP);
			float phi = 0.0f;
			for (int j = nsides; j >= 0; j--) {
				phi += sideDelta;
				float cosPhi = (float) Math.cos(phi);
				float sinPhi = (float) Math.sin(phi);
				float dist = R + r * cosPhi;
				gl.glNormal3f(cosTheta1 * cosPhi, -sinTheta1 * cosPhi, sinPhi);
				gl.glVertex3f(cosTheta1 * dist, -sinTheta1 * dist, r * sinPhi);
				gl.glNormal3f(cosTheta * cosPhi, -sinTheta * cosPhi, sinPhi);
				gl.glVertex3f(cosTheta * dist, -sinTheta * dist, r * sinPhi);
			}
			gl.glEnd();
			theta = theta1;
			cosTheta = cosTheta1;
			sinTheta = sinTheta1;
		}
	}

	public static void main(String [] args) {
		final Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		Composite comp = new Composite(shell, SWT.NONE);
		comp.setLayout(new FillLayout());
		GLData data = new GLData ();
		data.doubleBuffer = true;
		final JGLCanvas canvas = new JGLCanvas(comp, SWT.NONE, data);
		canvas.addListener(SWT.Resize, event -> {
			Rectangle bounds = canvas.getBounds();
			GL gl = canvas.getGL();
			float fAspect = (float)bounds.width / (float)bounds.height;
			gl.glViewport(0, 0, bounds.width, bounds.height);
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			float near = 0.5f;
			float bottom = -near * (float)Math.tan(45.f / 2);
			float left = fAspect * bottom;
			gl.glFrustum(left, -left, bottom, -bottom, near, 400.f);
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();
		});
		canvas.setCurrent();

		GL gl = canvas.getGL();
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glColor3f(1.0f, 0.0f, 0.0f);
		// TODO ? gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, gl.GL_NICEST);
		gl.glClearDepth(1.0);
		gl.glLineWidth(2);
		gl.glEnable(GL.GL_DEPTH_TEST);

		shell.setText("SWT/Adopted LWJGL Example");
		shell.setSize(640, 480);
		shell.open();

		final Runnable run = new Runnable() {
			int rot = 0;
			@Override
			public void run() {
				if (!canvas.isDisposed()) {
					GL gl = canvas.getGL();
					canvas.setCurrent();
					gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
					gl.glClearColor(.3f, .5f, .8f, 1.0f);
					gl.glLoadIdentity();
					gl.glTranslatef(0.0f, 0.0f, -10.0f);
					float frot = rot;
					gl.glRotatef(0.15f * rot, 2.0f * frot, 10.0f * frot, 1.0f);
					gl.glRotatef(0.3f * rot, 3.0f * frot, 1.0f * frot, 1.0f);
					rot++;
					gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
					gl.glColor3f(0.9f, 0.9f, 0.9f);
					drawTorus(gl, 1, 1.9f + ((float)Math.sin((0.004f * frot))), 15, 15);
					canvas.swapBuffers();
					display.timerExec(10, this);
				}
			}
		};
		display.asyncExec(run);

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
