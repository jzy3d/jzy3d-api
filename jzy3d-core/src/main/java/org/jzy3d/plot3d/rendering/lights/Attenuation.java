package org.jzy3d.plot3d.rendering.lights;

/**
 * A simple setting class to define the attenuation of a light w.r.t. to the distance of the vertex
 * to the light source, as defined in the {@link Light} javadoc.
 * 
 * @author Martin Pernollet
 *
 */
public class Attenuation {
  public enum Type {
    CONSTANT, LINEAR, QUADRATIC
  }

  protected float constant = 0f;
  protected float linear = 0f;
  protected float quadratic = 0f;

  public Attenuation() {
    this(1, 0, 0);
  }

  /**
   * @see {@link Light} javadoc for constant, linear and quadratic attenuation explanation.
   */
  public Attenuation(float constant, float linear, float quadratic) {
    this.linear = linear;
    this.constant = constant;
    this.quadratic = quadratic;
  }

  public float getLinear() {
    return linear;
  }

  public void setLinear(float linear) {
    this.linear = linear;
  }

  public float getConstant() {
    return constant;
  }

  public void setConstant(float constant) {
    this.constant = constant;
  }

  public float getQuadratic() {
    return quadratic;
  }

  public void setQuadratic(float quadratic) {
    this.quadratic = quadratic;
  }
}
