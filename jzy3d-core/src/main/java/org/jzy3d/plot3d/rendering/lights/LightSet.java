package org.jzy3d.plot3d.rendering.lights;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;

public class LightSet {
  protected List<Light> lights;
  protected boolean lazyLightInit = false;
  
  public LightSet() {
    this.lights = new ArrayList<Light>();
  }

  public LightSet(List<Light> lights) {
    this.lights = lights;
  }

  public void init(IPainter painter) {
    painter.glEnable_ColorMaterial();
  }

  public void apply(IPainter painter, Coord3d scale) {
    if (lazyLightInit) {
      initLight(painter);
      for (Light light : lights) {
        painter.glEnable_Light(light.getId());
      }
      lazyLightInit = false;
    }
    for (Light light : lights) {
      light.apply(painter, scale);
    }
  }
  
  // http://www.sjbaker.org/steve/omniv/opengl_lighting.html
  protected void initLight(IPainter painter) {
    painter.glEnable_ColorMaterial();
    painter.glEnable_Lighting();

    // Light model
    painter.glLightModel(LightModel.LIGHT_MODEL_TWO_SIDE, true);
    //painter.glLightModel(LightModel.LIGHT_MODEL_LOCAL_VIEWER, false);
    //painter.glLightModel(LightModel.LIGHT_MODEL_AMBIENT, Color.MAGENTA);
  }

  public void enableLightIfThereAreLights(IPainter painter) {
    enable(painter, true);
  }

  public void enable(IPainter painter, boolean onlyIfAtLeastOneLight) {
    if (onlyIfAtLeastOneLight) {
      if (lights.size() > 0)
        painter.glEnable_Lighting();
    } else
      painter.glEnable_Lighting();
  }

  public void disable(IPainter painter) {
    painter.glDisable_Lighting();
  }

  public Light get(int id) {
    return lights.get(id);
  }

  public List<Light> getLights() {
    return lights;
  }

  public void add(Light light) {
    if (lights.size() == 0)
      queryLazyLightInit();
    lights.add(light);
  }

  public void remove(Light light) {
    lights.remove(light);
  }

  protected void queryLazyLightInit() {
    lazyLightInit = true;
  }


}
