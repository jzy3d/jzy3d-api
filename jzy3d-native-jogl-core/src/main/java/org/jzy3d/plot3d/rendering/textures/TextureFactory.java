package org.jzy3d.plot3d.rendering.textures;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jzy3d.painters.IPainter;
import com.jogamp.opengl.GL;

public class TextureFactory {
  public static void preload(List<String> names) {
    for (String name : names) {
      preload(name);
    }
  }

  public static void preload(String name) {
    map.put(name, new SharedTexture(name));
  }

  public static void init(IPainter painter, GL gl) {
    for (SharedTexture texture : map.values())
      texture.mount(painter);
  }

  public static SharedTexture get(String name) {
    SharedTexture texture = map.get(name);
    if (texture == null)
      texture = new SharedTexture(name);
    map.put(name, texture);
    return texture;
  }

  protected static Map<String, SharedTexture> map = new HashMap<String, SharedTexture>();
}
