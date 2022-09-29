package org.jzy3d.io;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

public interface AWTImageExporter {
  public void export(BufferedImage image);
  public boolean terminate(long timeout, TimeUnit unit);
}
