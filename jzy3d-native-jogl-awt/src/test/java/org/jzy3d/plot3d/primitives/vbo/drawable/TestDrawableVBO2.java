package org.jzy3d.plot3d.primitives.vbo.drawable;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.maths.Array;
import org.jzy3d.mocks.painters.NativeDesktopPainterMock;
import org.jzy3d.painters.NativeDesktopPainter;

public class TestDrawableVBO2 {
  
  @Test
  public void givenVBO_whenMount_ThenDeclaredMounted() {
    NativeDesktopPainter painter = new NativeDesktopPainterMock();
    
    int dimensions = TestMesh.DIMENSIONS;
    float[] vertices = Array.cloneFloat(TestMesh.makeArray4());

    // When
    DrawableVBO2 vbo = new DrawableVBO2(vertices, dimensions);

    // Then
    Assert.assertFalse(vbo.hasMountedOnce());
    
    // When
    vbo.mount(painter);

    // Then
    Assert.assertTrue(vbo.hasMountedOnce());

    // When
    vbo.mount(painter);

    // Then no log saying we mount again!
  }

}
