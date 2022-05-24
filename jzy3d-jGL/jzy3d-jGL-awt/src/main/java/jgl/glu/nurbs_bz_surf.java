/*
 * @(#)nurbs_bz_surf.java 0.1 99/11/6
 *
 * jGL 3-D graphics library for Java Copyright (c) 1999 Robin Bing-Yu Chen
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

package jgl.glu;

import jgl.GL;
import jgl.wt.awt.GLU;

/**
 * nurbs_bz_surf is one of the GLU NURBS class of JavaGL 2.1.
 *
 * @version 0.1, 6 Nov 1999
 * @author Robin Bing-Yu Chen
 */

public class nurbs_bz_surf {

  private GL JavaGL;

  private int mode;

  private int s_fac[][];
  private int t_fac[][];

  private int s_cnt;
  private int t_cnt;

  private void tesselate_strip_t_line(int top_start, int top_end, int top_z, int bot_start,
      int bot_end, int bot_z, int bot_domain) {
    int top_cnt, bot_cnt, tri_cnt, k;
    int dir;

    top_cnt = top_end - top_start;
    if (top_cnt >= 0)
      dir = 1;
    else
      dir = -1;
    bot_cnt = bot_end - bot_start;
    JavaGL.glBegin(GL.GL_LINES);
    while (top_cnt != 0) {
      if (bot_cnt != 0)
        tri_cnt = top_cnt / bot_cnt;
      else
        tri_cnt = Math.abs(top_cnt);
      for (k = 0; k <= tri_cnt; k++, top_start += dir) {
        JavaGL.glEvalCoord2f((float) bot_z / bot_domain, (float) bot_start / bot_domain);
        JavaGL.glEvalPoint2(top_z, top_start);
      }
      if (bot_cnt != 0) {
        JavaGL.glEvalCoord2f((float) bot_z / bot_domain, (float) bot_start / bot_domain);
        bot_start += dir;
        top_start -= dir;
        JavaGL.glEvalCoord2f((float) bot_z / bot_domain, (float) bot_start / bot_domain);
        JavaGL.glEvalCoord2f((float) bot_z / bot_domain, (float) bot_start / bot_domain);
        JavaGL.glEvalPoint2(top_z, top_start);
      }
      top_cnt -= dir * tri_cnt;
      bot_cnt -= dir;
    }
    JavaGL.glEnd();
  }

  private void tesselate_strip_t_fill(int top_start, int top_end, int top_z, int bot_start,
      int bot_end, int bot_z, int bot_domain) {
    int top_cnt, bot_cnt, tri_cnt, k;
    int dir;

    top_cnt = top_end - top_start;
    if (top_cnt >= 0)
      dir = 1;
    else
      dir = -1;
    bot_cnt = bot_end - bot_start;
    while (top_cnt != 0) {
      if (bot_cnt != 0)
        tri_cnt = top_cnt / bot_cnt;
      else
        tri_cnt = Math.abs(top_cnt);
      JavaGL.glBegin(GL.GL_TRIANGLE_FAN);
      JavaGL.glEvalCoord2f((float) bot_z / bot_domain, (float) bot_start / bot_domain);
      for (k = 0; k <= tri_cnt; k++, top_start += dir)
        JavaGL.glEvalPoint2(top_z, top_start);
      if (bot_cnt != 0) {
        bot_start += dir;
        top_start -= dir;
        JavaGL.glEvalCoord2f((float) bot_z / bot_domain, (float) bot_start / bot_domain);
      }
      JavaGL.glEnd();
      top_cnt -= dir * tri_cnt;
      bot_cnt -= dir;
    }
  }

  private void tesselate_strip_t(int top_start, int top_end, int top_z, int bot_start, int bot_end,
      int bot_z, int bot_domain) {
    if (mode == GL.GL_FILL)
      tesselate_strip_t_fill(top_start, top_end, top_z, bot_start, bot_end, bot_z, bot_domain);
    else
      tesselate_strip_t_line(top_start, top_end, top_z, bot_start, bot_end, bot_z, bot_domain);
  }

  private void tesselate_strip_s_fill(int top_start, int top_end, int top_z, int bot_start,
      int bot_end, int bot_z, float bot_domain) {
    int top_cnt, bot_cnt, tri_cnt, k;
    int dir;

    top_cnt = top_end - top_start;
    if (top_cnt >= 0)
      dir = 1;
    else
      dir = -1;
    bot_cnt = bot_end - bot_start;
    while (top_cnt != 0) {
      if (bot_cnt != 0)
        tri_cnt = top_cnt / bot_cnt;
      else
        tri_cnt = Math.abs(top_cnt);
      JavaGL.glBegin(GL.GL_TRIANGLE_FAN);
      JavaGL.glEvalCoord2f((float) bot_start / bot_domain, (float) bot_z / bot_domain);
      for (k = 0; k <= tri_cnt; k++, top_start += dir)
        JavaGL.glEvalPoint2(top_start, top_z);
      if (bot_cnt != 0) {
        bot_start += dir;
        top_start -= dir;
        JavaGL.glEvalCoord2f((float) bot_start / bot_domain, (float) bot_z / bot_domain);
      }
      JavaGL.glEnd();
      top_cnt -= dir * tri_cnt;
      bot_cnt -= dir;
    }
  }

  private void tesselate_strip_s_line(int top_start, int top_end, int top_z, int bot_start,
      int bot_end, int bot_z, float bot_domain) {
    int top_cnt, bot_cnt, tri_cnt, k;
    int dir;

    top_cnt = top_end - top_start;
    if (top_cnt >= 0)
      dir = 1;
    else
      dir = -1;
    bot_cnt = bot_end - bot_start;
    JavaGL.glBegin(GL.GL_LINES);
    while (top_cnt != 0) {
      if (bot_cnt != 0)
        tri_cnt = top_cnt / bot_cnt;
      else
        tri_cnt = Math.abs(top_cnt);
      for (k = 0; k <= tri_cnt; k++, top_start += dir) {
        JavaGL.glEvalCoord2f((float) bot_start / bot_domain, (float) bot_z / bot_domain);
        JavaGL.glEvalPoint2(top_start, top_z);
      }
      if (bot_cnt != 0) {
        JavaGL.glEvalCoord2f((float) bot_start / bot_domain, (float) bot_z / bot_domain);
        bot_start += dir;
        top_start -= dir;
        JavaGL.glEvalCoord2f((float) bot_start / bot_domain, (float) bot_z / bot_domain);
        JavaGL.glEvalPoint2(top_start, top_z);
        JavaGL.glEvalCoord2f((float) bot_start / bot_domain, (float) bot_z / bot_domain);
      }
      top_cnt -= dir * tri_cnt;
      bot_cnt -= dir;
    }
    JavaGL.glEnd();
  }

  private void tesselate_strip_s(int top_start, int top_end, int top_z, int bot_start, int bot_end,
      int bot_z, float bot_domain) {
    if (mode == GL.GL_FILL)
      tesselate_strip_s_fill(top_start, top_end, top_z, bot_start, bot_end, bot_z, bot_domain);
    else
      tesselate_strip_s_line(top_start, top_end, top_z, bot_start, bot_end, bot_z, bot_domain);
  }

  private void tesselate_bl_corn(float s_1, float t_1) {
    if (mode == GL.GL_FILL) {
      JavaGL.glBegin(GL.GL_TRIANGLE_FAN);
      JavaGL.glEvalPoint2(1, 1);
      JavaGL.glEvalCoord2f(s_1, 0);
      JavaGL.glEvalCoord2f(0, 0);
      JavaGL.glEvalCoord2f(0, t_1);
    } else {
      JavaGL.glBegin(GL.GL_LINES);
      JavaGL.glEvalCoord2f(0, 0);
      JavaGL.glEvalCoord2f(0, t_1);
      JavaGL.glEvalCoord2f(0, 0);
      JavaGL.glEvalPoint2(1, 1);
      JavaGL.glEvalCoord2f(0, 0);
      JavaGL.glEvalCoord2f(s_1, 0);
    }
    JavaGL.glEnd();
  }

  private void tesselate_br_corn(int v_top, int v_bot, float s_1, float t_1) {
    if (mode == GL.GL_FILL) {
      JavaGL.glBegin(GL.GL_TRIANGLE_FAN);
      JavaGL.glEvalPoint2(1, v_top);
      JavaGL.glEvalCoord2f(0, v_bot * t_1);
      JavaGL.glEvalCoord2f(0, (v_bot + 1) * t_1);
      JavaGL.glEvalCoord2f(s_1, (v_bot + 1) * t_1);
    } else {
      JavaGL.glBegin(GL.GL_LINES);
      JavaGL.glEvalCoord2f(0, (v_bot + 1) * t_1);
      JavaGL.glEvalPoint2(1, v_top);
      JavaGL.glEvalCoord2f(0, (v_bot + 1) * t_1);
      JavaGL.glEvalCoord2f(0, v_bot * t_1);
      JavaGL.glEvalCoord2f(0, (v_bot + 1) * t_1);
      JavaGL.glEvalCoord2f(s_1, (v_bot + 1) * t_1);
    }
    JavaGL.glEnd();
  }

  private void tesselate_tl_corn(int u_rit, int u_let, float s_1, float t_1) {
    if (mode == GL.GL_FILL) {
      JavaGL.glBegin(GL.GL_TRIANGLE_FAN);
      JavaGL.glEvalPoint2(u_rit, 1);
      JavaGL.glEvalCoord2f((u_let + 1) * s_1, t_1);
      JavaGL.glEvalCoord2f((u_let + 1) * s_1, 0);
      JavaGL.glEvalCoord2f(u_let * s_1, 0);
    } else {
      JavaGL.glBegin(GL.GL_LINES);
      JavaGL.glEvalCoord2f((u_let + 1) * s_1, 0);
      JavaGL.glEvalPoint2(u_rit, 1);
      JavaGL.glEvalCoord2f((u_let + 1) * s_1, 0);
      JavaGL.glEvalCoord2f(u_let * s_1, 0);
      JavaGL.glEvalCoord2f((u_let + 1) * s_1, 0);
      JavaGL.glEvalCoord2f((u_let + 1) * s_1, t_1);
    }
    JavaGL.glEnd();
  }

  private void tesselate_tr_corn(int u_let, int v_bot, int u_rit, int v_top, float s_1, float t_1) {
    if (mode == GL.GL_FILL) {
      JavaGL.glBegin(GL.GL_TRIANGLE_FAN);
      JavaGL.glEvalPoint2(u_let, v_bot);
      JavaGL.glEvalCoord2f((u_rit - 1) * s_1, v_top * t_1);
      JavaGL.glEvalCoord2f(u_rit * s_1, v_top * t_1);
      JavaGL.glEvalCoord2f(u_rit * s_1, (v_top - 1) * t_1);
    } else {
      JavaGL.glBegin(GL.GL_LINES);
      JavaGL.glEvalCoord2f(u_rit * s_1, v_top * t_1);
      JavaGL.glEvalPoint2(u_let, v_bot);
      JavaGL.glEvalCoord2f(u_rit * s_1, v_top * t_1);
      JavaGL.glEvalCoord2f(u_rit * s_1, (v_top - 1) * t_1);
      JavaGL.glEvalCoord2f(u_rit * s_1, v_top * t_1);
      JavaGL.glEvalCoord2f((u_rit - 1) * s_1, v_top * t_1);
    }
    JavaGL.glEnd();
  }

  public void map(int s, int t) {
    int top, bot, rit, let;

    if (s == 0) {
      top = t_fac[t][0];
      bot = t_fac[t][1];
    } else {
      if (s == s_cnt - 1) {
        top = t_fac[t][2];
        bot = t_fac[t][0];
      } else {
        top = bot = t_fac[t][3];
      }
    }

    if (t == 0) {
      rit = s_fac[s][0];
      let = s_fac[s][1];
    } else {
      if (t == t_cnt - 1) {
        rit = s_fac[s][2];
        let = s_fac[s][0];
      } else {
        rit = let = s_fac[s][3];
      }
    }

    if (top > bot) {
      if (let < rit) {
        JavaGL.glMapGrid2f(rit, (float) 0.0, (float) 1.0, top, (float) 0.0, (float) 1.0);
        JavaGL.glEvalMesh2(mode, 1, rit, 1, top);
        tesselate_strip_s(1, rit, 1, 1, let, 0, let);
        tesselate_bl_corn((float) 1.0 / (float) let, (float) 1.0 / (float) bot);
        tesselate_strip_t(top, 1, 1, bot, 1, 0, bot);
      } else {
        if (let == rit) {
          JavaGL.glMapGrid2f(rit, (float) 0.0, (float) 1.0, top, (float) 0.0, (float) 1.0);
          JavaGL.glEvalMesh2(mode, 1, rit, 0, top);
          tesselate_strip_t(top, 0, 1, bot, 0, 0, bot);
        } else {
          JavaGL.glMapGrid2f(let, (float) 0.0, (float) 1.0, top, (float) 0.0, (float) 1.0);
          JavaGL.glEvalMesh2(mode, 1, rit, 0, top - 1);
          tesselate_strip_t(top - 1, 0, 1, bot - 1, 0, 0, bot);
          tesselate_br_corn(top - 1, bot - 1, (float) 1.0 / (float) rit, (float) 1.0 / (float) bot);
          tesselate_strip_s(let, 1, top - 1, rit, 1, rit, rit);
        }
      }
    } else {
      if (top == bot) {
        if (let < rit) {
          JavaGL.glMapGrid2f(rit, (float) 0.0, (float) 1.0, top, (float) 0.0, (float) 1.0);
          JavaGL.glEvalMesh2(mode, 0, rit, 1, top);
          tesselate_strip_s(0, rit, 1, 0, let, 0, let);
        } else {
          if (let == rit) {
            JavaGL.glMapGrid2f(rit, (float) 0.0, (float) 1.0, top, (float) 0.0, (float) 1.0);
            JavaGL.glEvalMesh2(mode, 0, rit, 0, top);
          } else {
            JavaGL.glMapGrid2f(let, (float) 0.0, (float) 1.0, top, (float) 0.0, (float) 1.0);
            JavaGL.glEvalMesh2(mode, 0, let, 0, top - 1);
            tesselate_strip_s(let, 0, top - 1, rit, 0, rit, rit);
          }
        }
      } else {
        if (let < rit) {
          JavaGL.glMapGrid2f(rit, (float) 0.0, (float) 1.0, bot, (float) 0.0, (float) 1.0);
          JavaGL.glEvalMesh2(mode, 0, rit - 1, 1, bot);
          tesselate_strip_s(0, rit - 1, 1, 0, let - 1, 0, let);
          tesselate_tl_corn(rit - 1, let - 1, (float) 1.0 / (float) let, (float) 1.0 / (float) top);
          tesselate_strip_t(1, bot, rit - 1, 1, top, top, top);
        } else {
          if (let == rit) {
            JavaGL.glMapGrid2f(rit, (float) 0.0, (float) 1.0, bot, (float) 0.0, (float) 1.0);
            JavaGL.glEvalMesh2(mode, 0, rit - 1, 0, bot);
            tesselate_strip_t(0, bot, rit - 1, 0, top, top, top);
          } else {
            JavaGL.glMapGrid2f(let, (float) 0.0, (float) 1.0, bot, (float) 0.0, (float) 1.0);
            JavaGL.glEvalMesh2(mode, 0, let - 1, 0, bot - 1);
            tesselate_strip_t(0, bot - 1, let - 1, 0, top - 1, top, top);
            tesselate_tr_corn(let - 1, bot - 1, rit, top, (float) 1.0 / (float) rit,
                (float) 1.0 / (float) top);
            tesselate_strip_s(let - 1, 0, bot - 1, rit - 1, 0, rit, rit);
          }
        }
      }
    }
  }

  public void set_property(int display_mode, int sfactors[][], int tfactors[][], int s_bezier_cnt,
      int t_bezier_cnt) {
    if (display_mode == GLU.GLU_FILL) {
      mode = GL.GL_FILL;
    } else {
      mode = GL.GL_LINE;
    }
    s_fac = sfactors;
    t_fac = tfactors;
    s_cnt = s_bezier_cnt;
    t_cnt = t_bezier_cnt;
  }

  public nurbs_bz_surf() {
    System.out.println("Please call new nurbs_bz_surf (yourGL)");
  }

  public nurbs_bz_surf(GL myGL) {
    JavaGL = myGL;
  }

}
