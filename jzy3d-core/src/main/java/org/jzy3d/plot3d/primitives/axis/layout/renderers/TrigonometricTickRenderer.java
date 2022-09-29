package org.jzy3d.plot3d.primitives.axis.layout.renderers;

import org.jzy3d.maths.Utils;

/**
 * Render ticks as multiple of π if the input value to be formated is not further to a multiple of π
 * than delta.
 * 
 * 
 * <img src="doc-files/trigonometric-circle.png"/>
 *
 * @author Martin Pernollet
 */
public class TrigonometricTickRenderer implements ITickRenderer {
  static final double PI = Math.PI;
  static final double PI_MUL_2 = Math.PI * 2;

  protected int maxDenominator = 6;

  // private byte pi = 0x03C0;
  private static final String π = new String("\u03C0");

  protected double delta = 0.0001;

  public TrigonometricTickRenderer() {
    autoDelta(maxDenominator);
  }

  /** Allow overriding the authorized delta to a known trigonometric angle in radian. */
  public TrigonometricTickRenderer(double delta) {
    this.delta = delta;
  }

  /**
   * Allow overriding the max authorized π denominator. Growing this value leads to slower π
   * fraction estimation. Decreasing this value leads to less fraction being supported
   */
  public TrigonometricTickRenderer(int maxDenominator) {
    this.maxDenominator = maxDenominator;
    autoDelta(maxDenominator);
  }

  public TrigonometricTickRenderer(double delta, int maxDenominator) {
    this.delta = delta;
    this.maxDenominator = maxDenominator;
  }
  
  protected void autoDelta(int maxDenominator) {
    delta = (Math.PI/maxDenominator)/10;
  }

  @Override
  public String format(double value) {
    if (eq(value, 0)) {
      return "0";
    }
    // Positive PI
    else if (value > 0) {
      // π
      if (eq(value, PI)) {
        return π;
      }
      // 2*π or above integer multiple of π
      else if (value >= PI_MUL_2){
        int nPi = (int)Math.round(value / PI);
        double remainder = value-(nPi*PI);
        double remainderAbs = Math.abs(remainder);
        if(remainderAbs<delta) {
          return nPi + π;
        }
      }
      // fraction
      else {
        for (int i = 2; i <= maxDenominator; i++) {
          // π/n
          if (eq(value, PI / i)) {
            return π + "/" + i;
          }
          // 2*π/n
          else if (eq(value, PI_MUL_2 / i)) {
            return "2" + π + "/" + i;
          }
        }
        
        if (eq(value, PI * 3 / 2)) {
          return "3"+ π + "/2";
        }
        else if (eq(value, PI * 4 / 3)) {
          return "4"+ π + "/3";
        }

      }
    }
    // Negative PI
    else {
      // -π
      if (eq(value, -PI)) {
        return "-" + π;
      }
      // -2π or above integer multiple of π
      else if (value <= -PI_MUL_2){
        int nPi = (int)Math.round(value / PI);
        double remainder = value-(nPi*PI);
        double remainderAbs = Math.abs(remainder);
        if(remainderAbs<delta) {
          return nPi + π;
        }
      }

      
      // fraction
      else {
        for (int i = 2; i <= maxDenominator; i++) {
          // -π/n
          if (eq(value, -PI / i)) {
            return "-" + π + "/" + i;
          }
          // -2*π/n
          else if (eq(value, -PI_MUL_2 / i)) {
            return "-2" + π + "/" + i;
          }
        }
        
        if (eq(value, -PI * 3 / 2)) {
          return "-3"+ π + "/2";
        }
        else if (eq(value, -PI * 4 / 3)) {
          return "-4"+ π + "/3";
        }

      }
    }

    // found no existing known value so just let it as double value
    return Utils.num2str('g', value, 6);
  }

  protected boolean eq(double value, double ref, double delta) {
    return Math.abs(value - ref) < delta;
  }

  protected boolean eq(double value, double ref) {
    return eq(value, ref, delta);
  }

  public double getDelta() {
    return delta;
  }

  public void setDelta(double delta) {
    this.delta = delta;
  }

}
