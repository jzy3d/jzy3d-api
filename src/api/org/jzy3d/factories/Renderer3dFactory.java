package org.jzy3d.factories;

import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;

public class Renderer3dFactory {
    public Renderer3d getInstance(View view, boolean traceGL, boolean debugGL){
        return new Renderer3d(view, traceGL, debugGL);
    }
}
