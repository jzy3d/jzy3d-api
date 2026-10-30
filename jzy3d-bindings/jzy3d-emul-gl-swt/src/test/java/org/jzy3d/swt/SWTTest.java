/*******************************************************************************
 * Copyright (c) 2021 Läubisoft GmbH and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.jzy3d.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import jgl.wt.swt.JGLCanvas;

public class SWTTest {

	public static void main(String[] args) {

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("jGL Canvas Test");
		shell.setLayout(new FillLayout());
		shell.open();
		GLData data = new GLData();
		data.doubleBuffer = true;
		JGLCanvas canvas = new bezcurve(shell, SWT.NONE, data);
		canvas.setCurrent();
		while(!shell.isDisposed()) {
			if(!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}
