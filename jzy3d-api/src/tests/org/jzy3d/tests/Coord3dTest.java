package org.jzy3d.tests;

import junit.framework.TestCase;

import org.jzy3d.maths.Coord3d;

/**
 * Tests cross product and AxisAngle rotation
 *
 * @author Florian Enner < florian @ enner.org >
 * @since 01.01.14
 */
public class Coord3dTest extends TestCase {

    float delta = 1E-6f;

    void assertEqualCoord(Coord3d expected, Coord3d actual) {
        assertEquals(expected.x, actual.x, delta);
        assertEquals(expected.y, actual.y, delta);
        assertEquals(expected.z, actual.z, delta);
    }

    public void testCross() throws Exception {
        Coord3d x = new Coord3d(1, 0, 0);
        Coord3d y = new Coord3d(0, 1, 0);
        Coord3d z = new Coord3d(0, 0, 1);
        assertEqualCoord(z, x.cross(y));
    }

    public void testRotateX() throws Exception {
        Coord3d input = new Coord3d(0, 1, 0);
        Coord3d axis = new Coord3d(1, 0, 0);
        float angle = 90f;
        Coord3d expected = new Coord3d(0, 0, 1);
        assertEqualCoord(expected, input.rotate(angle, axis));
    }

    public void testRotateY() throws Exception {
        Coord3d input = new Coord3d(1, 0, 0);
        Coord3d axis = new Coord3d(0, 1, 0);
        float angle = 90f;
        Coord3d expected = new Coord3d(0, 0, -1);
        assertEqualCoord(expected, input.rotate(angle, axis));
    }

    public void testRotateZ() throws Exception {
        Coord3d input = new Coord3d(0, 1, 0);
        Coord3d axis = new Coord3d(0, 0, 1);
        float angle = 90f;
        Coord3d expected = new Coord3d(-1, 0, 0);
        assertEqualCoord(expected, input.rotate(angle, axis));
    }
}
