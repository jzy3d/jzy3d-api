package org.jzy3d.plot3d.primitives.vbo.drawable.loaders;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.io.IGLLoader;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Normal.NormalMode;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO2;
import com.jogamp.common.nio.Buffers;

/**
 * A utility class to build buffers to feed a {@link DrawableVBO2}.
 * 
 * @author Martin Pernollet
 *
 */
public class VBOBufferLoaderForArrays extends VBOBufferLoader implements IGLLoader<DrawableVBO2> {
  double[] points;
  int pointDimensions;
  int[] geometries;
  int verticesPerGeometry;
  IColorMap colormap;
  float[] coloring;
  NormalMode normalMode;
  
  protected int[] multiGeometryStartIndices;
  protected int[] multiGeometryLengthes;
  
  public VBOBufferLoaderForArrays(double[] points, int pointDimensions, int[] multiGeometryStartIndices, int[] multiGeometryLengthes, int geometrySize,
      IColorMap colormap, float[] coloring, NormalMode normalMode) {
    super();
    this.points = points;
    this.pointDimensions = pointDimensions;
    this.multiGeometryStartIndices = multiGeometryStartIndices;
    this.multiGeometryLengthes = multiGeometryLengthes;
    this.verticesPerGeometry = geometrySize;
    this.colormap = colormap;
    this.coloring = coloring;
    this.normalMode = normalMode;
    
    this.geometries = null;
  }


  /**
   * Return a loader for this VBO that is invoked upon {@link #mount(IPainter)}, meaning after the
   * application has started.
   * 
   * Not all parameters are mandatory, so we depict the effect of providing or not some of them.
   * 
   * <h2>Points</h2>
   * This holds the x,y,z coordinates and is mandatory.
   * 
   * <h2>Colors</h2>
   * The loader can be given
   * <ul>
   * <li>A non null colormap with null colors array
   * <li>A non null color array with null colormap
   * <li>None of the above, in that case, the color will uniform to the whole drawable
   * <li>If both colormap and colors are given, an exception is thrown.
   * </ul>
   * 
   * <h2>Normals</h2>
   * 
   * We observed that when no normal is processed at all, there is still an automatic processing of
   * light "somehow". This is enabled by toggling {#link {@link #COMPUTE_NORMALS_IN_JAVA} to false.
   * In that case, the result looks like :
   * 
   * <img src="doc-files/SHARED_VERTEX_NO_NORMAL.png"/>
   * 
   * <h2>Geometries</h2>
   * 
   * <ul>
   * <li>VBO can render without geometries defined. geometry array can be null
   * <li>If defined, geometries array is a collection of indices refering to the points array. This avoid repeating the points but assumes that the drawn object is a STRIP or FAN, hence a single geometry.
   * <li>If defined, 
   * </ul>
   * 
   * @see {@link DrawableVBO2} constructor for argument description.
   */
  public VBOBufferLoaderForArrays(double[] points, int pointDimensions, int[] geometries, int verticesPerGeometry,
      IColorMap colormap, float[] coloring, NormalMode normalMode) {
    super();
    this.points = points;
    this.pointDimensions = pointDimensions;
    this.geometries = geometries;
    this.verticesPerGeometry = verticesPerGeometry;
    this.colormap = colormap;
    this.coloring = coloring;
    this.normalMode = normalMode;
    
    this.multiGeometryStartIndices = null;
    this.multiGeometryLengthes = null;
  }

  @Override
  public void load(IPainter painter, DrawableVBO2 drawable) throws Exception {
    
    // Temporary list, for computing color and normals later
    List<Coord3d> verticeList = new ArrayList<>();
    BoundingBox3d bounds = new BoundingBox3d();

    // -------------------------------------------
    // Vertices

    FloatBuffer vertices = loadVerticesFromArray(points, pointDimensions, verticeList, bounds);
System.out.println(verticeList.size());
    
    // -------------------------------------------
    // Colors

    // drawable.setColorChannels(4);
    
    FloatBuffer colors = null;
    
    if (colormap != null && coloring != null) {
      throw new IllegalArgumentException(
          "Should either define colormap or colors array, or none, but not both");
    } else if (colormap != null) {
      colors = loadColorBufferFromColormap(verticeList, bounds, drawable.getColorChannels(), colormap);
    } else if (coloring != null) {
      colors = loadColorBufferFromArray(coloring);
    }

    // -------------------------------------------
    // Element Indices to build a single geometry

    IntBuffer elements = null;

    if (geometries != null) {
      elements = Buffers.newDirectIntBuffer(geometries);
      elements.rewind();
    }

    // -------------------------------------------
    // Element Indices to build a multi-geometry list
    
    IntBuffer multiElementStartIndices = null;
    IntBuffer multiElementLengthes = null;
    
    if (multiGeometryStartIndices != null && multiGeometryLengthes!=null) {
      multiElementStartIndices = Buffers.newDirectIntBuffer(multiGeometryStartIndices);
      multiElementStartIndices.rewind();

      multiElementLengthes = Buffers.newDirectIntBuffer(multiGeometryStartIndices);
      multiElementLengthes.rewind();
    }
    

    // -------------------------------------------
    // Normals

    FloatBuffer normals = null;

    if (DrawableVBO2.COMPUTE_NORMALS_IN_JAVA) {
      if (geometries != null && NormalMode.SHARED.equals(normalMode)) {
        normals = computeSharedNormals(geometries, verticesPerGeometry, verticeList);
      } else {
        normals = computeSimpleNormals(verticesPerGeometry, verticeList);
      }
    }


    // -------------------------------------------
    // Store data

    drawable.setHasNormalInVertexArray(false);
    drawable.setVerticesPerGeometry(verticesPerGeometry);

    if (multiElementStartIndices != null && multiElementLengthes!=null) {
      drawable.setData(painter, multiElementStartIndices, multiElementLengthes, vertices, normals, colors, bounds);      
    }
    else {
      drawable.setData(painter, elements, vertices, normals, colors, bounds);
    }
    
    // drawable.setHasNormalInVertexArray(true);
    // drawable.setData(painter, elements, verticeAndNormals, null, colors, bounds);
  }
}
