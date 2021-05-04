/*
 * @(#)gl_select.java 0.2 99/12/17
 *
 * jGL 3-D graphics library for Java Copyright (c) 1997-1999 Robin Bing-Yu Chen
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

package jgl.context;

/**
 * gl_select is the selection class of JavaGL 2.1.
 *
 * @version 0.2, 17 Dec 1999
 * @author Robin Bing-Yu Chen
 */

public class gl_select {

  public int Buffer[];
  public int BufferSize = 0; /* size of Buffer */
  public int BufferCount = 0; /* number of values in Buffer */
  public int Hits = 0; /* number of records in Buffer */
  public int NameStackDepth = 0;
  public int NameStack[] = new int[64]; // MAX_NAME_STACK_DEPTH = 64
  public boolean HitFlag = false;
  public float HitMinZ;
  public float HitMaxZ;

  public void set_buffer(int size, int buffer[]) {
    Buffer = buffer;
    BufferSize = size;
    BufferCount = 0;
    HitFlag = false;
    HitMinZ = 1;
    HitMaxZ = -1;
  }

  public void init_name() {
    NameStackDepth = 0;
    HitFlag = false;
    HitMinZ = 1;
    HitMaxZ = -1;
  }

  public boolean load_name(int name) {
    if (NameStackDepth == 0) {
      return false;
    }
    if (HitFlag) {
      write_hit_record();
    }
    if (NameStackDepth < 64) {
      NameStack[NameStackDepth - 1] = name;
    } else {
      NameStack[63] = name;
    }
    return true;
  }

  public boolean push_name(int name) {
    if (HitFlag) {
      write_hit_record();
    }
    if (NameStackDepth >= 64) {
      return false;
    }
    NameStack[NameStackDepth++] = name;
    return true;
  }

  public boolean pop_name() {
    if (HitFlag) {
      write_hit_record();
    }
    if (NameStackDepth <= 0) {
      return false;
    }
    NameStackDepth--;
    return true;
  }

  public void update_hit_flag(float z) {
    HitFlag = true;
    if (z < HitMinZ) {
      HitMinZ = z;
    }
    if (z > HitMaxZ) {
      HitMaxZ = z;
    }
  }

  public void write_hit_record() {
    if (BufferCount + 3 + NameStackDepth < BufferSize) {
      int i;
      int zmin, zmax;

      /* HitMinZ and HitMaxZ are in [0,1]. Multiply these values by */
      /* Integer.MAX_VALUE and round to nearest unsigned integer. */

      zmin = (int) ((float) Integer.MAX_VALUE * HitMinZ);
      zmax = (int) ((float) Integer.MAX_VALUE * HitMaxZ);

      Buffer[BufferCount++] = NameStackDepth;
      Buffer[BufferCount++] = zmin;
      Buffer[BufferCount++] = zmax;

      for (i = 0; i < NameStackDepth; i++) {
        Buffer[BufferCount++] = NameStack[i];
      }

      Hits++;
    }
    HitFlag = false;
    HitMinZ = (float) 1.0;
    HitMaxZ = (float) -1.0;
  }

}
