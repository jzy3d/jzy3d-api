package org.jzy3d.plot3d.builder;

import org.jzy3d.plot3d.primitives.Shape;

public class IncreaseParamRemapTask extends AbstractRemapTask {

  public IncreaseParamRemapTask(Shape surface, SingleParameterMapper mapper) {
    super(surface, mapper);
  }

  @Override
  public void remap() {
    mapper.setParam(mapper.getParam() + 0.0001);
    mapper.remap(surface);
  }
}
