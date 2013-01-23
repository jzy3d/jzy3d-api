package org.jzy3d.io;

import java.util.List;

import org.jzy3d.plot3d.primitives.AbstractDrawable;

public interface ILoader {
    public List<AbstractDrawable> load(String file) throws Exception;
}