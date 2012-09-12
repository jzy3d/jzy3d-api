package org.jzy3d.factories;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.view.Camera;

public class CameraFactory{
    public Camera getInstance(Coord3d center) {
        return new Camera(center);
    }
}
