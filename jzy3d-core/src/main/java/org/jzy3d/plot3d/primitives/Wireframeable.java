package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.rendering.lights.MaterialProperty;

/**
 * An {@link Wireframeable} is a {@link Drawable} that has a wireframe mode for display, i.e. almost
 * all objects except {@link Point}.
 * 
 * Defining an object as {@link Wireframeable} means this object will have a wireframe mode status
 * (on/off), a wireframe color, and a wireframe width. As a consequence of being
 * {@link Wireframeable}, a 3d object may have its faces displayed or not by
 * {@link Wireframeable#setFaceDisplayed(boolean)}.
 * 
 * Wireframe coloring can either be based on the wireframe color or the geometry {@link Point}s'
 * colors.
 * 
 * {@link Wireframeable} objects have faces which may reflect lights if there is any light switched
 * on in the chart.
 * 
 * @author Martin Pernollet
 */
public abstract class Wireframeable extends Drawable {

  protected Color wireframeColor;
  protected float wireframeWidth;
  protected boolean wireframeDisplayed;
  protected boolean wireframeColorFromPolygonPoints;

  protected boolean faceDisplayed;

  // these 5 fields are different way of dealing with coplanar polygon/line rendering
  // which work differently depending on native, emulgl, etc
  
  /** if true, use line loop for rendering polygon border, otherwise use polygon mode + line mode. */
  protected boolean wireframeWithLineLoop = true;
  /** if true, may change depth function while rendering wireframe to avoid Z-fighting with polygon face. */
  protected boolean depthFunctionChangeForWireframe = true;
  /** if true, may change the depth range to set lines and polygons in different spaces. Not accurate. */
  protected boolean polygonWireframeDepthTrick = false;
  /** if true, enable polygon offset fill, which is the most clean way of handling polygon fill / line Z fighting, but only available in native mode (not emulgl)*/
  protected boolean polygonOffsetFillEnable = true;
  protected float polygonOffsetFactor = 1.0f;
  protected float polygonOffsetUnit = 1.0f;


  protected boolean reflectLight = false;
  protected Color materialAmbiantReflection = new Color(1, 1, 1, 1);
  protected Color materialDiffuseReflection = new Color(1, 1, 1, 1);
  protected Color materialSpecularReflection = new Color(1, 1, 1, 1);
  protected Color materialEmission = new Color(1, 1, 1, 1);
  protected float[] materialShininess = new float[1];

  /**
   * Initialize the wireframeable with a white color and width of 1 for wires, hidden wireframe, and
   * displayed faces.
   */
  public Wireframeable() {
    super();
    setWireframeColor(Color.WHITE);
    setWireframeWidth(1.0f);
    setWireframeDisplayed(true);
    setFaceDisplayed(true);

    // Apply most supported way of rendering
    // polygon edges cleanly
    setPolygonOffsetFillEnable(true);
    setPolygonWireframeDepthTrick(false);
  }

  public boolean isWireframeColorFromPolygonPoints() {
    return wireframeColorFromPolygonPoints;
  }

  public void setWireframeColorFromPolygonPoints(boolean wireframeColorFromPolygonPoints) {
    this.wireframeColorFromPolygonPoints = wireframeColorFromPolygonPoints;
  }

  /** Set the wireframe color. */
  public void setWireframeColor(Color color) {
    wireframeColor = color;
  }

  /** Set the wireframe display status to on or off. */
  public void setWireframeDisplayed(boolean status) {
    wireframeDisplayed = status;
  }

  /** Set the wireframe width. */
  public void setWireframeWidth(float width) {
    wireframeWidth = width;
  }

  /** Set the face display status to on or off. */
  public void setFaceDisplayed(boolean status) {
    faceDisplayed = status;
  }

  /** Get the wireframe color. */
  public Color getWireframeColor() {
    return wireframeColor;
  }

  /** Get the wireframe display status to on or off. */
  public boolean isWireframeDisplayed() {
    return wireframeDisplayed;
  }

  /** Get the wireframe width. */
  public float getWireframeWidth() {
    return wireframeWidth;
  }

  /** Get the face display status to on or off. */
  public boolean isFaceDisplayed() {
    return faceDisplayed;
  }

  /* ************ POLYGON OFFSET **************** */


  protected void polygonOffsetFillEnable(IPainter painter) {
    painter.glEnable_PolygonOffsetFill();
    painter.glPolygonOffset(polygonOffsetFactor, polygonOffsetUnit);
  }

  protected void polygonOffsetFillDisable(IPainter painter) {
    painter.glDisable_PolygonOffsetFill();
  }

  protected void polygonOffsetLineEnable(IPainter painter) {
    painter.glEnable_PolygonOffsetLine();
    painter.glPolygonOffset(polygonOffsetFactor, polygonOffsetUnit);
  }

  protected void polygonOffsetLineDisable(IPainter painter) {
    painter.glDisable_PolygonOffsetLine();
  }

  public boolean isPolygonOffsetFillEnable() {
    return polygonOffsetFillEnable;
  }

  /**
   * Enable offset fill, which let a polygon with a wireframe render cleanly without weird depth
   * uncertainty between face polygon and wireframe polygon.
   */
  public void setPolygonOffsetFillEnable(boolean polygonOffsetFillEnable) {
    this.polygonOffsetFillEnable = polygonOffsetFillEnable;
  }

  public float getPolygonOffsetFactor() {
    return polygonOffsetFactor;
  }

  public void setPolygonOffsetFactor(float polygonOffsetFactor) {
    this.polygonOffsetFactor = polygonOffsetFactor;
  }

  public float getPolygonOffsetUnit() {
    return polygonOffsetUnit;
  }

  public void setPolygonOffsetUnit(float polygonOffsetUnit) {
    this.polygonOffsetUnit = polygonOffsetUnit;
  }

  /* ************ DEPTH RANGE **************** */

  /**
   * The higher the value, the more the line are far from the faces and hence no z-fighting occurs
   * between faces and lines. In case of higher value, line will be display more often, but also
   * lines that should be behind the polygon
   */
  public static float NO_OVERLAP_DEPTH_RATIO = 0.1f;// 0.1f;

  /**
   * May be used as alternative to {@link #setPolygonOffsetFillEnable(boolean)} in case it is not
   * supported by underlying OpenGL version (Polygon offset appears as off version 2).
   */
  public void setPolygonWireframeDepthTrick(boolean polygonWireframeDepthTrick) {
    this.polygonWireframeDepthTrick = polygonWireframeDepthTrick;
  }

  public boolean isPolygonWireframeDepthTrick() {
    return polygonWireframeDepthTrick;
  }

  protected void applyDepthRangeForUnderlying(IPainter painter) {
    painter.glDepthRangef(NO_OVERLAP_DEPTH_RATIO, 1f);
  }

  protected void applyDepthRangeForOverlying(IPainter painter) {
    painter.glDepthRangef(0.0f, 1 - NO_OVERLAP_DEPTH_RATIO);
  }

  protected void applyDepthRangeDefault(IPainter painter) {
    painter.glDepthRangef(0f, 1f);
  }

  /* ************ LIGHTS **************** */

  public boolean isReflectLight() {
    return reflectLight;
  }

  /**
   * If true, drawing this object will set ambient, diffuse, specular and shininess parameters.
   * 
   * If the drawable has no normal defined, then the normal will be automatically processed.
   * 
   * @param reflectLight
   */
  public void setReflectLight(boolean reflectLight) {
    this.reflectLight = reflectLight;
  }

  /** Applies material settings */
  protected void applyMaterial(IPainter painter) {
    painter.glMaterial(MaterialProperty.AMBIENT, materialAmbiantReflection, true);
    painter.glMaterial(MaterialProperty.DIFFUSE, materialDiffuseReflection, true);
    painter.glMaterial(MaterialProperty.SPECULAR, materialSpecularReflection, true);
    painter.glMaterial(MaterialProperty.SHININESS, materialShininess, true);
  }

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

  protected void doDrawBoundsIfDisplayed(IPainter painter) {
    if (isBoundingBoxDisplayed()) {
      painter.box(bbox, getBoundingBoxColor(), getWireframeWidth(), spaceTransformer);
    }
  }
}
