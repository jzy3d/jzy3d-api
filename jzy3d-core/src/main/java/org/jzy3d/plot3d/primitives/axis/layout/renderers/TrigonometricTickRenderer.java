package org.jzy3d.plot3d.primitives.axis.layout.renderers;

import org.jzy3d.maths.Utils;

public class TrigonometricTickRenderer implements ITickRenderer {
  static final double PI = Math.PI;
  static final double PI_MUL_2 = Math.PI * 2;
  static final double PI_DIV_2 = Math.PI / 2;
  static final double PI_DIV_3 = Math.PI / 3;
  static final double PI_DIV_4 = Math.PI / 4;
  static final double PI_DIV_5 = Math.PI / 5;

  // private byte pi = 0x03C0;
  private static final String π = new String("\u03C0");

  protected double delta = 0.0000001;

  /*public static void main(String[] args) {
    System.out.println(π);
  }*/

  @Override
  public String format(double value) {
    if (eq(value, 0)) {
      return "0";
    }

    // Positive PI
    else if (eq(value, PI)) {
      return π;
    } else if (eq(value, PI_MUL_2)) {
      return "2" + π;
    } else if (eq(value, PI_DIV_2)) {
      return π + "/2";
    } else if (eq(value, PI_DIV_3)) {
      return π + "/3";
    } else if (eq(value, PI_DIV_4)) {
      return π + "/4";
    } else if (eq(value, PI_DIV_5)) {
      return π + "/5";
    }

    // Negative PI
    else if (eq(value, -PI)) {
      return "-" + π;
    } else if (eq(value, -PI_MUL_2)) {
      return "-2" + π;
    } else if (eq(value, -PI_DIV_2)) {
      return "-" + π + "/2";
    } else if (eq(value, -PI_DIV_3)) {
      return "-" + π + "/3";
    } else if (eq(value, -PI_DIV_4)) {
      return "-" + π + "/4";
    } else if (eq(value, -PI_DIV_5)) {
      return "-" + π + "/5";
    }

    else
      return Utils.num2str('g', value, 6);
  }

  protected boolean eq(double value, double ref, double delta) {
    return Math.abs(value - ref) < delta;
  }

  protected boolean eq(double value, double ref) {
    return eq(value, ref, delta);
  }

}
