package org.jzy3d.plot3d.builder;


/**
 * A base implementation for a {@link Mapper} with a single parameter used while applying f(x,y).
 * 
 * @author martin
 *
 */
public abstract class SingleParameterMapper extends Mapper {
  public SingleParameterMapper(double p) {
    this.p = p;
  }

  public void setParam(double p) {
    this.p = p;
  }

  public double getParam() {
    return p;
  }

  protected double p;
}
