package org.jzy3d.plot3d.primitives.vbo;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.List;

import javax.media.opengl.GL;

import org.apache.log4j.Logger;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.io.IGLLoader;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.vbo.DrawableVBO;

/**
 * A simple loader loading an existing collection of coordinates into a Vertex
 * Buffer Objects once GL initialization stage requires it to be loaded.
 * 
 * If a colormapper is given, color buffer will be filled according to coloring policy.
 * 
 * @author martin
 */
public class ListCoord3dVBOLoader implements IGLLoader<DrawableVBO> {
    protected List<Coord3d> coordinates = null;
    protected ColorMapper coloring = null;

    public ListCoord3dVBOLoader(List<Coord3d> coordinates) {
        this.coordinates = coordinates;
    }
    
    public ListCoord3dVBOLoader(List<Coord3d> coordinates, ColorMapper coloring) {
        this.coordinates = coordinates;
        this.coloring = coloring;
    }

    // @Override
    // @SuppressWarnings("unchecked")
    public void load(GL gl, DrawableVBO drawable) throws Exception {
        // configure
        boolean hasNormal = false;
        int n = coordinates.size();
        int dimension = 3; // x, y, z
        int geometrySize = computeGeometrySize(drawable);
        int verticeBufferSize = computeVerticeBufferSize(n, dimension, geometrySize, hasNormal, coloring!=null);
        int indexBufferSize = n * geometrySize;

        // build and load buffers
        FloatBuffer vertices = FloatBuffer.allocate(verticeBufferSize);
        IntBuffer indices = IntBuffer.allocate(indexBufferSize);
        BoundingBox3d bounds = new BoundingBox3d();
        fillBuffersWithCollection(drawable, vertices, indices, bounds);

        // Store in GPU
        drawable.setData(gl, indices, vertices, bounds);
        Logger.getLogger(ListCoord3dVBOLoader.class).info("done loading " + n + " coords");
    }

    protected void fillBuffersWithCollection(DrawableVBO drawable, FloatBuffer vertices, IntBuffer indices, BoundingBox3d bounds) {
        fillBuffersWithCollection(drawable, coordinates, coloring, vertices, indices, bounds);
    }

    protected void fillBuffersWithCollection(DrawableVBO drawable, Collection<Coord3d> coordinates, FloatBuffer vertices, IntBuffer indices, BoundingBox3d bounds) {
        fillBuffersWithCollection(drawable, coordinates, null, vertices, indices, bounds);
    }

    protected void fillBuffersWithCollection(DrawableVBO drawable, Collection<Coord3d> coordinates, ColorMapper colors, FloatBuffer vertices, IntBuffer indices, BoundingBox3d bounds) {
        drawable.setHasColorBuffer(colors!=null);
        
        int size = 0;
        for (Coord3d c : coordinates) {
            indices.put(size++);
            putCoord(vertices, c);
            bounds.add(c);
            
            if(colors!=null){
                putColor(vertices, colors.getColor(c));
            }
        }
        vertices.rewind();
        indices.rewind();
    }

    protected void putCoord(FloatBuffer vertices, Coord3d c) {
        vertices.put(c.x);
        vertices.put(c.y);
        vertices.put(c.z);
    }

    protected void putColor(FloatBuffer vertices, Color color) {
        vertices.put(color.r);
        vertices.put(color.g);
        vertices.put(color.b);
    }

    
    protected int computeVerticeBufferSize(int n, int dimension, int geometrysize, boolean hasNormal, boolean hasColor) {
        int verticeBufferSize = 0;
        
        if(hasColor){
            verticeBufferSize = n * (dimension * 2) * geometrysize;// *2 normals
        }
        
        if (hasNormal) {
            verticeBufferSize = n * (dimension * 2) * geometrysize;// *2 normals
        } else {
            verticeBufferSize = n * dimension * geometrysize;

        }

        if(hasColor){
            verticeBufferSize = n * (dimension * 2) * geometrysize;// *2 color
        }

        return verticeBufferSize;
    }

    protected int computeGeometrySize(DrawableVBO drawable) {
        int geomsize = 0; // triangle

        if (drawable.getGeometry() == GL.GL_POINTS) {
            geomsize = 1;
        } else if (drawable.getGeometry() == GL.GL_TRIANGLES) {
            geomsize = 3;
        }
        return geomsize;
    }

}
