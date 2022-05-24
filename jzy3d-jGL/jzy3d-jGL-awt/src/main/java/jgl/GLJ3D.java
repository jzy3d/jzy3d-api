/*
 * @(#)GLJ3D.java 0.1 00/01/20
 *
 * jGL 3-D graphics library for Java Copyright (c) 2000 Robin Bing-Yu Chen
 * (robin@is.s.u-tokyo.ac.jp)
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

package jgl;

import jgl.glaux.MODELPTR;
import jgl.glj3d.GLJ3DGeometry;

/**
 * GLJ3D is the Java3d like class of JavaGL 2.2.
 *
 * @version 0.1, 20 Jan 2000
 * @author Robin Bing-Yu Chen
 */

public class GLJ3D {

  /** Constants of J3D */
  private static final int COLORCUBE = 0;

  /** Private Data Members */
  private GL JavaGL;
  private MODELPTR lists[] = new MODELPTR[25];

  private int findList(int index, double paramArray[], int size) {
    MODELPTR endList;

    endList = lists[index];
    while (endList != null) {
      if (compareParams(endList.params, paramArray, size)) {
        return (endList.list);
      }
      endList = endList.ptr;
    }

    /* if not found, return 0 and calling routine should make a new list */
    return (0);
  }

  private boolean compareParams(double oneArray[], double twoArray[], int size) {
    int i;
    boolean matches = true;

    for (i = 0; (i < size) && matches; i++) {
      if (oneArray[i] != twoArray[i]) {
        matches = false;
      }
    }
    return (matches);
  }

  private int makeModelPtr(int index, double sizeArray[], int count) {
    MODELPTR newModel;

    newModel = new MODELPTR();
    newModel.list = JavaGL.glGenLists(1);
    newModel.numParam = count;
    newModel.params = sizeArray;
    newModel.ptr = lists[index];
    lists[index] = newModel;

    return (newModel.list);
  }

  public void j3dPerspective(double fovx, double aspect, double zNear, double zFar) {
    double xmin, xmax, ymin, ymax;

    xmax = zNear * Math.tan(fovx * Math.PI / 360.0);
    xmin = -xmax;

    ymin = xmin / aspect;
    ymax = xmax / aspect;

    JavaGL.glFrustum((float) xmin, (float) xmax, (float) ymin, (float) ymax, (float) zNear,
        (float) zFar);
  }

  public void j3dColorCube(double scale) {
    double sizeArray[] = {scale};
    int displayList = findList(COLORCUBE, sizeArray, 1);

    if (displayList == 0) {
      JavaGL.glNewList(makeModelPtr(COLORCUBE, sizeArray, 1), GL.GL_COMPILE_AND_EXECUTE);
      GLJ3DGeometry.drawColorCube(JavaGL, scale);
      JavaGL.glEndList();
    } else {
      JavaGL.glCallList(displayList);
    }
  }

  public GLJ3D() {
    System.out.println("Please call new GLJ3D (yourGL)");
  }

  public GLJ3D(GL myGL) {
    JavaGL = myGL;
  }

}
