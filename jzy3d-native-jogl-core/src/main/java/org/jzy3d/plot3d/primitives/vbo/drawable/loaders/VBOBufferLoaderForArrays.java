package org.jzy3d.plot3d.primitives.vbo.drawable.loaders;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.io.IGLLoader;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Normal.NormalMode;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.vbo.drawable.BufferUtil;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO2;
import com.jogamp.common.nio.Buffers;
import com.jogamp.common.nio.PointerBuffer;

/**
 * A utility class to build buffers to feed a {@link DrawableVBO2}.
 * 
 * @author Martin Pernollet
 *
 */
public class VBOBufferLoaderForArrays extends VBOBufferLoader implements IGLLoader<DrawableVBO2> {
  protected double[] points;
  protected int pointDimensions;

  protected int[] elements;
  protected int verticesPerGeometry;

  protected int[] elementsStarts;
  protected int[] elementsLength;

  protected int[] elementsCount;
  protected int[][] elementsIndices;

  protected IColorMap colormap;
  protected float[] coloring;
  protected NormalMode normalMode;

  protected boolean debug = false;


  public VBOBufferLoaderForArrays(double[] points, int pointDimensions, int[] elementStarts,
      int[] elementLength, int geometrySize, IColorMap colormap, float[] coloring,
      NormalMode normalMode) {
    super();
    this.points = points;
    this.pointDimensions = pointDimensions;
    this.colormap = colormap;
    this.coloring = coloring;
    this.normalMode = normalMode;

    this.elementsStarts = elementStarts;
    this.elementsLength = elementLength;
    this.verticesPerGeometry = geometrySize;
  }

  public VBOBufferLoaderForArrays(double[] points, int pointDimensions, int[] elementCount,
      int[][] elementIndices, int geometrySize, IColorMap colormap, float[] coloring,
      NormalMode normalMode) {
    super();
    this.points = points;
    this.pointDimensions = pointDimensions;
    this.colormap = colormap;
    this.coloring = coloring;
    this.normalMode = normalMode;

    this.elementsCount = elementCount;
    this.elementsIndices = elementIndices;
    this.verticesPerGeometry = geometrySize;
  }

  public VBOBufferLoaderForArrays(double[] points, int pointDimensions, int[][] elementIndices, int geometrySize, IColorMap colormap, float[] coloring,
      NormalMode normalMode) {
    super();
    this.points = points;
    this.pointDimensions = pointDimensions;
    this.colormap = colormap;
    this.coloring = coloring;
    this.normalMode = normalMode;

    this.elementsIndices = elementIndices;
    this.verticesPerGeometry = geometrySize;
  }


  /**
   * Return a loader for this VBO that is invoked upon {@link #mount(IPainter)}, meaning after the
   * application has started.
   * 
   * Not all parameters are mandatory, so we depict the effect of providing or not some of them.
   * 
   * <h2>Points</h2> This holds the x,y,z coordinates and is mandatory.
   * 
   * <h2>Colors</h2> The loader can be given
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
   * <li>If defined, geometries array is a collection of indices refering to the points array. This
   * avoid repeating the points but assumes that the drawn object is a STRIP or FAN, hence a single
   * geometry.
   * <li>If defined,
   * </ul>
   * 
   * @see {@link DrawableVBO2} constructor for argument description.
   */
  public VBOBufferLoaderForArrays(double[] points, int pointDimensions, int[] geometries,
      int verticesPerGeometry, IColorMap colormap, float[] coloring, NormalMode normalMode) {
    super();
    this.points = points;
    this.pointDimensions = pointDimensions;
    this.elements = geometries;

    this.colormap = colormap;
    this.coloring = coloring;
    this.normalMode = normalMode;

    this.elementsStarts = null;
    this.elementsLength = null;
    this.verticesPerGeometry = verticesPerGeometry;
  }

  @Override
  public void load(IPainter painter, DrawableVBO2 drawable) throws Exception {

    // Temporary list, for computing color and normals later
    List<Coord3d> verticeList = new ArrayList<>();
    BoundingBox3d bounds = new BoundingBox3d();

    // -------------------------------------------
    // Vertices

    FloatBuffer verticeBuffer = loadVerticesFromArray(points, pointDimensions, verticeList, bounds);

    // -------------------------------------------
    // Colors

    FloatBuffer colorBuffer = null;

    if (colormap != null && coloring != null) {
      throw new IllegalArgumentException(
          "Should either define colormap or colors array, or none, but not both");
    } else if (colormap != null) {
      colorBuffer =
          loadColorBufferFromColormap(verticeList, bounds, drawable.getColorChannels(), colormap);
    } else if (coloring != null) {
      colorBuffer = loadColorBufferFromArray(coloring);
    }

    // -------------------------------------------
    // Element Indices to build a single geometry

    IntBuffer elementBuffer = null;

    if (elements != null) {
      elementBuffer = Buffers.newDirectIntBuffer(elements);
      BufferUtil.rewind(elementBuffer);
    }

    // -------------------------------------------
    // Element Indices to build a multi-array VBO

    IntBuffer elementStartsBuffer = null;
    IntBuffer elementLengthBuffer = null;

    if (elementsStarts != null && elementsLength != null) {
      elementStartsBuffer = Buffers.newDirectIntBuffer(elementsStarts);
      BufferUtil.rewind(elementStartsBuffer);

      elementLengthBuffer = Buffers.newDirectIntBuffer(elementsLength);
      BufferUtil.rewind(elementLengthBuffer);
    }

    // -------------------------------------------
    // Element Indices to build a multi-element VBO

    IntBuffer elementCountBuffer = null;
    IntBuffer elementDataBuffer = null; // vertex index
    PointerBuffer elementIndicesBuffer = null; // index of index

    if (elementsIndices != null) {
      elementDataBuffer = Buffers.newDirectIntBuffer(size(elementsIndices));
      elementIndicesBuffer = PointerBuffer.allocateDirect(elementsIndices.length);//size(elementsIndices));
      
      if(elementsCount!=null) {
        elementCountBuffer = Buffers.newDirectIntBuffer(elementsCount);
      }
      else {
        elementCountBuffer = Buffers.newDirectIntBuffer(elementsIndices.length);
      }

      for (int i = 0; i < elementsIndices.length; i++) {
        IntBuffer elementDataBufferI =  Buffers.newDirectIntBuffer(elementsIndices[i]);
        BufferUtil.rewind(elementDataBufferI);
        
        elementIndicesBuffer.referenceBuffer(elementDataBufferI);
        
        if(elementsCount==null) {
          elementCountBuffer.put(elementsIndices[i].length);
        }
      }
      
      BufferUtil.rewind(elementDataBuffer);
      BufferUtil.rewind(elementDataBuffer);
      elementIndicesBuffer.rewind();
    }

    
    if(verticesPerGeometry<0) {
      if(elementCountBuffer!=null)
        verticesPerGeometry = elementCountBuffer.get(0);
      else if(elementLengthBuffer!=null)
        verticesPerGeometry = elementLengthBuffer.get(0);
    }

    

    // -------------------------------------------
    // Normals
    
    FloatBuffer normalBuffer = null;
    
    if (drawable.isComputeNormals()) {
      if (elements != null && NormalMode.SHARED.equals(normalMode)) {
        normalBuffer = computeSharedNormals(elements, verticesPerGeometry, verticeList);
      } else {
        normalBuffer = computeSimpleNormals(verticesPerGeometry, verticeList);
      }
    }


    // -------------------------------------------
    // Store data

    drawable.setHasNormalInVertexArray(false);
    drawable.setVerticesPerGeometry(verticesPerGeometry);
    // drawable.setColorChannels(4);

    // glMultiDrawElements
    if (elementCountBuffer != null && elementIndicesBuffer != null && elementDataBuffer!=null) {
      drawable.setData(painter, elementCountBuffer, elementIndicesBuffer, elementDataBuffer,
          verticeBuffer, normalBuffer, colorBuffer, bounds);
    } 
    
    // glMultiDrawArrays
    else if (elementStartsBuffer != null && elementLengthBuffer != null) {
      drawable.setData(painter, elementStartsBuffer, elementLengthBuffer, verticeBuffer,
          normalBuffer, colorBuffer, bounds);
    } 
    
    // glDrawElements
    else {
      drawable.setData(painter, elementBuffer, verticeBuffer, normalBuffer, colorBuffer, bounds);
    }

    // drawable.setHasNormalInVertexArray(true);
    // drawable.setData(painter, elements, verticeAndNormals, null, colors, bounds);
  }

  public int size(int[][] array) {
    int sz = 0;
    for (int i = 0; i < array.length; i++) {
      sz += array[i].length;
    }
    return sz;
  }
}
