package org.jzy3d.plot3d.rendering.view.lod;

import org.jzy3d.colors.Color;
import org.jzy3d.painters.ColorModel;
import org.jzy3d.plot3d.primitives.Wireframeable;

public class LODSetting {
  String name;

  FaceColor face;
  WireColor wire;
  Light light;
  Bounds bounds;
  ColorModel colorModel = ColorModel.SMOOTH;

  public enum FaceColor {
    ON, OFF
  }

  public enum WireColor {
    VARYING, UNIFORM, OFF
  }

  public enum Light {
    TWO, ONE, OFF
  }

  public enum Bounds {
    ON, OFF
  }


  public LODSetting() {}

  public LODSetting(String name, Bounds bounds) {
    this(name, FaceColor.OFF, WireColor.OFF, bounds);
  }

  public LODSetting(String name, FaceColor face, WireColor wire) {
    this(name, face, wire, Bounds.OFF);
  }

  public LODSetting(String name, FaceColor face, WireColor wire, ColorModel colorModel) {
    this(name, face, wire, Bounds.OFF);
    setColorModel(colorModel);
  }

  public LODSetting(String name, FaceColor face, WireColor wire, Bounds bounds) {
    super();
    this.name = name;
    this.face = face;
    this.wire = wire;
    this.bounds = bounds;
  }

  public void apply(Wireframeable wireframeable) {
    switch (face) {
      case ON:
        wireframeable.setFaceDisplayed(true);
        break;
      case OFF:
        wireframeable.setFaceDisplayed(false);
        break;
      default:
        throw new RuntimeException("Unknown LOD setting : " + face);
    }

    switch (wire) {
      case VARYING:
        wireframeable.setWireframeDisplayed(true);
        wireframeable.setWireframeColorFromPolygonPoints(true);
        break;
      case UNIFORM:
        wireframeable.setWireframeColor(Color.BLACK);
        wireframeable.setWireframeDisplayed(true);
        wireframeable.setWireframeColorFromPolygonPoints(false);
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

  public ColorModel getColorModel() {
    return colorModel;
  }

  public void setColorModel(ColorModel colorModel) {
    this.colorModel = colorModel;
  }
  
  public boolean isBoundsOnly() {
    return Bounds.ON.equals(bounds) && FaceColor.OFF.equals(face) && WireColor.OFF.equals(wire);
  }
}
