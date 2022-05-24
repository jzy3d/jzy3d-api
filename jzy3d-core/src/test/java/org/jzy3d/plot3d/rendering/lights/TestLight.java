package org.jzy3d.plot3d.rendering.lights;

import org.junit.Test;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.rendering.lights.Light.Type;
import org.mockito.Mockito;

public class TestLight {
  @Test
  public void whenSetPositionAndType_ThenGLIsCalledAccordingly() {
    IPainter p = Mockito.spy(IPainter.class);

    float scale = 2;
    float x = 10;
    float y = 11;
    float z = 12;

    // Given a light
    Light light = new Light();

    // ---------------------------------------------
    // When edit properties and trigger apply
    light.setType(Type.POSITIONAL);
    light.setPosition(new Coord3d(x, y, z));
    light.apply(p, new Coord3d(scale, scale, scale));

    // Then position and translation are properly set
    float[] expectedGLPosition = {0, 0, 0, Light.POSITIONAL_TYPE};
    Mockito.verify(p).glLight_Position(0, expectedGLPosition);
    Mockito.verify(p).glTranslatef(x * scale, y * scale, z * scale);
    
    // ---------------------------------------------
    // When edit properties and trigger apply
    scale = 3;
    light.setType(Type.DIRECTIONAL);
    light.setPosition(new Coord3d(x, y, z));
    light.apply(p, new Coord3d(scale, scale, scale));

    // Then position and translation are properly set
    float[] expectedGLPosition2 = {0, 0, 0, Light.DIRECTIONAL_TYPE};
    Mockito.verify(p).glLight_Position(0, expectedGLPosition2);
    Mockito.verify(p).glTranslatef(x * scale, y * scale, z * scale);

  }

}
