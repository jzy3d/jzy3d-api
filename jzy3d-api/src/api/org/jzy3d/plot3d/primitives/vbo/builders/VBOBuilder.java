package org.jzy3d.plot3d.primitives.vbo.builders;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.List;

import javax.media.opengl.GL;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.io.IGLLoader;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.vbo.buffers.FloatVBO;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO;

public abstract class VBOBuilder implements IGLLoader<DrawableVBO>{

    /**
     * Setup buffers dimensions according to various parameters: 
     * <ul>
     * <li>target drawable representation type (point, line, etc), 
     * <li>having color per vertex or not, 
     * <li>having normals defined (default to false)
     * <li>number of dimensions (default to 3)
     * </ul>
     * 
     */
    protected FloatVBO initFloatVBO(DrawableVBO drawable, boolean hasNormal, boolean hasColor, int n, int dimension) {
        int geometrySize = computeGeometrySize(drawable);
        int verticeBufferSize = computeVerticeBufferSize(drawable.getGeometry(), n, dimension, geometrySize, hasNormal, hasColor);
        int indexBufferSize = n * geometrySize;
        FloatVBO vbo = new FloatVBO(verticeBufferSize, indexBufferSize);
        return vbo;
    }
    
    protected FloatVBO initFloatVBO(DrawableVBO drawable, boolean hasNormal, boolean hasColor, int n) {
        return initFloatVBO(drawable, hasNormal, hasColor, n, 3);
    }

    protected FloatVBO initFloatVBO(DrawableVBO drawable, boolean hasColor, int n) {
        return initFloatVBO(drawable, false, hasColor, n, 3);
    }

    /* */
    
    protected void fillBuffersWithCollection(DrawableVBO drawable, List<Coord3d> coordinates, FloatVBO vbo) {
        fillBuffersWithCollection(drawable, coordinates, vbo.getVertices(), vbo.getIndices(), vbo.getBounds());
    }

    protected void fillBuffersWithCollection(DrawableVBO drawable, List<Coord3d> coordinates, FloatVBO vbo, ColorMapper coloring) {
        fillBuffersWithCollection(drawable, coordinates, coloring, vbo.getVertices(), vbo.getIndices(), vbo.getBounds());
    }

    protected void fillBuffersWithCollection(DrawableVBO drawable, Collection<Coord3d> coordinates, FloatBuffer vertices, IntBuffer indices, BoundingBox3d bounds) {
        fillBuffersWithCollection(drawable, coordinates, null, vertices, indices, bounds);
    }

    protected void fillBuffersWithCollection(DrawableVBO drawable, Collection<Coord3d> coordinates, ColorMapper colors, FloatBuffer vertices, IntBuffer indices, BoundingBox3d bounds) {
        drawable.setHasColorBuffer(colors != null);

        int size = 0;
        for (Coord3d c : coordinates) {
            indices.put(size++);
            putCoord(vertices, c);
            bounds.add(c);

            if (colors != null) {
                putColor(vertices, colors.getColor(c));
            }
        }
        vertices.rewind();
        indices.rewind();
    }

    /* */
    
    protected void fillBuffersWithRandom(int n, DrawableVBO drawable, FloatVBO vbo, ColorMapper colors) {
        fillBuffersWithRandom(n, drawable, vbo.getVertices(), vbo.getIndices(), vbo.getBounds(), colors);
    }
    
    protected void fillBuffersWithRandom(int n, DrawableVBO drawable, FloatBuffer vertices, IntBuffer indices, BoundingBox3d bounds, ColorMapper colors) {
        drawable.setHasColorBuffer(colors!=null);
        
        int size = 0;
        for (int i=0; i<n; i++) {
            
            float z = (float)Math.random()*100;
            float y = (float)Math.random()*1;

            indices.put(size++);
            Coord3d c1 = new Coord3d(i, 0, 0);
            putCoord(vertices, c1);
            putColor(vertices, Color.RED);
            bounds.add(c1);
            
            indices.put(size++);
            Coord3d c2 = new Coord3d(i, y, z);
            putCoord(vertices, c2);
            putColor(vertices, Color.RED);
            bounds.add(c2);
            
            /*if(colors!=null){
                putColor(vertices, colors.getColor(c1));
                putColor(vertices, colors.getColor(c2));
            }*/
        }
        vertices.rewind();
        indices.rewind();
    }
    
    /* */

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

    /* */
    protected int computeVerticeBufferSize(int type, int n, int dim, int size, boolean hasNormal, boolean hasColor) {
        if(type==GL.GL_LINES){
            return n * (dim *2) * size;// *2 lines
        }
        else{
            if (hasColor) {
                return n * (dim * 2) * size;// *2 colors
            }
            if (hasNormal) {
                return n * (dim * 2) * size;// *2 normals
            }
            
            return n * dim * size;
        }
    }
    
    protected int computeGeometrySize(DrawableVBO drawable) {
        if (drawable.getGeometry() == GL.GL_POINTS) {
            return 1;
        }
        if (drawable.getGeometry() == GL.GL_LINES) {
            return 2;
        }
        else if (drawable.getGeometry() == GL.GL_TRIANGLES) {
            return 3;
        }
        return 2;
    }
}