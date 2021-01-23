package org.jzy3d.plot3d.builder;

import org.jzy3d.maths.TicToc;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.primitives.Shape;

/**
 * A remapping task that let a remapping be performed in a simple while loop.
 * 
 * To be used by an Executor or Thread.
 * 
 * @author martin
 *
 */
public abstract class AbstractRemapTask implements Runnable {
  protected SingleParameterMapper mapper;
  protected String info;
  protected TicToc time = new TicToc();


  public AbstractRemapTask(Shape surface, SingleParameterMapper mapper) {
    this.surface = surface;
    this.mapper = mapper;
    this.info = "n/a";
  }

  public abstract void remap();

  protected Shape surface;

  @Override
  public void run() {
    while (true) {
      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
      }
      time.tic();
      remap();
      time.toc();

      info = Utils.num2str(time.elapsedSecond(), 4) + "s to remap surface";
    }
  }

  public Shape getSurface() {
    return surface;
  }

  public void setSurface(Shape surface) {
    this.surface = surface;
  }

  public SingleParameterMapper getMapper() {
    return mapper;
  }

  public void setMapper(SingleParameterMapper mapper) {
    this.mapper = mapper;
  }

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }
}
