package org.jzy3d.plot3d.primitives;

import javax.media.opengl.GL2;

public interface IGLBindedResource {
    /** Mount resources to gl context*/
    public void mount(GL2 gl);
    
    public boolean hasMountedOnce();
}
