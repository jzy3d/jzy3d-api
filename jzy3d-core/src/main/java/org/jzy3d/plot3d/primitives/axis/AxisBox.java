package org.jzy3d.plot3d.primitives.axis;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Vector3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.RenderMode;
import org.jzy3d.plot3d.primitives.PolygonFill;
import org.jzy3d.plot3d.primitives.PolygonMode;
import org.jzy3d.plot3d.primitives.axis.annotations.AxeAnnotation;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.ZAxisSide;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;
import org.jzy3d.plot3d.text.ITextRenderer;
import org.jzy3d.plot3d.text.align.Horizontal;
import org.jzy3d.plot3d.text.align.Vertical;
import org.jzy3d.plot3d.text.renderers.TextBitmapRenderer;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;


/**
 * The {@link AxisBox} displays a box with front face invisible and ticks labels.
 * 
 * @author Martin Pernollet
 */
public class AxisBox implements IAxis {
  static Logger LOGGER = LogManager.getLogger(AxisBox.class);
  

  protected static final int PRECISION = 6;

  protected View view;

  // use this text renderer to get occupied volume by text
  protected ITextRenderer textRenderer;
  protected AxisLabelRotator rotateLabel;
  protected AxisLabelProcessor labels;
  protected AxisTickProcessor ticks;
  protected IAxisLayout layout;

  protected BoundingBox3d boxBounds;
  protected BoundingBox3d wholeBounds;
  protected Coord3d center;
  protected Coord3d scale;

  protected float xrange;
  protected float yrange;
  protected float zrange;

  protected float quadx[][];
  protected float quady[][];
  protected float quadz[][];

  protected float normx[];
  protected float normy[];
  protected float normz[];

  protected float axeXx[][];
  protected float axeXy[][];
  protected float axeXz[][];
  protected float axeYx[][];
  protected float axeYy[][];
  protected float axeYz[][];
  protected float axeZx[][];
  protected float axeZy[][];
  protected float axeZz[][];

  protected int axeXquads[][];
  protected int axeYquads[][];
  protected int axeZquads[][];

  protected boolean quadIsHidden[];

  public static final int AXE_X = 0;
  public static final int AXE_Y = 1;
  public static final int AXE_Z = 2;
  
  protected boolean depthRangeTrick = true;
  
  /**
   * The higher the value, the more the line are far from the faces and hence
   * no z-fighting occurs between faces and lines. In case of higher value, line
   * will be display more often, but also lines that should be behind the polygon
   */
  public static float NO_OVERLAP_DEPTH_RATIO = 0.5f;

  protected List<AxeAnnotation> annotations = new ArrayList<AxeAnnotation>();

  protected SpaceTransformer spaceTransformer;

  public AxisBox(BoundingBox3d bbox) {
    this(bbox, new AxisLayout());
  }

  public AxisBox(BoundingBox3d bbox, IAxisLayout layout) {
    this.layout = layout;
    //if (bbox.valid())
      setAxe(bbox);
    //else
    //  setAxe(new BoundingBox3d(-1, 1, -1, 1, -1, 1));
    wholeBounds = new BoundingBox3d();
    textRenderer = new TextBitmapRenderer();
    rotateLabel = new AxisLabelRotator();
    labels = new AxisLabelProcessor(this);
    ticks = new AxisTickProcessor(this);
    init();
  }

  /**
   * Draws the AxeBox. The camera is used to determine which axis is closest to the ur point ov
   * view, in order to decide for an axis on which to diplay the tick values.
   */
  @Override
  public void draw(IPainter painter) {
    updateHiddenQuads(painter);

    doTransform(painter);
    drawFace(painter);

    doTransform(painter);
    drawGrid(painter);

    drawAnnotations(painter);

    doTransform(painter);
    drawTicksAndLabels(painter);
  }

  protected void drawAnnotations(IPainter painter) {
    synchronized (annotations) {
      for (AxeAnnotation a : annotations) {
        a.draw(painter, this);
      }
    }
  }

  /**
   * reset to identity and apply scaling
   * 
   * @param painter TODO
   */
  public void doTransform(IPainter painter) {
    painter.glLoadIdentity();
    painter.glScalef(scale.x, scale.y, scale.z);
  }

  public void cullingDisable(IPainter painter) {
    painter.glDisable_CullFace();
  }

  public void cullingEnable(IPainter painter) {
    painter.glEnable_CullFace();
    painter.glFrontFace_ClockWise();
    painter.glCullFace_Front();
  }

  /* */

  /* DRAW AXEBOX ELEMENTS */

  // FORMER NATIVE PRIMITIVE

  public void drawFace(IPainter painter) {
    if (layout.isFaceDisplayed()) {
      Color quadcolor = layout.getQuadColor();
      painter.glPolygonMode(PolygonMode.BACK, PolygonFill.FILL);

      painter.glColor4f(quadcolor.r, quadcolor.g, quadcolor.b, quadcolor.a);
      painter.glLineWidth(1.0f);
      painter.glEnable_PolygonOffsetFill();
      painter.glPolygonOffset(1.0f, 1.0f); // handle stippling
      drawCube(painter, RenderMode.RENDER);
      painter.glDisable_PolygonOffsetFill();
    }
  }

  public void drawGrid(IPainter painter) {
    Color gridcolor = layout.getGridColor();
    
    // Push far from camera, to ensure the axis grid
    // Will remain covered by surface
    if(depthRangeTrick)
      painter.glDepthRangef(NO_OVERLAP_DEPTH_RATIO, 1f);

    
    painter.glPolygonMode(PolygonMode.BACK, PolygonFill.LINE);
    painter.color(gridcolor);
    painter.glLineWidth(1);
    drawCube(painter, RenderMode.RENDER);

    // Draw grids on non hidden quads
    painter.glPolygonMode(PolygonMode.BACK, PolygonFill.LINE);
    painter.color(gridcolor);
    painter.glLineWidth(1);
    painter.glLineStipple(1, (short) 0xAAAA);

    painter.glEnable_LineStipple();
    for (int quad = 0; quad < 6; quad++)
      if (!quadIsHidden[quad])
        drawGridOnQuad(painter, quad);
    painter.glDisable_LineStipple();
    
    
    // Reset depth range
    
    if(depthRangeTrick)
      painter.glDepthRangef(0f, 1f);

  }

  ///////////////////////////

  protected void drawCube(IPainter painter, RenderMode mode) {
    for (int q = 0; q < 6; q++) {
      if (!getQuadIsHidden()[q]) { // makes culling useless!

        painter.glBegin_LineLoop();
        for (int v = 0; v < 4; v++) {
          painter.vertex(quadx[q][v], quady[q][v], quadz[q][v], spaceTransformer);
        }
        painter.glEnd();
      }
    }
  }

  // Draw a grid on the desired quad.
  protected void drawGridOnQuad(IPainter painter, int quad) {
    
    // Draw X grid along X axis
    if ((quad != 0) && (quad != 1)) {
      double[] xticks = layout.getXTicks();
      for (int t = 0; t < xticks.length; t++) {
        painter.glBegin_Line();
        painter.vertex((float) xticks[t], quady[quad][0], quadz[quad][0], spaceTransformer);
        painter.vertex((float) xticks[t], quady[quad][2], quadz[quad][2], spaceTransformer);
        painter.glEnd();
      }
    }
    // Draw Y grid along Y axis
    if ((quad != 2) && (quad != 3)) {
      double[] yticks = layout.getYTicks();
      for (int t = 0; t < yticks.length; t++) {
        painter.glBegin_Line();
        painter.vertex(quadx[quad][0], (float) yticks[t], quadz[quad][0], spaceTransformer);
        painter.vertex(quadx[quad][2], (float) yticks[t], quadz[quad][2], spaceTransformer);
        painter.glEnd();
      }
    }
    // Draw Z grid along Z axis
    if ((quad != 4) && (quad != 5)) {
      double[] zticks = layout.getZTicks();
      for (int t = 0; t < zticks.length; t++) {
        painter.glBegin_Line();
        painter.vertex(quadx[quad][0], quady[quad][0], (float) zticks[t], spaceTransformer);
        painter.vertex(quadx[quad][2], quady[quad][2], (float) zticks[t], spaceTransformer);
        painter.glEnd();
      }
    }
  }

  ///////////////////////////


  public void drawTicksAndLabels(IPainter painter) {
    wholeBounds.reset();
    wholeBounds.add(boxBounds);

    drawTicksAndLabelsX(painter);
    drawTicksAndLabelsY(painter);
    drawTicksAndLabelsZ(painter);
  }

  public void drawTicksAndLabelsX(IPainter painter) {
    if (xrange > 0 && layout.isXTickLabelDisplayed()) {

      // If we are on top, we make direct axe placement
      if (view != null && view.getViewMode().equals(ViewPositionMode.TOP)) {
        BoundingBox3d bbox =
            ticks.drawTicks(painter, 1, AXE_X, layout.getXTickColor(), Horizontal.LEFT, Vertical.TOP);
        wholeBounds.add(bbox);
      }
      // otherwise computed placement
      else {
        int xselect = findClosestXaxe(painter.getCamera());
        if (xselect >= 0) {
          BoundingBox3d bbox = ticks.drawTicks(painter, xselect, AXE_X, layout.getXTickColor());
          wholeBounds.add(bbox);
        } else {
          // HACK: handles "on top" view, when all face of cube are
          // drawn, which forbid to select an axe automatically
          BoundingBox3d bbox =
              ticks.drawTicks(painter, 2, AXE_X, layout.getXTickColor(), Horizontal.CENTER, Vertical.TOP);
          wholeBounds.add(bbox);
        }
      }
    }
  }

  public void drawTicksAndLabelsY(IPainter painter) {
    if (yrange > 0 && layout.isYTickLabelDisplayed()) {
      if (view != null && view.getViewMode().equals(ViewPositionMode.TOP)) {
        BoundingBox3d bbox =
            ticks.drawTicks(painter, 2, AXE_Y, layout.getYTickColor(), Horizontal.LEFT, Vertical.GROUND);
        wholeBounds.add(bbox);
      } else {
        int yselect = findClosestYaxe(painter.getCamera());
        if (yselect >= 0) {
          BoundingBox3d bbox = ticks.drawTicks(painter, yselect, AXE_Y, layout.getYTickColor());
          wholeBounds.add(bbox);
        } else {
          // HACK: handles "on top" view, when all face of cube are
          // drawn, which forbid to select an axe automatically
          BoundingBox3d bbox = ticks.drawTicks(painter, 1, AXE_Y, layout.getYTickColor(),
              Horizontal.RIGHT, Vertical.GROUND);
          wholeBounds.add(bbox);
        }
      }
    }
  }

  public void drawTicksAndLabelsZ(IPainter painter) {
    if (zrange > 0 && layout.isZTickLabelDisplayed()) {
      if (view != null && view.getViewMode().equals(ViewPositionMode.TOP)) {

      } else {
        int zselect = findClosestZaxe(painter.getCamera());
        if (zselect >= 0) {
          BoundingBox3d bbox = ticks.drawTicks(painter, zselect, AXE_Z, layout.getZTickColor());
          wholeBounds.add(bbox);
        }
      }
    }
  }

  ///////////////////////////

  protected boolean isZAxeLabelDisplayed(int direction) {
    return isZ(direction) && layout.isZAxeLabelDisplayed();
  }

  protected boolean isYAxeLabelDisplayed(int direction) {
    return isY(direction) && layout.isYAxeLabelDisplayed();
  }

  protected boolean isXAxeLabelDisplayed(int direction) {
    return isX(direction) && layout.isXAxeLabelDisplayed();
  }

  protected boolean isZ(int direction) {
    return direction == AXE_Z;
  }

  protected boolean isY(int direction) {
    return direction == AXE_Y;
  }

  protected boolean isX(int direction) {
    return direction == AXE_X;
  }

  protected double[] getAxisTicks(int direction) {
    if (isX(direction))
      return layout.getXTicks();
    else if (isY(direction))
      return layout.getYTicks();
    else
      return layout.getZTicks();
  }

  /* ************************************************/
  /* ************** AXIS SELECTION ******************/
  /* ************************************************/

  /**
   * Selects the closest displayable X axe from camera
   */
  protected int findClosestXaxe(Camera cam) {
    int na = 4;
    double[] distAxeX = new double[na];

    // keeps axes that are not at intersection of 2 quads
    for (int a = 0; a < na; a++) {
      if (quadIsHidden[axeXquads[a][0]] ^ quadIsHidden[axeXquads[a][1]])
        distAxeX[a] = new Vector3d(axeXx[a][0], axeXy[a][0], axeXz[a][0], axeXx[a][1], axeXy[a][1],
            axeXz[a][1]).distance(cam.getEye());
      else
        distAxeX[a] = Double.MAX_VALUE;
    }

    // prefers the lower one
    for (int a = 0; a < na; a++) {
      if (distAxeX[a] < Double.MAX_VALUE) {
        if (center.z > (axeXz[a][0] + axeXz[a][1]) / 2)
          distAxeX[a] *= -1;
      }
    }

    return min(distAxeX);
  }

  /**
   * Selects the closest displayable Y axe from camera
   */
  protected int findClosestYaxe(Camera cam) {
    int na = 4;
    double[] distAxeY = new double[na];

    // keeps axes that are not at intersection of 2 quads
    for (int a = 0; a < na; a++) {
      if (quadIsHidden[axeYquads[a][0]] ^ quadIsHidden[axeYquads[a][1]])
        distAxeY[a] = new Vector3d(axeYx[a][0], axeYy[a][0], axeYz[a][0], axeYx[a][1], axeYy[a][1],
            axeYz[a][1]).distance(cam.getEye());
      else
        distAxeY[a] = Double.MAX_VALUE;
    }

    // prefers the lower one
    for (int a = 0; a < na; a++) {
      if (distAxeY[a] < Double.MAX_VALUE) {
        if (center.z > (axeYz[a][0] + axeYz[a][1]) / 2)
          distAxeY[a] *= -1;
      }
    }

    return min(distAxeY);
  }

  /**
   * Selects the closest displayable Z axe from camera
   */
  protected int findClosestZaxe(Camera cam) {
    int na = 4;
    double[] distAxeZ = new double[na];

    // keeps axes that are not at intersection of 2 quads
    for (int a = 0; a < na; a++) {
      if (quadIsHidden[axeZquads[a][0]] ^ quadIsHidden[axeZquads[a][1]])
        distAxeZ[a] = new Vector3d(axeZx[a][0], axeZy[a][0], axeZz[a][0], axeZx[a][1], axeZy[a][1],
            axeZz[a][1]).distance(cam.getEye());
      else
        distAxeZ[a] = Double.MAX_VALUE;
    }

    // prefers the left or right one accoring to layout
    for (int a = 0; a < na; a++) {
      if (distAxeZ[a] < Double.MAX_VALUE) {
        Coord3d axeCenter = new Coord3d((axeZx[a][0] + axeZx[a][1]) / 2,
            (axeZy[a][0] + axeZy[a][1]) / 2, (axeZz[a][0] + axeZz[a][1]) / 2);


        if (ZAxisSide.LEFT.equals(layout.getZAxisSide())) {
          if (cam.side(axeCenter))
            distAxeZ[a] *= -1;
        } else {
          if (!cam.side(axeCenter))
            distAxeZ[a] *= -1;
        }

      }
    }

    return min(distAxeZ);
  }

  /**
   * Return the index of the minimum value contained in the input array of doubles. If no value is
   * smaller than Double.MAX_VALUE, the returned index is -1.
   */
  protected int min(double[] values) {
    double minv = Double.MAX_VALUE;
    int index = -1;

    for (int i = 0; i < values.length; i++)
      if (values[i] < minv) {
        minv = values[i];
        index = i;
      }
    return index;
  }

  /* */

  /* COMPUTATION OF HIDDEN QUADS */

  protected void updateHiddenQuads(IPainter painter) {
    quadIsHidden = getHiddenQuads(painter);
  }

  /**
   * Computes the visibility of each cube face.
   */
  protected boolean[] getHiddenQuads(IPainter painter) {
    Coord3d scaledEye = painter.getCamera().getEye().div(scale);
    return getHiddenQuads(scaledEye, center);
  }

  public boolean[] getHiddenQuads(Coord3d scaledEye, Coord3d center) {
    boolean[] status = new boolean[6];

    if (scaledEye.x <= center.x) {
      status[0] = false;
      status[1] = true;
    } else {
      status[0] = true;
      status[1] = false;
    }
    if (scaledEye.y <= center.y) {
      status[2] = false;
      status[3] = true;
    } else {
      status[2] = true;
      status[3] = false;
    }
    if (scaledEye.z <= center.z) {
      status[4] = false;
      status[5] = true;
    } else {
      status[4] = true;
      status[5] = false;
    }
    return status;
  }

  /* ************************************************/
  /* ************** CONFIGURE AXIS ******************/
  /* ************************************************/

  /**
   * Set the parameters and data of the AxeBox.
   */
  protected void setAxeBox(float xmin, float xmax, float ymin, float ymax, float zmin, float zmax) {
    // Compute center
    center = new Coord3d((xmax + xmin) / 2, (ymax + ymin) / 2, (zmax + zmin) / 2);
    xrange = xmax - xmin;
    yrange = ymax - ymin;
    zrange = zmax - zmin;

    // Define configuration of 6 quads (faces of the box)
    quadx = new float[6][4];
    quady = new float[6][4];
    quadz = new float[6][4];

    // x near
    quadx[0][0] = xmax;
    quady[0][0] = ymin;
    quadz[0][0] = zmax;
    quadx[0][1] = xmax;
    quady[0][1] = ymin;
    quadz[0][1] = zmin;
    quadx[0][2] = xmax;
    quady[0][2] = ymax;
    quadz[0][2] = zmin;
    quadx[0][3] = xmax;
    quady[0][3] = ymax;
    quadz[0][3] = zmax;
    // x far
    quadx[1][0] = xmin;
    quady[1][0] = ymax;
    quadz[1][0] = zmax;
    quadx[1][1] = xmin;
    quady[1][1] = ymax;
    quadz[1][1] = zmin;
    quadx[1][2] = xmin;
    quady[1][2] = ymin;
    quadz[1][2] = zmin;
    quadx[1][3] = xmin;
    quady[1][3] = ymin;
    quadz[1][3] = zmax;
    // y near
    quadx[2][0] = xmax;
    quady[2][0] = ymax;
    quadz[2][0] = zmax;
    quadx[2][1] = xmax;
    quady[2][1] = ymax;
    quadz[2][1] = zmin;
    quadx[2][2] = xmin;
    quady[2][2] = ymax;
    quadz[2][2] = zmin;
    quadx[2][3] = xmin;
    quady[2][3] = ymax;
    quadz[2][3] = zmax;
    // y far
    quadx[3][0] = xmin;
    quady[3][0] = ymin;
    quadz[3][0] = zmax;
    quadx[3][1] = xmin;
    quady[3][1] = ymin;
    quadz[3][1] = zmin;
    quadx[3][2] = xmax;
    quady[3][2] = ymin;
    quadz[3][2] = zmin;
    quadx[3][3] = xmax;
    quady[3][3] = ymin;
    quadz[3][3] = zmax;
    // z top
    quadx[4][0] = xmin;
    quady[4][0] = ymin;
    quadz[4][0] = zmax;
    quadx[4][1] = xmax;
    quady[4][1] = ymin;
    quadz[4][1] = zmax;
    quadx[4][2] = xmax;
    quady[4][2] = ymax;
    quadz[4][2] = zmax;
    quadx[4][3] = xmin;
    quady[4][3] = ymax;
    quadz[4][3] = zmax;
    // z down
    quadx[5][0] = xmax;
    quady[5][0] = ymin;
    quadz[5][0] = zmin;
    quadx[5][1] = xmin;
    quady[5][1] = ymin;
    quadz[5][1] = zmin;
    quadx[5][2] = xmin;
    quady[5][2] = ymax;
    quadz[5][2] = zmin;
    quadx[5][3] = xmax;
    quady[5][3] = ymax;
    quadz[5][3] = zmin;

    // Define configuration of each quad's normal
    normx = new float[6];
    normy = new float[6];
    normz = new float[6];

    normx[0] = xmax;
    normy[0] = 0;
    normz[0] = 0;
    normx[1] = xmin;
    normy[1] = 0;
    normz[1] = 0;
    normx[2] = 0;
    normy[2] = ymax;
    normz[2] = 0;
    normx[3] = 0;
    normy[3] = ymin;
    normz[3] = 0;
    normx[4] = 0;
    normy[4] = 0;
    normz[4] = zmax;
    normx[5] = 0;
    normy[5] = 0;
    normz[5] = zmin;

    // Define quad intersections that generate an axe
    // axe{A}quads[i][q]
    // A = axe direction (X, Y, or Z)
    // i = axe id (0 to 4)
    // q = quad id (0 to 1: an intersection is made of two quads)
    int na = 4; // n axes per dimension
    int np = 2; // n points for an axe
    int nq = 2;
    int i; // axe id

    axeXquads = new int[na][nq];
    axeYquads = new int[na][nq];
    axeZquads = new int[na][nq];

    i = 0;
    axeXquads[i][0] = 4;
    axeXquads[i][1] = 3; // quads making axe x0
    i = 1;
    axeXquads[i][0] = 3;
    axeXquads[i][1] = 5; // quads making axe x1
    i = 2;
    axeXquads[i][0] = 5;
    axeXquads[i][1] = 2; // quads making axe x2
    i = 3;
    axeXquads[i][0] = 2;
    axeXquads[i][1] = 4; // quads making axe x3
    i = 0;
    axeYquads[i][0] = 4;
    axeYquads[i][1] = 0; // quads making axe y0
    i = 1;
    axeYquads[i][0] = 0;
    axeYquads[i][1] = 5; // quads making axe y1
    i = 2;
    axeYquads[i][0] = 5;
    axeYquads[i][1] = 1; // quads making axe y2
    i = 3;
    axeYquads[i][0] = 1;
    axeYquads[i][1] = 4; // quads making axe y3
    i = 0;
    axeZquads[i][0] = 3;
    axeZquads[i][1] = 0; // quads making axe z0
    i = 1;
    axeZquads[i][0] = 0;
    axeZquads[i][1] = 2; // quads making axe z1
    i = 2;
    axeZquads[i][0] = 2;
    axeZquads[i][1] = 1; // quads making axe z2
    i = 3;
    axeZquads[i][0] = 1;
    axeZquads[i][1] = 3; // quads making axe z3

    // Define configuration of 4 axe per dimension:
    // axe{A}d[i][p], where
    //
    // A = axe direction (X, Y, or Z)
    // d = dimension (x coordinate, y coordinate or z coordinate)
    // i = axe id (0 to 4)
    // p = point id (0 to 1)
    //
    // Note: the points making an axe are from - to +
    // (i.e. direction is given by p0->p1)

    axeXx = new float[na][np];
    axeXy = new float[na][np];
    axeXz = new float[na][np];
    axeYx = new float[na][np];
    axeYy = new float[na][np];
    axeYz = new float[na][np];
    axeZx = new float[na][np];
    axeZy = new float[na][np];
    axeZz = new float[na][np];

    i = 0; // axe x0
    axeXx[i][0] = xmin;
    axeXy[i][0] = ymin;
    axeXz[i][0] = zmax;
    axeXx[i][1] = xmax;
    axeXy[i][1] = ymin;
    axeXz[i][1] = zmax;
    i = 1; // axe x1
    axeXx[i][0] = xmin;
    axeXy[i][0] = ymin;
    axeXz[i][0] = zmin;
    axeXx[i][1] = xmax;
    axeXy[i][1] = ymin;
    axeXz[i][1] = zmin;
    i = 2; // axe x2
    axeXx[i][0] = xmin;
    axeXy[i][0] = ymax;
    axeXz[i][0] = zmin;
    axeXx[i][1] = xmax;
    axeXy[i][1] = ymax;
    axeXz[i][1] = zmin;
    i = 3; // axe x3
    axeXx[i][0] = xmin;
    axeXy[i][0] = ymax;
    axeXz[i][0] = zmax;
    axeXx[i][1] = xmax;
    axeXy[i][1] = ymax;
    axeXz[i][1] = zmax;
    i = 0; // axe y0
    axeYx[i][0] = xmax;
    axeYy[i][0] = ymin;
    axeYz[i][0] = zmax;
    axeYx[i][1] = xmax;
    axeYy[i][1] = ymax;
    axeYz[i][1] = zmax;
    i = 1; // axe y1
    axeYx[i][0] = xmax;
    axeYy[i][0] = ymin;
    axeYz[i][0] = zmin;
    axeYx[i][1] = xmax;
    axeYy[i][1] = ymax;
    axeYz[i][1] = zmin;
    i = 2; // axe y2
    axeYx[i][0] = xmin;
    axeYy[i][0] = ymin;
    axeYz[i][0] = zmin;
    axeYx[i][1] = xmin;
    axeYy[i][1] = ymax;
    axeYz[i][1] = zmin;
    i = 3; // axe y3
    axeYx[i][0] = xmin;
    axeYy[i][0] = ymin;
    axeYz[i][0] = zmax;
    axeYx[i][1] = xmin;
    axeYy[i][1] = ymax;
    axeYz[i][1] = zmax;
    i = 0; // axe z0
    axeZx[i][0] = xmax;
    axeZy[i][0] = ymin;
    axeZz[i][0] = zmin;
    axeZx[i][1] = xmax;
    axeZy[i][1] = ymin;
    axeZz[i][1] = zmax;
    i = 1; // axe z1
    axeZx[i][0] = xmax;
    axeZy[i][0] = ymax;
    axeZz[i][0] = zmin;
    axeZx[i][1] = xmax;
    axeZy[i][1] = ymax;
    axeZz[i][1] = zmax;
    i = 2; // axe z2
    axeZx[i][0] = xmin;
    axeZy[i][0] = ymax;
    axeZz[i][0] = zmin;
    axeZx[i][1] = xmin;
    axeZy[i][1] = ymax;
    axeZz[i][1] = zmax;
    i = 3; // axe z3
    axeZx[i][0] = xmin;
    axeZy[i][0] = ymin;
    axeZz[i][0] = zmin;
    axeZx[i][1] = xmin;
    axeZy[i][1] = ymin;
    axeZz[i][1] = zmax;

    layout.getXTicks(xmin, xmax); // prepare ticks to display in the layout
                                  // tick buffer
    layout.getYTicks(ymin, ymax);
    layout.getZTicks(zmin, zmax);
    /*
     * setXTickMode(TICK_REGULAR, 3);5 setYTickMode(TICK_REGULAR, 3);5 setZTickMode(TICK_REGULAR,
     * 5);6
     */
  }
  
  /* ************************************************/
  /* ************ AXIS GETTER/SETTER ****************/
  /* ************************************************/


  protected void init() {
    setScale(new Coord3d(1.0f, 1.0f, 1.0f));
  }

  @Override
  public void dispose() {}

  @Override
  public ITextRenderer getTextRenderer() {
    return textRenderer;
  }

  @Override
  public void setTextRenderer(ITextRenderer renderer) {
    textRenderer = renderer;
  }

  public View getView() {
    return view;
  }

  @Override
  public BoundingBox3d getBounds() {
    return boxBounds;
  }

  @Override
  public IAxisLayout getLayout() {
    return layout;
  }

  @Override
  public SpaceTransformer getSpaceTransformer() {
    return spaceTransformer;
  }

  @Override
  public void setSpaceTransformer(SpaceTransformer spaceTransformer) {
    this.spaceTransformer = spaceTransformer;
  }

  /**
   * When setting a current view, the AxeBox can know the view is on mode CameraMode.TOP, and
   * optimize some axis placement.
   */
  public void setView(View view) {
    this.view = view;
  }

  @Override
  public void setAxe(BoundingBox3d bbox) {
    this.boxBounds = bbox;
    // LOGGER.info(bbox);
    setAxeBox(bbox.getXmin(), bbox.getXmax(), bbox.getYmin(), bbox.getYmax(), bbox.getZmin(),
        bbox.getZmax());
  }

  /**
   * Return the boundingBox of this axis, including the volume occupied by the texts. This requires
   * calling {@link draw()} before, which computes actual ticks position in 3d, and updates the
   * bounds.
   */
  @Override
  public BoundingBox3d getWholeBounds() {
    return wholeBounds;
  }

  @Override
  public Coord3d getCenter() {
    return center;
  }

  /**
   * Set the scaling factor that are applyed on this object before GL2 commands.
   */
  @Override
  public void setScale(Coord3d scale) {
    this.scale = scale;
  }

  @Override
  public Coord3d getScale() {
    return scale;
  }

  @Override
  public List<AxeAnnotation> getAnnotations() {
    return annotations;
  }

  @Override
  public void setAnnotations(List<AxeAnnotation> annotations) {
    this.annotations = annotations;
  }

  public void addAnnotation(AxeAnnotation annotation) {
    synchronized (annotations) {
      annotations.add(annotation);
    }
  }

  public float[][] getQuadX() {
    return quadx;
  }

  public float[][] getQuadY() {
    return quady;
  }

  public float[][] getQuadZ() {
    return quadz;
  }

  public boolean[] getQuadIsHidden() {
    return quadIsHidden;
  }

  public BoundingBox3d.Corners getCorners() {
    return getBounds().getCorners();
  }

}
