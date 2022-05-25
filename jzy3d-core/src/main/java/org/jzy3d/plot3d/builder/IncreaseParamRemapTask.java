package org.jzy3d.plot3d.builder;

import org.jzy3d.plot3d.primitives.Shape;

public class IncreaseParamRemapTask extends AbstractRemapTask {
  double increment = 0.0001;
  
  public IncreaseParamRemapTask(Shape surface, SingleParameterMapper mapper) {
    super(surface, mapper);
  }

  @Override
  public void remap() {
    mapper.setParam(mapper.getParam() + increment);
    
    //System.out.println("remap with " + mapper.getParam());
    
    mapper.remap(surface);
  }

  public double getIncrement() {
    return increment;
  }

  public void setIncrement(double increment) {
    this.increment = increment;
  }
}
