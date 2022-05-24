package org.jzy3d.io;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class AWTImageFile {
  public static BufferedImage load(String filename) throws IOException {
    return ImageIO.read(new File(filename));
  }

  public static void savePNG(Image image, String filename) throws IOException {
    ImageIO.write((BufferedImage) image, "png", new File(filename));
  }
}
