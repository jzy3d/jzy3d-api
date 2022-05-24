package org.jzy3d.plot3d.primitives.vbo.drawable.loaders;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.io.BufferUtil;
import org.jzy3d.io.IGLLoader;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Normal.NormalMode;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO2;
import com.jogamp.common.nio.Buffers;
import com.jogamp.common.nio.PointerBuffer;

/**
 * A utility class to build buffers to feed a {@link DrawableVBO2}. Reading VBO javadoc may be
 * helpful as well
 * 
 * Not all parameters are mandatory, so we depict the effect of providing or not some of them.
 * 
 * <h2>Points</h2>
 * 
 * This holds the x,y,z coordinates and is mandatory.
 * 
 * <h2>Colors</h2>
 * 
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
 * Normals allow processing light. There are four modes
 * 
 * <ul>
 * <li>{@link NormalMode.REPEATED} lead to autonomous processing of normals where each polygon get a
 * single normal processed for the whole geometry (but the normal is repeated for each point).
 * <li>{@link NormalMode.SHARED} lead to autonomous processing of normals where each point get a
 * normal processed as the average normal of all the polygons it belongs to.
 * <li>A normal array can be provided externally to rely on other algorithms.
 * <li>If neither {@link NormalMode} neither a normal array are provided, no normal is will be
 * buffered in the VBO.
 * </ul>
 * 
 * 
 * We observed that when no normal is processed at all, there is still an automatic processing of
 * light "somehow". This is enabled by toggling {@link DrawableVBO2#setComputeNormals(boolean)} to
 * false. In that case, the result looks like :
 * 
 * <img src="../doc-files/SHARED_VERTEX_NO_NORMAL.png"/>
 * 
 * <h2>Elements</h2>
 * 
 * Elements are optional and are used to avoid repeating intput points. In that case, geometries are
 * defined by the indices and the point they refer to.
 * 
 * @see {@link DrawableVBO2} constructor for all possible element index possibilities. See also
 *      {@link TestDrawableVBO2_glDrawElement} and {@link TestDrawableVBO2_glMultiDrawElements} for
 *      examples.
 * 
 * @author Martin Pernollet
 *
 */
public class VBOBufferLoaderForArrays extends VBOBufferLoader implements IGLLoader<DrawableVBO2> {
  protected static Logger log = LogManager.getLogger(VBOBufferLoaderForArrays.class);

  protected float[] points;
  protected int pointDimensions;

  protected float[] normals = null;

  // fields for glDrawElements
  protected int[] elements = null;
  protected int verticesPerGeometry = -1;

  // fields for glMultiDrawArrays
  protected int[] elementsStarts = null;
  protected int[] elementsLength = null;

  // fields for glMultiDrawElements
  protected int[][] elementsIndices = null;

  // fields for coloring and shading
  protected IColorMap colormap;
  protected float[] coloring;
  protected NormalMode normalMode;

  protected boolean debug = false;

  public VBOBufferLoaderForArrays(float[] points, int pointDimensions, int[] elementStarts,
      int[] elementLength, IColorMap colormap, float[] coloring, NormalMode normalMode) {
    super();
    this.points = points;
    this.pointDimensions = pointDimensions;
    this.colormap = colormap;
    this.coloring = coloring;
    this.normalMode = normalMode;

    this.elementsStarts = elementStarts;
    this.elementsLength = elementLength;
  }

  public VBOBufferLoaderForArrays(float[] points, int pointDimensions, int[][] elementIndices,
      IColorMap colormap, float[] coloring, NormalMode normalMode) {
    super();
    this.points = points;
    this.pointDimensions = pointDimensions;
    this.colormap = colormap;
    this.coloring = coloring;
    this.normalMode = normalMode;

    this.elementsIndices = elementIndices;
  }

  public VBOBufferLoaderForArrays(float[] points, int pointDimensions, int[][] elementIndices,
      IColorMap colormap, float[] coloring, float[] normals) {
    super();
    this.points = points;
    this.pointDimensions = pointDimensions;
    this.colormap = colormap;
    this.coloring = coloring;
    this.normals = normals;

    this.elementsIndices = elementIndices;
  }


  public VBOBufferLoaderForArrays(float[] points, int pointDimensions, int[] geometries,
      int verticesPerGeometry, IColorMap colormap, float[] coloring, NormalMode normalMode) {
    super();
    this.points = points;
    this.pointDimensions = pointDimensions;

    this.elements = geometries;
    this.verticesPerGeometry = verticesPerGeometry;

    this.colormap = colormap;
    this.coloring = coloring;
    this.normalMode = normalMode;

    this.elementsStarts = null;
    this.elementsLength = null;
  }



  @Override
  public void load(IPainter painter, DrawableVBO2 drawable) throws Exception {

    // Temporary list, for computing colors and normals later
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

    IntBuffer elementCountBuffer = null; // size of each geom (4 for quads)
    PointerBuffer elementIndicesBuffer = null; // index of index, will contain buffers

    if (elementsIndices != null) {
      elementIndicesBuffer = PointerBuffer.allocateDirect(elementsIndices.length);
      elementCountBuffer = Buffers.newDirectIntBuffer(elementsIndices.length);

      for (int i = 0; i < elementsIndices.length; i++) {
        IntBuffer elementDataBufferI = Buffers.newDirectIntBuffer(elementsIndices[i]);
        BufferUtil.rewind(elementDataBufferI);

        elementIndicesBuffer.referenceBuffer(elementDataBufferI);
        elementCountBuffer.put(elementsIndices[i].length);
      }

      BufferUtil.rewind(elementCountBuffer);
      elementIndicesBuffer.rewind();
    }

    // guess number of vertice with input data if it was no given ahead
    if (verticesPerGeometry < 0) {
      if (elementCountBuffer != null)
        verticesPerGeometry = elementCountBuffer.get(0);
      else if (elementLengthBuffer != null)
        verticesPerGeometry = elementLengthBuffer.get(0);
    }



    // -------------------------------------------
    // Normals

    FloatBuffer normalBuffer = null;

    if (drawable.isComputeNormals()) {

      // Compute averaged normals
      if (NormalMode.SHARED.equals(normalMode)) {
        if (elements != null) {
          normalBuffer = computeSharedNormals(elements, verticesPerGeometry, verticeList);
        } else if (elementsIndices != null) {
          normalBuffer = computeSharedNormals(elementsIndices, verticeList);
        }
      }

      // Compute repeated normals per geometry vertex
      else if (NormalMode.REPEATED.equals(normalMode)) {
        normalBuffer = computeSimpleNormals(verticesPerGeometry, verticeList);
      }

      // Provide normals externally
      else if (normalMode == null && normals != null) {
        normalBuffer = loadNormalsFromArray(normals);
      } else {
        drawable.setComputeNormals(false);
        log.warn(
            "No normal provided despite drawable is configured to receive processed or provided normal. Toggle normal mode of drawable to false.");
      }
    } else {
      // do not provide neither compute normals
    }


    // -------------------------------------------
    // Store data

    drawable.setHasNormalInVertexArray(false);
    drawable.setVerticesPerGeometry(verticesPerGeometry);

    if(colorBuffer!=null) {
      verifyColorBufferSizeConsistency(drawable, verticeBuffer, colorBuffer);      
    }

    // glMultiDrawElements
    if (elementCountBuffer != null && elementIndicesBuffer != null) {
      drawable.setData(painter, elementCountBuffer, elementIndicesBuffer, verticeBuffer,
          normalBuffer, colorBuffer, bounds);
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
  }

  protected void verifyColorBufferSizeConsistency(DrawableVBO2 drawable, FloatBuffer verticeBuffer,
      FloatBuffer colorBuffer) {
    float nColor = (1f * colorBuffer.capacity()) / (1f*drawable.getColorChannels());
    float nVertice = verticeBuffer.capacity() / 3f;

    if (nColor != nVertice) {
      String info =
          "Color & vertice number do not match. Check alpha channel configuration. colorBuf.size:"
              + colorBuffer.capacity() + " channels:" + drawable.getColorChannels() + " verticeBuf.size:"
              + verticeBuffer.capacity() + " nColor:" + nColor + " nVertice:" + nVertice;
      throw new RuntimeException(info);
      //log.error(info);
    }
  }
}
