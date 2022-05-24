package org.jzy3d.emulgl.trials;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;

/**
 * TODO : stop my Animator properly TODO : single animator interface for all
 * 
 * TODO : why need to chart.getView().setBoundMode(ViewBoundMode.AUTO_FIT); TODO : why need to
 * q.setAlphaActivated(false); to avoid DARK ALPHA (problem not existing in POC)
 */
public class SurfaceDemoEmulGL_Alpha {

  static final float ALPHA_FACTOR = .50f;

  public static void main(String[] args) {
    // LoggerUtils.minimal();

    // ---------------------
    // JZY3D CONTENT

    Shape surface = surface();
    surface.setPolygonWireframeDepthTrick(true); // IMPORTANT FOR JGL TO RENDER COPLANAR POLYGON &
                                                 // EDGE WHEN ALPHA DISABLED

    EmulGLChartFactory factory = new EmulGLChartFactory();

    Quality q = Quality.Advanced(); // assez propre avec l'ancienne méthode de setQuality

    // Le mode Fastest blend la couleur mais ne fait pas le calcul de la transparence + WEIRD
    // WIREFRAME
    // Le mode Intermediate blend la couleur mais ne fait pas le calcul de la transparence + WEIRD
    // WIREFRAME -> Offset fill bug with glClearColor
    // Le mode Advanced active le calcul de la transparence mais BLACK BACKGROUND bug (surface
    // should be white)

    // READ THIS : https://opengl-notes.readthedocs.io/en/latest/topics/texturing/aliasing.html
    // ABOUT HOW ALPHA IS USED TO IMPLEMENT ANTI ALIASING
    // WHICH MAY EXPLAIN WHY NO ALPHA MAKE SURFACE UGLY WITH WIREFRAME
    q.setAlphaActivated(false);
    
    
    // ALPHA BUG
    // Also, q.setAlphaActivated(false) is what involve a WEIRD WIREFRAME effect

    // q.setSmoothEdge(true);
    // q.setSmoothPolygon(true);
    // q.setSmoothColor(false);

    // q.setSmoothColor(false);
    Chart chart = factory.newChart(q);
    chart.add(surface);
    chart.getView().setAxisDisplayed(false);
    chart.open();
    chart.getMouse();

  }


  private static Shape surface() {

    // ---------------------------
    // DEFINE SURFACE MATHS
    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return x * Math.sin(x * y);
      }
    };
    Range range = new Range(-3, 3);
    int steps = 50;

    // ---------------------------
    // CUSTOMIZE SURFACE BUILDER FOR JGL

    SurfaceBuilder builder = new SurfaceBuilder();

    Shape surface = builder.orthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);

    // surface.setPolygonOffsetFillEnable(false);

    ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1, ALPHA_FACTOR));// 0.65f));
    surface.setColorMapper(colorMapper);
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.BLACK);
    return surface;
  }

}
