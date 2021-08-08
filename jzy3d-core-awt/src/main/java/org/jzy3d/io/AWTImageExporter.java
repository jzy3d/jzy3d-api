package org.jzy3d.io;

import java.awt.image.BufferedImage;

public interface AWTImageExporter {
  public void export(BufferedImage image);
  public void terminate();
}
