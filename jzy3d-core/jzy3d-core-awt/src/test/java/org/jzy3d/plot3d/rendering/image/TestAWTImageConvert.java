package org.jzy3d.plot3d.rendering.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import org.junit.Assert;
import org.junit.Test;

public class TestAWTImageConvert {
  /**
   * This test does verify lot of thing apart checking that a result will arrive without exception.
   * 
   * Added it to ensure buffer management won't fail with other java version.
   * 
   * @throws IOException
   */
  @Test
  public void getImageAsByteBuffer() throws IOException {
    BufferedImage image = ImageIO.read(new File("src/test/resources/jzy3d-blue-small-80.png"));
    
    ByteBuffer b = AWTImageConvert.getImageAsByteBuffer(image);
    
    Assert.assertNotNull(b);
    Assert.assertTrue(b.capacity()>0);
  }
}
