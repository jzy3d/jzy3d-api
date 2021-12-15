package org.jzy3d.debugGL.tracers;


import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.primitives.CubeComposite;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.legends.overlay.Legend;
import org.jzy3d.plot3d.rendering.legends.overlay.LegendLayout;
import org.jzy3d.plot3d.rendering.legends.overlay.LegendLayout.Corner;
import org.jzy3d.plot3d.rendering.legends.overlay.OverlayLegendRenderer;
import org.jzy3d.plot3d.rendering.lights.Light;
import org.jzy3d.plot3d.rendering.view.AWTRenderer2d;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

/**
 * Display elements of a 3d scene
 * <ul>
 * <li>axis box
 * <li>camera parameters (eye, target, up)
 * </ul>
 * 
 * @author martin
 */
public class DebugGLChart3d {

  // watched
  Chart watchedChart;
  SpaceTransformer spaceTransform;

  // debug
  Chart debugChart;
  Point cameraEye;
  Point cameraTarget;
  Point cameraUp;
  CubeComposite viewBox;
  CubeComposite axisBox;
  
  boolean watchCameraUp = false;

  Point[] lightPositions = new Point[8];

  int cameraPointWidth = 10;
  Color cameraEyeColor = Color.RED;
  Color cameraTargetColor = Color.GREEN;
  Color cameraUpColor = Color.MAGENTA;

  int axisWireframeWidth = 3;
  Color axisWireframeColor = Color.YELLOW;
  Color axisFaceColor = Color.YELLOW.alphaSelf(0.2f);

  int viewWireframeWidth = 2;
  Color viewWireframeColor = Color.BLUE;
  Color viewFaceColor = Color.BLUE.alphaSelf(0.2f);

  int lightPointWidth = 20;
  Color lightColor = Color.ORANGE;
  

  Font infoFont = new Font("Arial", Font.PLAIN, 8);


  public DebugGLChart3d(Chart watchedChart, ChartFactory debugChartFactory) {
    this.watchedChart = watchedChart;

    this.debugChart = debugChartFactory.newChart(Quality.Advanced().setAnimated(true));
    this.debugChart.getView().setSquared(false);
    
    spaceTransform = watchedChart.getView().getSpaceTransformer();

    //watchViewBounds();
    watchAxis();
    watchCamera();
    watchLights();

    watchedItemsTextOverlay(watchedChart);

    // ((AWTView) debugChart.getView()).addRenderer2d(renderer);

    OverlayLegendRenderer legend = watchedItemsColorLegend();
    LegendLayout layout = new LegendLayout();
    layout.corner = Corner.TOP_LEFT;

    legend.setLayout(layout);
    ((AWTChart) debugChart).addRenderer(legend);

  }

  protected OverlayLegendRenderer watchedItemsColorLegend() {
    List<Legend> infos = new ArrayList<Legend>();
    infos.add(new Legend("Camera.eye", cameraEyeColor));
    infos.add(new Legend("Camera.target", cameraTargetColor));
    infos.add(new Legend("Camera.up", cameraUpColor));
    infos.add(new Legend("Axis", axisWireframeColor));
    infos.add(new Legend("View", viewWireframeColor));
    infos.add(new Legend("Light", lightColor));

    OverlayLegendRenderer legend = new OverlayLegendRenderer(infos);
    return legend;
  }

  protected AWTRenderer2d watchedItemsTextOverlay(Chart watchedChart) {
    AWTRenderer2d renderer = new AWTRenderer2d() {
      @Override
      public void paint(Graphics g, int canvasWidth, int canvasHeight) {

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(java.awt.Color.BLACK);
        g2d.setFont(infoFont);

        if (watchedChart != null && watchedChart.getView() != null
            && watchedChart.getView().getCamera() != null) {
          g2d.drawString("watched.near=" + watchedChart.getView().getCamera().getNear(), 5, 20);
          g2d.drawString("watched.far=" + watchedChart.getView().getCamera().getFar(), 5, 40);
          g2d.drawString(
              "watched.radius=" + watchedChart.getView().getCamera().getRenderingSphereRadius(), 5,
              60);
          g2d.drawString("watched.up.z=" + watchedChart.getView().getCamera().getUp().z, 5, 80);
          g2d.drawString("watched.axe=" + watchedChart.getView().getAxis().getBounds().toString(),
              5, 100);
          g2d.drawString("transformed axe=" + axisBox.getBounds().toString(), 5, 120);
        }
      }
    };
    return renderer;
  }

  public void watchViewBounds() {
    BoundingBox3d viewBounds = watchedChart.getView().getBounds();
    if (spaceTransform != null) {
      viewBounds = spaceTransform.compute(viewBounds);
    }
    viewBox = new CubeComposite(viewBounds, viewWireframeColor, viewFaceColor);
    viewBox.setFaceDisplayed(false);
    viewBox.setWireframeWidth(viewWireframeWidth);

    debugChart.add(viewBox);
  }

  public void watchAxis() {
    BoundingBox3d axisBounds = watchedChart.getView().getAxis().getBounds();
    if (spaceTransform != null) {
      axisBounds = spaceTransform.compute(axisBounds);
    }
    System.out.println(axisBounds);
    axisBox = new CubeComposite(axisBounds, axisWireframeColor, axisFaceColor);
    //axisBox.setFaceDisplayed(false);
    axisBox.setWireframeWidth(axisWireframeWidth);

    debugChart.add(axisBox);
  }

  public void watchCamera() {
    cameraEye = new Point(watchedChart.getView().getCamera().getEye(), cameraEyeColor);
    cameraEye.setWidth(cameraPointWidth);

    cameraTarget = new Point(watchedChart.getView().getCamera().getTarget(), cameraTargetColor);
    cameraTarget.setWidth(cameraPointWidth);

    debugChart.add(cameraEye);
    debugChart.add(cameraTarget);
    
    if(watchCameraUp) {
      cameraUp = new Point(watchedChart.getView().getCamera().getUp(), cameraUpColor);
      cameraUp.setWidth(cameraPointWidth);
      debugChart.add(cameraUp);
    }

  }

  public void watchLights() {
    List<Light> lights = watchedChart.getScene().getLightSet().getLights();

    for (int i = 0; i < lights.size(); i++) {
      lightPositions[i] = new Point(lights.get(i).getPosition(), lightColor);
      lightPositions[i].setWidth(lightPointWidth);
      
      debugChart.add(lightPositions[i]);
    }

  }

  public void open(Rectangle rectangle) {
    debugChart.open("GL Debug", rectangle);
    debugChart.addMouseCameraController();

    startUpdater();
  }

  protected void startUpdater() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (watchedChart != null && watchedChart.getView() != null) {
          watchCameraUpdate();
          watchLightsUpdate();
          
          if (debugChart.getView() != null) {
            debugChart.getView().setBoundMode(ViewBoundMode.AUTO_FIT);
            debugChart.getView().updateBounds();
            //debugChart.getView().setBoundManual(new BoundingBox3d(-1,1,-1,1,-1,1));
            //debugChart.getView().set
            //System.out.println(debugChart.getView().getBounds());

          }
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }).start();
  }

  public void watchCameraUpdate() {
    if(watchedChart.getView().getCamera()==null)
      return;
    
    Coord3d eye = watchedChart.getView().getCamera().getEye();
    Coord3d target = watchedChart.getView().getCamera().getTarget();
    
    cameraEye.setData(eye);
    cameraTarget.setData(target);
    
    if(watchCameraUp) {
      Coord3d up = watchedChart.getView().getCamera().getUp();
      cameraUp.setData(up.add(eye));
    }
  }

  public void watchLightsUpdate() {
    List<Light> lights = watchedChart.getScene().getLightSet().getLights();

    for (int i = 0; i < lights.size(); i++) {
      lightPositions[i].setCoord(lights.get(i).getPosition());
    }
  }
}
