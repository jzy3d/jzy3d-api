package org.jzy3d.plot3d.rendering.view;
/*
 * Copyright (c) Since 2010, Martin Pernollet All rights reserved.
 *
 * Redistribution in binary form, with or without modification, is permitted. Edition of source
 * files is allowed. Redistribution of original or modified source files is FORBIDDEN.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.picking.AWTMousePickingController;
import org.jzy3d.chart.controllers.mouse.picking.IObjectPickedListener;
import org.jzy3d.chart.controllers.mouse.picking.PickingSupport;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.maths.graphs.IGraph;
import org.jzy3d.maths.graphs.StringGraphGenerator;
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.primitives.graphs.impl.PointGraph2d;
import org.jzy3d.plot3d.primitives.graphs.layout.IGraphLayout2d;
import org.jzy3d.plot3d.primitives.selectable.SelectableScatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;



/**
 * These tests seamingly stupid allows verifying if selection or picking
 * trigger exceptions or not. Interaction indeed require to acquire and 
 * release the OpenGL context. Not doing so properly may lead to exceptions.
 * 
 * @author Martin Pernollet
 */
public class TestView_Interactive {
  @Test
  public void givenSelectableItem_whenProject_thenNoException() {
    // Given
    SelectableScatter scatter = SampleGeom.generateSelectableScatter(10);

    AWTChartFactory factory = new AWTChartFactory();
    factory.getPainterFactory().setOffscreen(200, 200);

    Chart chart = factory.newChart(Quality.Advanced());
    chart.getScene().add(scatter);
    chart.getView().setMaximized(true);

    // --------------------------------
    // When
    chart.getView().project();

    // Then no exception occurs

    // --------------------------------
    // When
    chart.getView().projectMouse(100, 100);

    // Then no exception occurs

    // --------------------------------
    Assert.assertTrue(true);
  }


  /**
   * Problem : via maven, not same pixel size, hence brush size pick more vertex May have problems
   * with headless tests (travis) should mock picking behaviour
   */
  @Ignore
  @Test
  public void givenPickableItem_whenPick_thenNoNullPointerException() {
    // Given a graph model
    IGraph<String, String> graph = StringGraphGenerator.getGraph(500, 10);
    IGraphLayout2d<String> layout = StringGraphGenerator.getRandomLayout(graph, 10);

    // ------------------------------
    // Setup a chart
    AWTChartFactory factory = new AWTChartFactory();
    // factory.setOffscreen(200, 200); // want to test offscreen but will use mouse events from AWT!

    Chart chart = factory.newChart(Quality.Advanced());
    // chart.getView().setAxeBoxDisplayed(false);
    chart.getView().setViewPositionMode(ViewPositionMode.TOP);
    chart.getView().setSquared(false);
    // chart.getView().setMaximized(true);

    // ------------------------------
    // Build a pickable drawable graph
    final PointGraph2d<String, String> drawableGraph = new PointGraph2d<String, String>();

    List<Object> pickedVertices = new ArrayList<>();

    AWTMousePickingController mouse =
        (AWTMousePickingController) chart.addMousePickingController((int) 30);
    mouse.getPickingSupport().addObjectPickedListener(new IObjectPickedListener() {
      @Override
      public void objectPicked(List<? extends Object> vertices, PickingSupport picking) {
        for (Object vertex : vertices) {
          System.out.println("picked: " + vertex);
          drawableGraph.setVertexHighlighted((String) vertex, true);
          pickedVertices.add(vertex);
        }

        // disable re-rendering to avoid recursive lock in test
        // chart.render();
      }
    });

    drawableGraph.setGraphLayout(layout);
    drawableGraph.setGraphModel(graph, mouse.getPickingSupport());
    chart.add(drawableGraph);

    // ------------------------------
    // Open Chart
    FrameAWT frame = (FrameAWT) chart.open();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }

    // --------------------------------
    // When
    MouseEvent e =
        new MouseEvent((Component) chart.getCanvas(), 0, 0, 0, 300, 300, 100, 100, 1, false, 0);
    mouse.pick(e);

    frame.setVisible(false);

    // --------------------------------
    // Then was able to pick a vertex

    Assert.assertEquals(1, pickedVertices.size());
    Assert.assertEquals("vertex 230", ((String) pickedVertices.get(0)));
  }




}
