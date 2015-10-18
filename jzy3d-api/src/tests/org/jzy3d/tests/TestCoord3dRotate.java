package org.jzy3d.tests;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.maths.Coord3d;

/**
 * Tests cross product and AxisAngle rotation
 *
 * @author Florian Enner < florian @ enner.org >
 * @since 01.01.14
 */
public class TestCoord3dRotate {
    float delta = 1E-6f;

    @Test
    public void testCross() throws Exception {
        Coord3d x = new Coord3d(1, 0, 0);
        Coord3d y = new Coord3d(0, 1, 0);
        Coord3d z = new Coord3d(0, 0, 1);
        assertEqualCoord(z, x.cross(y));
    }

    @Test
    public void rotateX() throws Exception {
        Coord3d input = new Coord3d(0, 1, 0);
        Coord3d axis = new Coord3d(1, 0, 0);
        float angle = 90f;
        Coord3d expected = new Coord3d(0, 0, 1);
        assertEqualCoord(expected, input.rotate(angle, axis));
    }

    @Test
    public void rotateY() throws Exception {
        Coord3d input = new Coord3d(1, 0, 0);
        Coord3d axis = new Coord3d(0, 1, 0);
        float angle = 90f;
        Coord3d expected = new Coord3d(0, 0, -1);
        assertEqualCoord(expected, input.rotate(angle, axis));
    }

    @Test
    public void rotateZ() throws Exception {
        Coord3d input = new Coord3d(0, 1, 0);
        Coord3d axis = new Coord3d(0, 0, 1);
        float angle = 90f;
        Coord3d expected = new Coord3d(-1, 0, 0);
        assertEqualCoord(expected, input.rotate(angle, axis));
    }
    
    void assertEqualCoord(Coord3d expected, Coord3d actual) {
        Assert.assertEquals(expected.x, actual.x, delta);
        Assert.assertEquals(expected.y, actual.y, delta);
        Assert.assertEquals(expected.z, actual.z, delta);
    }
}
