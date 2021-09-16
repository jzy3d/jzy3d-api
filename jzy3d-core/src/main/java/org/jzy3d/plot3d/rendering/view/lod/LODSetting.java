package org.jzy3d.plot3d.rendering.view.lod;

import org.jzy3d.plot3d.primitives.Wireframeable;

public class LODSetting {
  String name;

  FaceColor face;
  WireColor wire;
  Light light;
  Bounds bounds;

  enum FaceColor {
    SMOOTH, FLAT, OFF
  }

  enum WireColor {
    SMOOTH, FLAT, UNIFORM, OFF
  }

  enum Light {
    TWO, ONE, OFF
  }

  enum Bounds {
    /** Bounds are rendered alone without face nor wire whatever their setting */
    OVERRIDE, ON, OFF
  }


  public LODSetting() {}

  public LODSetting(String name, Bounds bounds) {
    this(name, FaceColor.OFF, WireColor.OFF, Light.TWO, bounds);
  }

  public LODSetting(String name, FaceColor face, WireColor wire) {
    this(name, face, wire, Light.TWO, Bounds.OFF);
  }

  public LODSetting(String name, FaceColor face, WireColor wire, Light light) {
    this(name, face, wire, light, Bounds.OFF);
  }

  public LODSetting(String name, FaceColor face, WireColor wire, Light light, Bounds bounds) {
    super();
    this.name = name;
    this.face = face;
    this.wire = wire;
    this.light = light;
    this.bounds = bounds;
  }

  public void apply(Wireframeable wireframeable) {
    switch (face) {
      case SMOOTH:
        wireframeable.setFaceDisplayed(true);
        break;
      case FLAT:
        wireframeable.setFaceDisplayed(true);
        break;
      case OFF:
        wireframeable.setFaceDisplayed(false);
        break;
      default:
        throw new RuntimeException("Unknown LOD setting : " + face);
    }

    switch (wire) {
      case SMOOTH:
        wireframeable.setWireframeDisplayed(true);
        break;
      case FLAT:
        wireframeable.setWireframeDisplayed(true);
        break;
      case UNIFORM:
        wireframeable.setWireframeDisplayed(true);
        break;
      case OFF:
        wireframeable.setWireframeDisplayed(false);
        break;
      default:
        throw new RuntimeException("Unknown LOD setting : " + wire);
    }

    switch (bounds) {
      case ON:
        wireframeable.setBoundingBoxDisplayed(true);
        break;
      case OFF:
        wireframeable.setBoundingBoxDisplayed(false);
        break;
      default:
        throw new RuntimeException("Unknown LOD setting : " + bounds);
    }

  }



  public String getName() {
    return name;
  }


  public void setName(String name) {
    this.name = name;
  }


  public FaceColor getFace() {
    return face;
  }


  public void setFace(FaceColor face) {
    this.face = face;
  }


  public WireColor getWire() {
    return wire;
  }


  public void setWire(WireColor wire) {
    this.wire = wire;
  }


  public Light getLight() {
    return light;
  }


  public void setLight(Light light) {
    this.light = light;
  }


}
