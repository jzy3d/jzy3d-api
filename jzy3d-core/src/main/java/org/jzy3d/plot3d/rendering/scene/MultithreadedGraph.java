package org.jzy3d.plot3d.rendering.scene;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Composite;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.rendering.ordering.AbstractOrderingStrategy;
import com.google.common.collect.Lists;

/**
 * 
 * WORK IN PROGRESS
 * =================
 * 
 * Split drawing tasks to multiple thread, and wait for termination of all drawing tasks
 * before exiting the draw method, hence ensuring a consistent display.
 * 
 * This prove to be hardly managed by opengl backends (emulgl and jogl) that require GL primitives
 * being called consistently in order (glBegin->glVertex->glEnd) : multi thread make them interlaced
 * in the current GL context.
 * 
 * EmulGL : throws exception 
 * JOGL : AWT and NEWT are both crashing the JVM
 * 
 * 
 * 
 * 
 * @author martin
 *
 */
public class MultithreadedGraph extends Graph {
  int cores;
  ExecutorService executor;

  public MultithreadedGraph(Scene scene, AbstractOrderingStrategy strategy, boolean sort) {
    super(scene, strategy, sort);

    cores = Runtime.getRuntime().availableProcessors();
    executor = Executors.newFixedThreadPool(cores);
  }

  @Override
  public synchronized void dispose() {
    executor.shutdownNow(); // does not await termination of tasks
    super.dispose();
  }



  /**
   * Decompose all {@link Composite} objects, and sort the extracted monotype (i.e.
   * non-{@link Composite} {@link Drawable}s) in order to render them according to the default -or
   * defined- {@link AbstractOrderingStrategy}.
   * 
   */
  @Override
  public void draw(IPainter painter) {
    draw(painter, components, sort);
  }

  // https://www.baeldung.com/java-executor-service-tutorial
  @Override
  public synchronized void draw(IPainter painter, List<Drawable> components, boolean sort) {

    if(components.size()==0)
      return;

    painter.glMatrixMode_ModelView();

    // Decompose
    synchronized (components) {
      components = Decomposition.getDecomposition(components);
    }
      

    
    // Compute chunks
    int chunckSize = (int) (1f * components.size() / cores);
    
    List<List<Drawable>> chuncks =
        Lists.partition(components, chunckSize);

    int chunckNumber = chuncks.size();

    System.out.println("cores:" + cores + " chunks:" + chunckNumber + " components:" + components.size() + " chunk size:" + chunckSize);

    
    //CyclicBarrier cyclicBarrier = new CyclicBarrier(chunckNumber);
    CountDownLatch doneSignal = new CountDownLatch(chunckNumber);
    
    for (List<Drawable> chunck : chuncks) {

      /*Callable<Void> drawChunkTask = () -> {
        System.out.println("GO CHUNK (size:" + chunck.size());

        for (Drawable d : chunck) {
          d.draw(painter);
        }
        System.out.println("COUNT DOWN");

        doneSignal.countDown();
        return null;
      };*/
      
      Runnable drawChunkTask2 = () -> {
        System.out.println("GO CHUNK (size:" + chunck.size());

        for (Drawable d : chunck) {
          System.out.print("*");
          d.draw(painter);
        }
        System.out.println("COUNT DOWN");

        doneSignal.countDown();
      };
      //executor.submit(drawChunkTask2);
      executor.execute(drawChunkTask2);
    }

    // Wait for task to finish
    System.out.println("AWAIT");
    try {
      //cyclicBarrier.await();
      doneSignal.await();
      System.out.println("DONE");
    } catch (InterruptedException e) {
      e.printStackTrace();
    }    
    

    /*
     * if (!sort) { drawSimple(painter, components); } else { drawDecomposition(painter); }
     */
  }

  /** render all items of the graph */
  @Override
  public void drawSimple(IPainter painter, List<Drawable> components) {
    for (Drawable d : components)
      if (d.isDisplayed())
        d.draw(painter);
  }

  /** render all items of the graph after decomposing all composite item into primitive drawables */
  @Override
  public void drawDecomposition(IPainter painter) {
    List<Drawable> monotypes = getDecomposition();
    strategy.sort(monotypes, painter.getCamera());

    for (Drawable d : monotypes) {
      if (d.isDisplayed())
        d.draw(painter);
    }
  }

  /**
   * Expand all {@link AbstractComposites} instance into a list of atomic {@link Drawable} types and
   * return all the current Graph primitives decomposition.
   */
  @Override
  public List<Drawable> getDecomposition() {
    List<Drawable> monotypes;
    synchronized (components) {
      monotypes = Decomposition.getDecomposition(components);
    }
    return monotypes;
  }
}
