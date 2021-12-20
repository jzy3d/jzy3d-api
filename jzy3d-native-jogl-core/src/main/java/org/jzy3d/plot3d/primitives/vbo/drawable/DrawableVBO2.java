package org.jzy3d.plot3d.primitives.vbo.drawable;


import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.io.IGLLoader;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Normal.NormalMode;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.primitives.Composite;
import org.jzy3d.plot3d.primitives.IGLBindedResource;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.Wireframeable;
import org.jzy3d.plot3d.primitives.vbo.drawable.loaders.VBOBufferLoader;
import org.jzy3d.plot3d.primitives.vbo.drawable.loaders.VBOBufferLoaderForArrays;
import org.jzy3d.plot3d.primitives.vbo.drawable.loaders.VBOBufferLoaderForPolygons;
import org.jzy3d.plot3d.rendering.lights.Light;
import org.jzy3d.plot3d.rendering.scene.Decomposition;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.transform.Transform;
import com.jogamp.common.nio.Buffers;
import com.jogamp.common.nio.PointerBuffer;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.fixedfunc.GLPointerFunc;

/**
 * A {@link DrawableVBO2} is able to efficiently draw a large collection of geometries.
 * 
 * 
 * <p>
 * <b>Effect of repeated vertices</b><br>
 * 
 * Repeated vertices make all vertice normal being processed with the only three vertices of a
 * triangle. A collection of neighbour triangles hence have normals producing sharp light reaction
 * as bellow.
 * 
 * <img src="doc-files/REPEATED_VERTEX_AND_NORMALS.png"/>
 * 
 * </p>
 * 
 * <p>
 * <b>Shared vertices between triangles based on element index</b><br>
 * 
 * Sharing vertices among triangles avoid repeating data, and also allows knowing all surrounding
 * triangles to a point, hence allowing to compute a normal based on the mean of all triangles
 * normal. This produce a smooth light reaction at the triangle edges.
 * 
 * <img src="doc-files/SHARED_VERTEX_AVERAGED_NORMALS.png"/>
 * </p>
 * 
 * <p>
 * <b>Not processing normals in java</b><br>
 * 
 * Is faster and yield to this light reaction.
 * 
 * <img src="doc-files/SHARED_VERTEX_NO_NORMAL.png"/>
 * </p>
 * 
 * <p>
 * <b>Using a colormap</b><br>
 * 
 * <img src="doc-files/COLORMAP.png"/>
 * </p>
 * 
 * <p>
 * <b>Initializing DrawableVBO2</b></br>
 * 
 * <pre>
 * <code>
 * AWTChartFactory f = new AWTChartFactory();
 * Chart chart = f.newChart(Quality.Intermediate());
 *
 * int dimensions = TestMesh.DIMENSIONS;
 * double[] vertices = TestMesh.makeArray4();
 * int[] elements = TestMesh.makeElementArray4();
 * int nVertices = TestMesh.nVertices(vertices);
 *
 * DrawableVBO2 vbo = new DrawableVBO2(vertices, dimensions, elements, null);
 * chart.add(vbo);
 * chart.open();
 * </code>
 * </pre>
 * </p>
 * 
 * @see {@link DrawableVBO2} constructor documentation for details.
 * 
 * 
 */
public class DrawableVBO2 extends Wireframeable implements IGLBindedResource {
  protected static Logger log = LogManager.getLogger(DrawableVBO2.class);
  
  /**
   * Primitive restart is NOT working for now. Kept here for further debugging
   * https://forum.jogamp.org/Using-glPrimitiveRestartIndex-to-declare-multiple-geometries-in-the-same-VBO-td4041307.html
   */
  public static int PRIMITIVE_RESTART_VALUE = -1;// 0xffffffff;

  protected IGLLoader<DrawableVBO2> loader;

  protected boolean hasNormalInVertexArray = false;

  protected boolean primitiveRestart = false;

  protected boolean debug = false;

  protected boolean computeNormals = true;

  // ---------------------------------------
  // Buffers to feed VBO in GPU memory
  // ---------------------------------------

  /**
   * The (direct) float buffer storing vertices in GPU. If none of the non mandatory element buffer
   * are defined, will render with {@link GL#glDrawArrays()}
   */
  protected FloatBuffer vertices;

  /** The (direct) float buffer storing normals in GPU. */
  protected FloatBuffer normals;

  /** The (direct) float buffer storing colors in GPU. */
  protected FloatBuffer colors;

  /**
   * A color buffer that should replace the current one, that may have been loaded outside the
   * rendering thread and that should be applied at the next draw loop.
   */
  FloatBuffer nextColorBuffer = null;


  /**
   * The (non-mandatory) int buffer storing geometry indices in GPU. If defined, will render with
   * {@link GL#glDrawElements()}
   */
  protected IntBuffer elements;

  /**
   * The (non-mandatory) int buffer storing geometry indices in GPU. If defined, will render with
   * {@link GL#glMultiDrawArrays}
   */
  protected IntBuffer elementsStarts;
  protected IntBuffer elementsLength;

  /**
   * The (non-mandatory) int buffer storing geometry indices in GPU. If defined, will render with
   * {@link GL#glMultiDrawElements}
   */
  protected IntBuffer elementsCount;
  protected PointerBuffer elementsIndices;


  // ---------------------------------------
  // Buffer offsets
  // ---------------------------------------

  /** Byte shift between two vertices in the vertex buffer. */
  protected int vertexOffset;
  /** Byte shift between to access a vertice normal in the vertex buffer. */
  protected int normalOffset;
  /** Number of element (geometries) in the element buffer. */
  protected int elementSize;

  protected int firstCoordOffset = 0;


  // ---------------------------------------
  // array ID generated by open GL
  // ---------------------------------------

  protected int[] colorArrayIds = new int[1];
  protected int[] vertexArrayIds = new int[1];
  protected int[] normalArrayIds = new int[1];
  protected int[] elementArrayIds = new int[1];

  protected boolean hasMountedOnce = false;

  protected static int QUAD_SIZE = 4;
  protected static int TRIANGLE_SIZE = 3;
  /** number of vertext per geometry, 3 for triangles, 4 for quads */
  protected static int GEOMETRY_SIZE = TRIANGLE_SIZE; // triangle

  /** number of dimensions for vertex, 3 for {x,y,z} */
  public static int VERTEX_DIMENSIONS = 3; // x,y,z

  /** number of vertex per geometry */
  protected int verticesPerGeometry = -1;

  /** number of channels per color, 3 for {r,g,b}, 4 for {r,g,b,a} */
  protected int colorChannels = 3;

  /** store the Geometry type in OpenGL, GL_TRIANGLES, GL_POLYGONS, etc */
  protected int glGeometryType;

  protected boolean hasColorBuffer = false;

  /** Default flat color for the complete geometry */
  protected Color color = new Color(1f, 0f, 1f, 0.75f);



  /**
   * Should be true AND the element array provided to be able to process averaged normal. If any of
   * these two conditions is not fullfilled, then one normal per point is computed.
   */
  protected NormalMode normalMode = NormalMode.REPEATED;



  /* ************************* glDrawArrays ************************ */
  /*                                                                 */
  /*                                                                 */
  /*                                                                 */
  /* *************************************************************** */


  /**
   * Initialize a VBO object with arrays with no colormap and no vertex sharing scheme. The object
   * has a uniform color given by {@link #setColor(Color)}.
   * 
   * When using a {@link Light}, the object will have edges looking sharp as shown on the picture
   * below. One can obtain smoother edges by avoiding vertex repetitions and instead define an
   * element array indicating which unique vertex should be used in each triangle. See
   * {@link DrawableVBO2(double[], int, int[]).
   * 
   * <img src="doc-files/REPEATED_VERTEX_AND_NORMALS.png"/>
   * 
   * @see other constructor for detailed arguments.
   * 
   */
  public DrawableVBO2(float[] points, int pointDimensions) {
    this(makeLoader(points, pointDimensions, null, GEOMETRY_SIZE, null, null, NormalMode.REPEATED));
  }

  /**
   * Initialize a VBO object with arrays with a colormap but no vertex sharing scheme.
   * 
   * <img src="doc-files/COLORMAP.png"/>
   * 
   * @see other constructor for detailed arguments.
   * 
   */
  public DrawableVBO2(float[] points, int pointDimensions, IColorMap colormap) {
    this(makeLoader(points, pointDimensions, null, GEOMETRY_SIZE, colormap, null,
        NormalMode.REPEATED));
  }


  /* *********************** glDrawElements ************************ */
  /*                                                                 */
  /*                                                                 */
  /*                                                                 */
  /* *************************************************************** */

  /**
   * Initialize a VBO object with arrays with the following content.
   * 
   * 
   * @param points contains an array of vertices
   *        <code>[x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, ..]</code>
   * @param pointDimensions indicate the number of dimension in the array for each vertex, as the
   *        input array may contain extra dimensions for each vertex, e.g. [x1, y1, z1, m1, x2, y2,
   *        z2, m2, ...]
   * @param elements contains reference to points that may be used in several elements. E.g. the
   *        array <code>[1, 3, 2, 4,
   *        3, 2, ...]</code> indicates that the two first triangles are sharing vertices 3 and 2.
   *        This assume the user of this class is aware that this class will build triangles made of
   *        3 vertices each. It may be null, in that case the <code>points</code> array will be
   *        drawn as a sequence of vertices defining elements, without sharing vertices between
   *        elements.
   * @param colormap defines how to color vertices. It may be null, in that case the object is
   *        colored by {@link #setColor(Color)} adn shaded by {@link Light}
   */
  public DrawableVBO2(float[] points, int pointDimensions, int[] elements, IColorMap colormap) {
    this(makeLoader(points, pointDimensions, elements, GEOMETRY_SIZE, colormap, null,
        NormalMode.SHARED));
  }

  /**
   * Initialize a VBO object with arrays with no colormap. The object has a uniform color given by
   * {@link #setColor(Color)}.
   * 
   * Providing an element array allows defining which points form a triangle without having to
   * repeat these points. When such constructor is used, shared vertices allow computing shared
   * normals for each point, hence each point normal is computed as a mean of the normals of all
   * triangles that share this point. The result is smoother light transition between polygons,
   * which differ from the light transition of repeated-vertex schemes as processed when the element
   * array is null (or when invoking {@link DrawableVBO2(double[], int)}
   * 
   * <img src="doc-files/SHARED_VERTEX_AVERAGED_NORMALS.png"/>
   * 
   * @see other constructor for detailed arguments.
   * 
   */
  public DrawableVBO2(float[] points, int pointDimensions, int[] elements) {
    this(makeLoader(points, pointDimensions, elements, GEOMETRY_SIZE, null, null,
        NormalMode.REPEATED));
  }

  public DrawableVBO2(float[] points, int pointDimensions, int[] elements, IColorMap colormap,
      NormalMode normalMode) {
    this(makeLoader(points, pointDimensions, elements, GEOMETRY_SIZE, colormap, null, normalMode));
  }

  /**
   * This constructor will only work correctly for triangle geometry. The elementSize parameter is
   * mainly used for processing normals. If one wish to draw multiple polygons of 4 or more point,
   * then use a loader suitable for this.
   * 
   * @param points
   * @param pointDimensions
   * @param elements
   * @param elementSize
   * @param colors
   */
  public DrawableVBO2(float[] points, int pointDimensions, int[] elements, int elementSize,
      float[] colors) {
    this(makeLoader(points, pointDimensions, elements, elementSize, null, colors,
        NormalMode.SHARED));
  }

  public DrawableVBO2(float[] points, int[] elements, int elementSize, float[] colors) {
    this(points, VERTEX_DIMENSIONS, elements, elementSize, colors);
  }


  /* ********************** glMultiDrawArray *********************** */
  /*                                                                 */
  /*                                                                 */
  /*                                                                 */
  /* *************************************************************** */


  public DrawableVBO2(float[] points, int[] elementStart, int[] elementLength, float[] colors) {
    this(points, VERTEX_DIMENSIONS, elementStart, elementLength, colors);
  }

  public DrawableVBO2(float[] points, int pointDimensions, int[] elementStart, int[] elementLength,
      float[] colors) {
    this(makeLoader(points, pointDimensions, elementStart, elementLength, colors,
        NormalMode.REPEATED));
  }

  /** 
   * Build a VBO out of simple polygons.
   * 
   * Assume the same number of vertices for all polygons given the list.
   */
  public DrawableVBO2(List<Polygon> polygons) {
    this(makeLoader(polygons, polygons.get(0).size()));
  }

  /** 
   * Build a VBO out of a composite made of simple polygons.
   * 
   * Assume the same number of vertices for all polygons given the list.
   */
  public DrawableVBO2(Composite composite) {
    this(Decomposition.getPolygonDecomposition(composite));
  }

  /** 
   * Build a VBO out of a list of composites made of simple polygons.
   * 
   * Assume the same number of vertices for all polygons given the list.
   */
  public static DrawableVBO2 fromComposites(List<Composite> composites) {
    return new DrawableVBO2(Decomposition.getPolygonDecomposition(composites));
  }

  /* ********************* glMultiDrawElements ********************** */
  /*                                                                 */
  /*                                                                 */
  /*                                                                 */
  /* *************************************************************** */


  public DrawableVBO2(float[] points, int[][] elementIndices, float[] colors) {
    this(points, VERTEX_DIMENSIONS, elementIndices, colors);
  }

  public DrawableVBO2(float[] points, int[][] elementIndices, float[] colors, float[] normals) {
    this(makeLoader(points, VERTEX_DIMENSIONS, elementIndices, colors, normals));
  }

  public DrawableVBO2(float[] points, int[][] elementIndices, float[] colors,
      NormalMode normalMode) {
    this(makeLoader(points, VERTEX_DIMENSIONS, elementIndices, colors, normalMode));
  }


  public DrawableVBO2(float[] points, int pointDimensions, int[][] elementIndices,
      float[] colors) {
    this(makeLoader(points, pointDimensions, elementIndices, colors, NormalMode.REPEATED));
  }


  /* ***************************************************************** */
  /* *********************** LOAD DRAWABLE *************************** */
  /* ***************************************************************** */

  /**
   * Initialize a VBO object with a customizable loader.
   */
  public DrawableVBO2(IGLLoader<DrawableVBO2> loader) {
    this.loader = loader;
  }


  public static VBOBufferLoaderForPolygons makeLoader(List<Polygon> polygons,
      int verticesPerGeometry) {
    return new VBOBufferLoaderForPolygons(polygons, verticesPerGeometry);
  }

  public static IGLLoader<DrawableVBO2> makeLoader(float[] points, int pointDimensions,
      int[][] elementIndices, float[] colors, NormalMode perVertex) {
    return new VBOBufferLoaderForArrays(points, pointDimensions, elementIndices, null, colors,
        perVertex);
  }

  public static IGLLoader<DrawableVBO2> makeLoader(float[] points, int pointDimensions,
      int[][] elementIndices, float[] colors, float[] normals) {
    return new VBOBufferLoaderForArrays(points, pointDimensions, elementIndices, null, colors,
        normals);
  }

  public static IGLLoader<DrawableVBO2> makeLoader(float[] points, int pointDimensions,
      int[] elements, int elementSize, IColorMap colormap, float[] coloring,
      NormalMode normalMode) {
    return new VBOBufferLoaderForArrays(points, pointDimensions, elements, elementSize, colormap,
        coloring, normalMode);
  }

  public static IGLLoader<DrawableVBO2> makeLoader(float[] points, int elementSize) {
    return new VBOBufferLoaderForArrays(points, VERTEX_DIMENSIONS, null, elementSize, null, null, null);
  }

  public static IGLLoader<DrawableVBO2> makeLoader(float[] points, int pointDimensions,
      int[] elementStart, int[] elementLength, float[] coloring, NormalMode normalMode) {
    return new VBOBufferLoaderForArrays(points, pointDimensions, elementStart, elementLength, null,
        coloring, normalMode);
  }


  /**
   * Invoke the loader. This is called by {@link Graph} when the application initialize and a
   * {@link GL} context is available for feeding the GPU with arrays.
   */
  @Override
  public void mount(IPainter painter) {
    try {
      if(!hasMountedOnce()) {
        loader.load(painter, this);
        hasMountedOnce = true;
      }
    } catch (Exception e) {
      LogManager.getLogger(DrawableVBO2.class).error(e, e);
      throw new RuntimeException(e);
      //e.printStackTrace();
    }
  }

  @Override
  public boolean hasMountedOnce() {
    return hasMountedOnce;
  }

  /* ***************************************************************** */
  /* ************************ SET DRAWABLE *************************** */
  /* ***************************************************************** */

  public void setData(IPainter painter, FloatBuffer vertices, FloatBuffer normals,
      FloatBuffer colors, BoundingBox3d bounds) {
    this.bbox = bounds;
    this.hasColorBuffer = colors != null;

    // indexing not used at all
    this.elements = null;
    this.elementsStarts = null;
    this.elementsLength = null;
    this.elementsIndices = null;
    this.elementsCount = null;


    GL gl = getGL(painter);

    applyPrimitiveRestartIfEnabled(gl);

    // -----------------------------------
    // Register data

    registerVertexAndNormalOffsets();
    registerVertices(gl, vertices);
    registerNormals(gl, normals);
    registerColors(gl, colors);
  }

  /**
   * Configure a VBO with vertices, colors, and indices describing vertex references for building
   * triangles.
   * 
   * This is supposed to be called by the {@link IGLLoader} when {@link #mount(IPainter)} is
   * invoked.
   * 
   * The result is that all non null provided buffers are binded to the GPU.
   * 
   * @see the constructor documentation for argument description.
   */
  public void setData(IPainter painter, IntBuffer elements, FloatBuffer vertices,
      FloatBuffer normals, FloatBuffer colors, BoundingBox3d bounds) {
    this.bbox = bounds;
    this.hasColorBuffer = colors != null;

    // use single element indexing
    this.elements = elements;
    this.elementsStarts = null;
    this.elementsLength = null;
    this.elementsIndices = null;
    this.elementsCount = null;


    GL gl = getGL(painter);

    applyPrimitiveRestartIfEnabled(gl);

    // -----------------------------------
    // Register data

    registerVertexAndNormalOffsets();
    registerVertices(gl, vertices);
    registerNormals(gl, normals);
    registerColors(gl, colors);
    registerElements(gl, elements);
  }

  public void setData(IPainter painter, IntBuffer elementsStarts, IntBuffer elementsLength,
      FloatBuffer vertices, FloatBuffer normals, FloatBuffer colors, BoundingBox3d bounds) {
    this.bbox = bounds;
    this.hasColorBuffer = colors != null;

    // use multi array drawing
    this.elementsStarts = elementsStarts;
    this.elementsLength = elementsLength;
    this.elementsIndices = null;
    this.elementsCount = null;
    this.elements = null;

    GL gl = getGL(painter);

    // -----------------------------------
    // Register data

    registerVertexAndNormalOffsets();
    registerVertices(gl, vertices);
    registerNormals(gl, normals);
    registerColors(gl, colors);
    // no element to register
  }

  public void setData(IPainter painter, IntBuffer elementsCount, PointerBuffer elementsIndices,
      FloatBuffer vertices, FloatBuffer normals, FloatBuffer colors, BoundingBox3d bounds) {
    this.vertices = vertices;
    this.normals = normals;
    this.colors = colors;
    this.bbox = bounds;
    this.hasColorBuffer = this.colors != null;

    // use multi element indexing
    this.elementsIndices = elementsIndices;
    this.elementsCount = elementsCount;

    this.elementsStarts = null;
    this.elementsLength = null;
    this.elements = null;

    GL gl = getGL(painter);

    // -----------------------------------
    // Register data

    registerVertexAndNormalOffsets();
    registerVertices(gl, vertices);
    registerNormals(gl, normals);
    registerColors(gl, colors);
    // registerElementsData(gl);
    // register multi-element?
  }

  protected void registerVertexAndNormalOffsets() {
    if (hasNormalInVertexArray) {
      vertexOffset = (VERTEX_DIMENSIONS * 2) * Buffers.SIZEOF_FLOAT; // (coord+normal)
    } else {
      vertexOffset = VERTEX_DIMENSIONS * Buffers.SIZEOF_FLOAT; // (coord only)
    }
    normalOffset = VERTEX_DIMENSIONS * Buffers.SIZEOF_FLOAT;
  }

  protected void registerVertices(GL gl, FloatBuffer newVertices) {
    if (newVertices != null) {
      int vertexSize = newVertices.capacity() * Buffers.SIZEOF_FLOAT;

      if(vertexArrayIds[0]==0)
        gl.glGenBuffers(1, vertexArrayIds, 0);
      
      gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexArrayIds[0]);
      gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexSize, newVertices, GL.GL_STATIC_DRAW);
      
      vertices = newVertices;
    }
  }

  protected void registerNormals(GL gl, FloatBuffer newNormals) {
    if (newNormals != null) {
      int normalSize = newNormals.capacity() * Buffers.SIZEOF_FLOAT;

      if(normalArrayIds[0]==0)
        gl.glGenBuffers(1, normalArrayIds, 0);
      
      gl.glBindBuffer(GL.GL_ARRAY_BUFFER, normalArrayIds[0]);
      gl.glBufferData(GL.GL_ARRAY_BUFFER, normalSize, newNormals, GL.GL_STATIC_DRAW);

      // activate light reflection for VBO filled with normals
      setReflectLight(true);
      
      normals = newNormals;
    }
  }

  protected void registerColors(GL gl, FloatBuffer newColors) {
    if (newColors != null) {
      int colorSize = newColors.capacity() * Buffers.SIZEOF_FLOAT;

      if(colorArrayIds[0]==0)
        gl.glGenBuffers(1, colorArrayIds, 0);
      
      gl.glBindBuffer(GL.GL_ARRAY_BUFFER, colorArrayIds[0]);
      gl.glBufferData(GL.GL_ARRAY_BUFFER, colorSize, newColors, GL.GL_STATIC_DRAW);
      
      colors = newColors;
    }
  }


  // for glDrawElements
  protected void registerElements(GL gl, IntBuffer newElements) {
    if (newElements != null) {
      elementSize = newElements.capacity();

      int indexSize = newElements.capacity() * Buffers.SIZEOF_INT;

      if(elementArrayIds[0]==0)
        gl.glGenBuffers(1, elementArrayIds, 0);
      
      gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementArrayIds[0]);
      gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indexSize, newElements, GL.GL_STATIC_DRAW);
      
      elements = newElements;
    }
  }

  /* ***************************************************************** */
  /* ********************** RENDER DRAWABLE ************************** */
  /* ***************************************************************** */

  @Override
  public void draw(IPainter painter) {
    if(!hasMountedOnce) {
      mount(painter);
    }
    
    if (hasMountedOnce) {
      
      if(nextColorBuffer!=null) {
        registerColors(getGL(painter), nextColorBuffer);
        nextColorBuffer = null;
      }
      
      doTransform(painter);
      doDrawElements(painter);
      doDrawBoundsIfDisplayed(painter);
    }
  }

  /**
   * Perform rendering of this VBO.
   * 
   * @param painter holds a GL instance to invoke the GPU.
   * 
   * @see http://www.opengl-tutorial.org/intermediate-tutorials/tutorial-9-vbo-indexing/
   * @see https://github.com/opengl-tutorials/ogl/blob/master/tutorial09_vbo_indexing/tutorial09.cpp
   */
  protected void doDrawElements(IPainter painter) {
    GL gl = getGL(painter);

    if (!gl.isGL2()) {
      throw new RuntimeException("Need a GL2 instance");
    }

    GL2 gl2 = gl.getGL2();

    // -----------------------------------
    // Prepare buffers

    // System.out.println("V id " + vertexArrayIds[0]);
    // System.out.println("E id " + elementArrayIds[0]);

    // Vertex buffer
    gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexArrayIds[0]);
    gl2.glVertexPointer(VERTEX_DIMENSIONS, GL.GL_FLOAT, vertexOffset, firstCoordOffset);
    gl2.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);

    // Element buffer
    if (elementArrayIds[0] != 0) {
      gl2.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementArrayIds[0]);
    }

    // Normal buffer
    if (normalArrayIds[0] != 0) {
      // Experimental : try providing explicit normals
      gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, normalArrayIds[0]);
      gl2.glNormalPointer(GL.GL_FLOAT, normalOffset, firstCoordOffset);
      gl2.glEnableClientState(GLPointerFunc.GL_NORMAL_ARRAY);
    } else {
      // "Automatic normals", not really sure they are correct
      // invoking gl.glDisable(GL2.GL_AUTO_NORMAL) does not disable them surprisingly
      gl2.glNormalPointer(GL.GL_FLOAT, vertexOffset, normalOffset);
      gl2.glEnableClientState(GLPointerFunc.GL_NORMAL_ARRAY);
    }

    // Color buffer
    if (hasColorBuffer) {
      gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, colorArrayIds[0]);
      gl2.glColorPointer(colorChannels, GL.GL_FLOAT, colorChannels * Buffers.SIZEOF_FLOAT,
          firstCoordOffset);
      gl2.glEnableClientState(GL2.GL_COLOR_ARRAY);
    } else {
      if(color!=null)
        gl2.glColor4f(color.r, color.g, color.b, color.a);
    }


    // -----------------------------------
    // Offset filling

    if (isPolygonOffsetFillEnable())
      polygonOffsetFillEnable(painter); // typo fixed in 2.0.1
    else
      polygonOffsetFillDisable(painter);


    // -----------------------------------
    // Draw faces

    if (isFaceDisplayed()) {
      gl2.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);

      doDrawGeometries(gl2);
    }

    // -----------------------------------
    // Draw wireframe

    if (isWireframeDisplayed()) {
      // disable coloring to use single color
      if (hasColorBuffer) {
        gl2.glDisableClientState(GL2.GL_COLOR_ARRAY);
      }

      Color c = getWireframeColor();

      gl2.glColor4f(c.r, c.g, c.b, c.a);
      gl2.glLineWidth(getWireframeWidth());

      gl2.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_LINE);

      doDrawGeometries(gl2);
    }

    // -----------------------------------
    // Disable

    gl2.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
    gl2.glDisableClientState(GLPointerFunc.GL_NORMAL_ARRAY);

    // disable coloring if it was not done before
    if (hasColorBuffer && !isWireframeDisplayed()) {
      gl2.glDisableClientState(GL2.GL_COLOR_ARRAY);
    }
  }

  /**
   * Actually do draw by applying the GL geometry defined by {@link #glGeometryType}.
   * 
   * Either use non indexed mode, simple index mode, or multi-index mode.
   * 
   * Expect glPolygonMode to be defined before to define if we are drawing wireframe or filling
   * polygon.
   */
  protected void doDrawGeometries(GL2 gl2) {

    applyPrimitiveRestartIfEnabled(gl2);

    // -----------------------------------------
    // Case of simple index mode

    if (elements != null && elementSize > 0) {
      gl2.glDrawElements(glGeometryType, elementSize, GL.GL_UNSIGNED_INT, firstCoordOffset);
    }

    // -----------------------------------------
    // Case of indexed multi array mode

    else if (elementsCount != null && elementsIndices != null) {

      if (debug)
        debugMultiDrawElements();

      gl2.glMultiDrawElements(glGeometryType, elementsCount, GL.GL_UNSIGNED_INT, elementsIndices,
          elementsIndices.capacity());
    }

    // -----------------------------------------
    // Case of non indexed multi array mode

    else if (elementsStarts != null && elementsLength != null) {

      if (debug)
        debugMultiDrawArray();

      gl2.glMultiDrawArrays(glGeometryType, elementsStarts, elementsLength,
          elementsStarts.capacity());
    }

    // -----------------------------------------
    // Case of non indexed mode (no vertex index defined)

    else {
      gl2.glDrawArrays(glGeometryType, 0, vertices.capacity());
    }

  }


  /**
   * Experimental - not working yet
   *
   * How it should work
   * 
   * @see https://www.khronos.org/opengl/wiki/Vertex_Rendering#Primitive_Restart
   * @see https://stackoverflow.com/questions/4386861/opengl-jogl-multiple-triangle-fans-in-a-vertex-array
   * @see https://stackoverflow.com/questions/26944959/opengl-separating-polygons-inside-vbo
   * 
   *      What happens for now
   * @see https://forum.jogamp.org/Using-glPrimitiveRestartIndex-to-declare-multiple-geometries-in-the-same-VBO-td4041307.html
   * 
   */
  protected void applyPrimitiveRestartIfEnabled(GL gl) {
    if (primitiveRestart) {
      if (gl.isGL2()) {
        GL2 gl2 = gl.getGL2();
        gl2.glEnable(GL2.GL_PRIMITIVE_RESTART);
        // gl2.glEnable(GL2.GL_PRIMITIVE_RESTART_FIXED_INDEX);
        gl2.glPrimitiveRestartIndex(PRIMITIVE_RESTART_VALUE);
      }
    }
  }


  /* ***************************************************************** */
  /* ************************** PROPERTIES *************************** */
  /* ***************************************************************** */

  public boolean isHasColorBuffer() {
    return hasColorBuffer;
  }

  public void setHasColorBuffer(boolean hasColorBuffer) {
    this.hasColorBuffer = hasColorBuffer;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public boolean hasNormalInVertexArray() {
    return hasNormalInVertexArray;
  }

  public void setHasNormalInVertexArray(boolean hasNormal) {
    this.hasNormalInVertexArray = hasNormal;
  }

  public int getColorChannels() {
    return colorChannels;
  }

  public void setColorChannels(int colorChannels) {
    this.colorChannels = colorChannels;    
  }

  @Override
  public void applyGeometryTransform(Transform transform) {
    LogManager.getLogger(DrawableVBO2.class).warn("not implemented");
  }

  @Override
  public void updateBounds() { // requires smart reload
    LogManager.getLogger(DrawableVBO2.class).warn("not implemented");
  }

  protected GL getGL(IPainter painter) {
    return ((NativeDesktopPainter) painter).getGL();
  }


  /* ***************************************************************** */
  /* **************************** BUFFERS **************************** */
  /* ***************************************************************** */

  public FloatBuffer getVertices() {
    return vertices;
  }

  public FloatBuffer getNormals() {
    return normals;
  }

  public FloatBuffer getColors() {
    return colors;
  }

  public IntBuffer getElements() {
    return elements;
  }

  public IntBuffer getElementsStarts() {
    return elementsStarts;
  }

  public IntBuffer getElementsLength() {
    return elementsLength;
  }

  public IntBuffer getElementsCount() {
    return elementsCount;
  }

  public PointerBuffer getElementsIndices() {
    return elementsIndices;
  }

  public int[] getColorArrayIds() {
    return colorArrayIds;
  }

  public int[] getVertexArrayIds() {
    return vertexArrayIds;
  }

  public int[] getNormalArrayIds() {
    return normalArrayIds;
  }

  public int[] getElementArrayIds() {
    return elementArrayIds;
  }

  public int getGLGeometryType() {
    return glGeometryType;
  }

  public void setGLGeometryType(int glGeometryType) {
    this.glGeometryType = glGeometryType;
  }

  public int getVerticesPerGeometry() {
    return verticesPerGeometry;
  }

  public void setVerticesPerGeometry(int geometrySize) {
    this.verticesPerGeometry = geometrySize;

    if (geometrySize == TRIANGLE_SIZE) {
      setGLGeometryType(GL.GL_TRIANGLES);
    } else if (geometrySize >= QUAD_SIZE) {
      setGLGeometryType(GL2.GL_TRIANGLE_FAN);
    } else if (geometrySize == 2) {
      setGLGeometryType(GL2.GL_LINE);
    } else {
      throw new IllegalArgumentException("Unsupported geometry size : " + geometrySize);
    }

  }

  /** Kept for prototyping, but not supported for now */
  public boolean isPrimitiveRestart() {
    return primitiveRestart;
  }

  /**
   * Kept for prototyping, but not supported for now. Do not change this setting. @see
   * {@link #applyPrimitiveRestartIfEnabled(GL)}
   */
  public void setPrimitiveRestart(boolean primitiveRestart) {
    this.primitiveRestart = primitiveRestart;
  }

  public boolean isComputeNormals() {
    return computeNormals;
  }

  /** If false, normals are not computed and light processing might depend on GPU capabilities */
  public void setComputeNormals(boolean computeNormals) {
    this.computeNormals = computeNormals;
  }

  /* ***************************************************************** */
  /* ************************ MODIFY BUFFERS ************************* */
  /* ***************************************************************** */

  /**
   * Set the next color buffer to apply to this VBO. The color will be updated at next rendering,
   * which may be forced with {@link Chart#render()}.
   */
  public void setColors(float[] colors) {
    VBOBufferLoader colorLoader = new VBOBufferLoader();
    nextColorBuffer = colorLoader.loadColorBufferFromArray(colors);
  }



  /* ***************************************************************** */
  /* ***************************** DEBUG ***************************** */
  /* ***************************************************************** */


  protected void debugMultiDrawElements() {
    System.out.println("glMultiDrawElements : count(" + elementsCount.capacity() + "), indices("
        + elementsIndices.capacity() + "), Vertices : " + vertices.capacity());

    for (int i = 0; i < elementsCount.capacity(); i++) {
      int count = elementsCount.get(i);
      long ptr = elementsIndices.get(i);

      System.out.print(count + " :" + ptr + "\t");


      IntBuffer ib = (IntBuffer) elementsIndices.getReferencedBuffer(i);

      for (int j = 0; j < ib.capacity(); j++) {
        System.out.print(ib.get(j) + "\t");
      }

      System.out.println();
    }
  }

  protected void debugMultiDrawArray() {
    System.out.println("glMultiDrawArray : starts, length");
    for (int i = 0; i < elementsStarts.capacity(); i++) {
      System.out.println(elementsStarts.get(i) + "\t:\t" + elementsLength.get(i));
    }
  }


}
