/*
 * @(#)gl_render.java 0.4 99/11/29
 *
 * jGL 3-D graphics library for Java Copyright (c) 1996-1999 Robin Bing-Yu Chen
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

package jgl.context.render;

import jgl.GL;
import jgl.context.gl_context;
import jgl.context.gl_polygon;
import jgl.context.gl_vertex;
import jgl.context.render.pixel.gl_render_pixel;

/**
 * gl_render is the basic rendering class of JavaGL 2.1.
 * 
 * The most important method is {@link #draw_line(gl_vertex, gl_vertex)} which
 * paints the pixel between 2 2D points. The third dimension of the vertex represents
 * the depth of the pixel. Note that considering depth for drawing a pixel is 
 * made in {@link gl_depth} (a subclass of {@link gl_render})
 *
 * @version 0.4, 29 Nov 1999
 * @author Robin Bing-Yu Chen
 */

public class gl_render {

  protected gl_context CC;
  protected gl_render_pixel pixel;

  // Members for Line
  private int LineXY[][] = new int[2][2];
  protected int x;
  protected int y;

  protected int color; // for flat shading

  protected void init_xy(gl_vertex v1, gl_vertex v2) {
    LineXY[0][0] = (int) (v1.Vertex[0] + (float) 0.5);
    LineXY[0][1] = (int) (v1.Vertex[1] + (float) 0.5);
    LineXY[1][0] = (int) (v2.Vertex[0] + (float) 0.5);
    LineXY[1][1] = (int) (v2.Vertex[1] + (float) 0.5);
  }

  protected void init(gl_vertex v1, gl_vertex v2) {
    init_xy(v1, v2);
  }

  protected void set_first_xy() {
    x = LineXY[0][0];
    y = LineXY[0][1];
  }

  protected void set_first_point() {
    set_first_xy();
  }

  protected void init_dx(int dx) {}

  protected void init_dy(int dy) {}

  protected void init_dx_dy(int dx, int dy) {
    if (dx != 0) {
      init_dx(dx);
    }
    init_dy(dy); // dy must not be equal to 0
  }

  protected void x_inc_x() {
    //x++;
    ++x;
  }

  protected void x_dec_x() {
    //x--;
    --x;
  }

  protected void y_inc_y() {
    //y++;
    ++y;
  }

  protected void inc_x() {
    x_inc_x();
  }

  protected void dec_x() {
    x_dec_x();
  }

  protected void inc_y() {
    y_inc_y();
  }

  protected void inc_x_inc_y() {
    inc_x();
    inc_y();
  }

  protected void dec_x_inc_y() {
    dec_x();
    inc_y();
  }

  /** Will color the given pixel WITHOUT verifying depth buffer (see {@link gl_depth#put_pixel()}*/
  protected void put_pixel() {
    pixel.put_pixel(x, y, color);
  }

  /** Will color the given pixel WITHOUT verifying depth buffer (see {@link gl_depth#put_pixel_by_index()}*/
  protected void put_pixel_by_index() {
    pixel.put_pixel_by_index(x, color);
  }

  protected void draw_point(gl_vertex v) {
    CC.CR.pixel.put_pixel((int) (v.Vertex[0] + (float) 0.5), (int) (v.Vertex[1] + (float) 0.5),
        color);
  }

  protected void draw_point(gl_vertex v, int color) {
    this.color = color;
    draw_point(v);
  }

  /**
   * Draw a flat horizontal line in the Color Buffer, assume that x1 is in the left side of x2
   */
  protected void draw_horizontal_line(int x1, int x2, int y) {
    this.LineXY[0][0] = x1;
    this.LineXY[1][0] = x2;
    this.LineXY[0][1] = y;
    set_first_point();
    draw_horizontal_line();
  }

  /**
   * Draw a flat horizontal line in the Color Buffer, assume that x1 is in the left side of x2
   */
  protected void draw_horizontal_line() {
    int dx = LineXY[1][0] - LineXY[0][0];

    if (dx == 0) {
      put_pixel();
      return;
    }

    int RightPoint = LineXY[1][0] + CC.Viewport.Width * LineXY[0][1];

    x = LineXY[0][0] + CC.Viewport.Width * LineXY[0][1];

    while (x <= RightPoint) {
      put_pixel_by_index();
      inc_x();
    }
  }

  /** Draw a line in the Color Buffer */
  public void draw_line(gl_vertex v1, gl_vertex v2) {
    int i, err = 0;
    int dx = (int) (v1.Vertex[1] + 0.5);
    int dy = (int) (v2.Vertex[1] + 0.5);
    gl_vertex temp;

    // to guarantee the first point is upper than the second
    if (dy < dx) {
      temp = v1;
      v1 = v2;
      v2 = temp;
    } else {
      if ((dy == dx) && ((int) (v2.Vertex[0] + 0.5) < (int) (v1.Vertex[0] + 0.5))) {
        temp = v1;
        v1 = v2;
        v2 = temp;
      }
    }

    init(v1, v2);
    set_first_point();

    dx = LineXY[1][0] - LineXY[0][0];
    dy = LineXY[1][1] - LineXY[0][1];

    init_dx_dy(dx, dy);

    pixel.init(dx, dy);

    if (dy != 0) { // not for horizontal line
      put_pixel();
      if (dx >= 0) {
        if (dx >= dy) {
          for (i = 1; i <= dx; i++) {
            if (err < 0) {
              inc_x(); // also inc Z according to slope in gl_depth.
              err += dy;
            } else {
              inc_x_inc_y(); // also inc Z according to slope in gl_depth.
              err += dy - dx;
            }
            put_pixel();
          }
        } else {
          for (i = 1; i <= dy; i++) {
            if (err < 0) {
              inc_x_inc_y(); // also inc Z according to slope in gl_depth.
              err += dy - dx;
            } else {
              inc_y(); // also inc Z according to slope in gl_depth.
              err -= dx;
            }
            put_pixel();
          }
        }
      } else {
        if (Math.abs(dx) >= dy) {
          for (i = 1; i <= Math.abs(dx); i++) {
            if (err < 0) {
              dec_x();
              err += dy;
            } else {
              dec_x_inc_y();
              err += dx + dy;
            }
            put_pixel();
          }
        } else {
          for (i = 1; i <= dy; i++) {
            if (err < 0) {
              dec_x_inc_y();
              err += dx + dy;
            } else {
              inc_y();
              err += dx;
            }
            put_pixel();
          }
        }
      }
    } else { // only for horizontal line
      draw_horizontal_line();
    }
  }

  /** Draw a flat line in the Color Buffer */
  public void draw_line(gl_vertex v1, gl_vertex v2, int color) {
    this.color = color;
    draw_line(v1, v2);
  }

  // Members for Triangle
  private int TriXY[][] = new int[3][2];
  protected int RightPoint, LeftPoint;
  protected int dyl, dyl2, dyr, dyr2;
  protected int dxl, dxr;
  private int errxl, dxdyyl, dxdyl;
  private int errxr, dxdyyr, dxdyr;

  protected void init_xy(gl_vertex v1, gl_vertex v2, gl_vertex v3) {
    TriXY[0][0] = (int) (v1.Vertex[0] + (float) 0.5);
    TriXY[1][0] = (int) (v2.Vertex[0] + (float) 0.5);
    TriXY[2][0] = (int) (v3.Vertex[0] + (float) 0.5);
    TriXY[0][1] = (int) (v1.Vertex[1] + (float) 0.5);
    TriXY[1][1] = (int) (v2.Vertex[1] + (float) 0.5);
    TriXY[2][1] = (int) (v3.Vertex[1] + (float) 0.5);
    // System.out.println (TriXY[0][0]+" "+TriXY[0][1]+" "+v1.Vertex[0]+" "+v1.Vertex[1]+"
    // "+v1.Vertex[2]);
    // System.out.println (TriXY[1][0]+" "+TriXY[1][1]+" "+v2.Vertex[0]+" "+v2.Vertex[1]+"
    // "+v2.Vertex[2]);
    // System.out.println (TriXY[2][0]+" "+TriXY[2][1]+" "+v3.Vertex[0]+" "+v3.Vertex[1]+"
    // "+v3.Vertex[2]);
  }

  protected void init(gl_vertex v1, gl_vertex v2, gl_vertex v3) {
    init_xy(v1, v2, v3);
  }

  protected void set_left_xy(int pos) {
    LeftPoint = TriXY[pos][0];
  }

  protected void set_right_xy(int pos) {
    RightPoint = TriXY[pos][0];
  }

  protected void set_left(int pos) {
    set_left_xy(pos);
  }

  protected void set_right(int pos) {
    set_right_xy(pos);
  }

  protected void init_dx_dy(int area, int left, int right, int top) {}

  protected void init_other(boolean delta, int dy) {}

  protected void init_left_xy() {
    boolean deltaxl = (dxl < 0);
    if (deltaxl) {
      dxl = -dxl;
    }
    dxdyl = 0;
    if (dyl == 0) {
      dyl2 = 0;
      errxl = 1;
      dxdyyl = 1;
    } else {
      dyl2 = dyl >> 1;
      if ((dyl & 0x00000001) == 1) {
        errxl = 0;
      } else {
        errxl = 1;
      }
      while (dxl > dyl) {
        dxdyl++;
        dxl -= dyl;
      }
      dxdyyl = dxdyl + 1;
    }
    if (deltaxl) {
      dxdyl = -dxdyl;
      dxdyyl = -dxdyyl;
    }
    init_other(deltaxl, dxdyl);
  }

  protected void init_right_xy() {
    boolean deltaxr = (dxr < 0);
    if (deltaxr) {
      dxr = -dxr;
    }
    dxdyr = 0;
    if (dyr == 0) {
      dyr2 = 0;
      errxr = 1;
      dxdyr = 0;
      dxdyyr = 1;
    } else {
      dyr2 = dyr >> 1;
      if ((dyr & 0x00000001) == 1) {
        errxr = 0;
      } else {
        errxr = 1;
      }
      while (dxr > dyr) {
        dxdyr++;
        dxr -= dyr;
      }
      dxdyyr = dxdyr + 1;
    }
    if (deltaxr) {
      dxdyr = -dxdyr;
      dxdyyr = -dxdyyr;
    }
  }

  protected void init_left(int down, int top) {
    init_left_xy();
  }

  protected void init_right(int down, int top) {
    init_right_xy();
  }

  protected void inc_y_once() {}

  protected void inc_y_more() {}

  protected void inc_left_xy() {
    errxl += dxl;
    if (errxl > dyl2) {
      errxl -= dyl;
      LeftPoint += dxdyyl;
      inc_y_more();
    } else {
      LeftPoint += dxdyl;
      inc_y_once();
    }
  }

  protected void inc_right_xy() {
    errxr += dxr;
    if (errxr > dyr2) {
      errxr -= dyr;
      RightPoint += dxdyyr;
    } else {
      RightPoint += dxdyr;
    }
  }

  protected void inc_left() {
    inc_left_xy();
  }

  protected void inc_right() {
    inc_right_xy();
  }

  /*
   * protected void draw_line (gl_vertex v1, gl_vertex v2, gl_vertex v3) { draw_line (v1, v2);
   * draw_line (v2, v3); }
   */

  protected void draw_horizontal_line(int y) {
    draw_horizontal_line(LeftPoint, RightPoint, y);
  }

  /*
   * protected void draw_point (int v) { CC.CR.pixel.put_pixel (TriXY[v][0], TriXY[v][1], color); }
   */

  /** Draw a flat triangle in the Color Buffer */
  public void draw_triangle(gl_vertex v1, gl_vertex v2, gl_vertex v3) {
    int i;
    int Top, Mid, Down, Right, Left;
    /*
     * if ((int)(v1.Vertex [1]+0.5)==(int)(v2.Vertex [1]+0.5) && (int)(v2.Vertex
     * [1]+0.5)==(int)(v3.Vertex [1]+0.5)) { draw_line (v1, v2, v3); return; }
     */

    init(v1, v2, v3);

    boolean facing;

    // Test the position of the three points....
    if (TriXY[0][1] < TriXY[1][1]) {
      if (TriXY[1][1] < TriXY[2][1]) {
        Top = 0;
        Mid = 1;
        Down = 2;
        facing = false;
      } else {
        Down = 1;
        if (TriXY[0][1] < TriXY[2][1]) {
          Top = 0;
          Mid = 2;
          facing = true;
        } else {
          Top = 2;
          Mid = 0;
          facing = false;
        }
      }
    } else {
      if (TriXY[0][1] < TriXY[2][1]) {
        Top = 1;
        Mid = 0;
        Down = 2;
        facing = true;
      } else {
        Down = 0;
        if (TriXY[1][1] < TriXY[2][1]) {
          Top = 1;
          Mid = 2;
          facing = false;
        } else {
          Top = 2;
          Mid = 1;
          facing = true;
        }
      }
    }

    dxl = TriXY[Mid][0] - TriXY[Top][0];
    dyl = TriXY[Mid][1] - TriXY[Top][1];
    dxr = TriXY[Down][0] - TriXY[Top][0];
    dyr = TriXY[Down][1] - TriXY[Top][1];

    int area = dxr * dyl - dxl * dyr;

    if (area == 0) {
      /*
       * if (CC.Raster.CullFace && (CC.Raster.CullFaceMode == GL.GL_FRONT_AND_BACK)) return; if
       * ((CC.Raster.FrontMode == GL.GL_POINT) && (CC.Raster.BackMode == GL.GL_POINT)) { draw_point
       * (v1); draw_point (v2); draw_point (v3); // draw_point (Top); // draw_point (Mid); //
       * draw_point (Down); } else { draw_line (v1, v2); draw_line (v2, v3); }
       */
      return;
    }

    facing = facing ^ (area > 0) ^ (CC.Raster.FrontFace == GL.GL_CW);

    if (CC.Raster.CullFace) {
      if (CC.Raster.CullFaceMode == GL.GL_FRONT_AND_BACK)
        return;
      if (facing & (CC.Raster.CullFaceMode == GL.GL_FRONT))
        return;
      if (!facing & (CC.Raster.CullFaceMode == GL.GL_BACK))
        return;
    }

    if (facing) {
      switch (CC.Raster.FrontMode) {
        case GL.GL_POINT:
          draw_point(v1);
          draw_point(v2);
          draw_point(v3);
          // draw_point (Top);
          // draw_point (Mid);
          // draw_point (Down);
          return;
        case GL.GL_LINE:
          draw_line(v1, v2);
          draw_line(v2, v3);
          draw_line(v3, v1);
          return;
      }
    } else {
      switch (CC.Raster.BackMode) {
        case GL.GL_POINT:
          draw_point(v1);
          draw_point(v2);
          draw_point(v3);
          // draw_point (Top);
          // draw_point (Mid);
          // draw_point (Down);
          return;
        case GL.GL_LINE:
          draw_line(v1, v2);
          draw_line(v2, v3);
          draw_line(v3, v1);
          return;
      }
    }

    if (area < 0) {
      Left = dxr;
      dxr = dxl;
      dxl = Left;
      Right = dyr;
      dyr = dyl;
      dyl = Right;
      area = -area;
      Left = Down;
      Right = Mid;
    } else {
      Left = Mid;
      Right = Down;
    }

    init_dx_dy(area, Left, Right, Top);
    init_left(Left, Top);
    init_right(Right, Top);

    set_left(Top);
    set_right(Top);

    // Draw the upper part of the triangle....
    for (i = TriXY[Top][1]; i < TriXY[Mid][1]; i++) {
      draw_horizontal_line(i);
      inc_left();
      inc_right();
    }

    if (Mid == Right) {
      dyr = TriXY[Down][1] - TriXY[Mid][1];
      dxr = TriXY[Down][0] - TriXY[Mid][0];
      init_right(Down, Mid);
      set_right(Mid);
    } else { // Mid == Left
      dyl = TriXY[Down][1] - TriXY[Mid][1];
      dxl = TriXY[Down][0] - TriXY[Mid][0];
      init_left(Down, Mid);
      set_left(Mid);
    }

    // Draw the lower part of the triangle....
    for (i = TriXY[Mid][1]; i < TriXY[Down][1]; i++) {
      draw_horizontal_line(i);
      inc_left();
      inc_right();
    }

    // Draw the lowest line of the triangle....
    draw_horizontal_line(TriXY[Down][1]);
  }

  public void draw_triangle(gl_vertex v1, gl_vertex v2, gl_vertex v3, int color) {
    this.color = color;
    draw_triangle(v1, v2, v3);
  }

  public void draw_polygon(gl_polygon p) {
    if (p.n == 0) {
      return;
    }

    for (int i = 2; i < p.n; i++) {
      draw_triangle(p.Polygon[0], p.Polygon[i - 1], p.Polygon[i]);
    }
  }

  public void draw_polygon(gl_polygon p, int color) {
    this.color = color;
    draw_polygon(p);
  }

  public void set_pixel(gl_render_pixel p) {
    pixel = p;
  }

  public gl_render(gl_context cc) {
    CC = cc;
  }

}
