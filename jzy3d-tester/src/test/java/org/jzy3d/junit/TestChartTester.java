package org.jzy3d.junit;

import java.awt.image.BufferedImage;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Testing the test tool!
 * 
 * @see more test in {@link NativeChartTester}
 * @author martin
 *
 */
public class TestChartTester {
  ChartTester test;

  @Before
  public void before() {
    test = new ChartTester();
  }

  @Test
  public void compareImageWithHerselfSucceed() throws IOException {
    BufferedImage bi = test.loadBufferedImage("src/test/resources/testimage.png");
    try {
      test.compare(bi, bi);
    } catch (ChartTestFailed e) {
      Assert.fail(e.getMessage());
    }
    Assert.assertTrue(true);
  }

  @Test
  public void compareImageWithAnotherFails() throws IOException {
    BufferedImage bi1 = test.loadBufferedImage("src/test/resources/testimage.png");
    BufferedImage bi2 = test.loadBufferedImage("src/test/resources/testimage2.png");
    try {
      test.compare(bi1, bi2);
    } catch (ChartTestFailed e) {
      Assert.assertTrue(e.getMessage(), true);
      return;
    }
    Assert.fail("two different image should throw an exception");
  }
}
