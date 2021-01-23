package org.jzy3d.plot3d.primitives.enlightables;

import org.jzy3d.colors.Color;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Wireframeable;
import org.jzy3d.plot3d.rendering.lights.MaterialProperty;

public abstract class AbstractEnlightable extends Wireframeable {

  protected void applyMaterial(IPainter painter) {
    painter.glMaterial(MaterialProperty.AMBIENT, materialAmbiantReflection, true);
    painter.glMaterial(MaterialProperty.DIFFUSE, materialDiffuseReflection, true);
    painter.glMaterial(MaterialProperty.SPECULAR, materialSpecularReflection, true);
    painter.glMaterial(MaterialProperty.SHININESS, materialShininess, true);
  }

  /******************** LIGHT CONFIG **************************/

  public Color getMaterialAmbiantReflection() {
    return materialAmbiantReflection;
  }

  public void setMaterialAmbiantReflection(Color materialAmbiantReflection) {
    this.materialAmbiantReflection = materialAmbiantReflection;
  }

  public Color getMaterialDiffuseReflection() {
    return materialDiffuseReflection;
  }

  public void setMaterialDiffuseReflection(Color materialDiffuseReflection) {
    this.materialDiffuseReflection = materialDiffuseReflection;
  }

  public Color getMaterialSpecularReflection() {
    return materialSpecularReflection;
  }

  public void setMaterialSpecularReflection(Color materialSpecularReflection) {
    this.materialSpecularReflection = materialSpecularReflection;
  }

  public Color getMaterialEmission() {
    return materialEmission;
  }

  public void setMaterialEmission(Color materialEmission) {
    this.materialEmission = materialEmission;
  }

  public float getMaterialShininess() {
    return materialShininess[0];
  }

  public void setMaterialShininess(float shininess) {
    materialShininess[0] = shininess;
  }

  protected Color materialAmbiantReflection = new Color(1, 1, 1, 1);
  protected Color materialDiffuseReflection = new Color(1, 1, 1, 1);
  protected Color materialSpecularReflection = new Color(1, 1, 1, 1);
  protected Color materialEmission = new Color(1, 1, 1, 1);
  protected float[] materialShininess = new float[1];
}
