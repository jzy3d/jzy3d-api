package org.jzy3d.io.gif;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.maths.TicToc;
import org.jzy3d.plot2d.rendering.AWTGraphicsUtils;

public class TestGifExporter {


  @Test
  public void whenGlobalDelay_ThenImagesAreSkippedOrRepeatedToFitGlobalDelay() throws IOException {
    TicToc timer = mock(TicToc.class);


    // Configure to have exactly 10 image per second, spaced by 100ms
    int gifFrameDelayMs = 500; // ms

    String path = "./target/TestGifExporterRegular.gif";
    File outputFile = new File(path);

    // ------------------------------------
    // Given an exporter with a global inter-frame delay

    GifExporter gif = new GifExporter(outputFile, gifFrameDelayMs);
    gif.setTimer(timer);

    Assert.assertTrue(gif.isGlobalDelay());


    // ------------------------------------
    // When append the first image

    BufferedImage i = makeBufferedImage("1");
    ImageIO.write(i, "png", new File(path + "-1.png"));

    when(timer.elapsedMilisecond()).thenReturn(new Double(0));
    gif.export(i);

    // Then number of submitted image grows after a short delay
    sleep(50);
    Assert.assertEquals(1, gif.getNumberSubmittedImages().intValue());
    Assert.assertEquals(0, gif.getNumberOfSkippedImages().intValue());

    // ------------------------------------
    // When append a second image before the gifFrameRate
    // (so arrives too early)

    i = makeBufferedImage("2");
    ImageIO.write(i, "png", new File(path + "-2.png"));

    when(timer.elapsedMilisecond()).thenReturn(new Double(gifFrameDelayMs / 10));
    gif.export(i);

    // Then number of submitted image did not grow after a short delay
    sleep(50);
    Assert.assertEquals(1, gif.getNumberSubmittedImages().intValue());
    Assert.assertEquals(1, gif.getNumberOfSkippedImages().intValue());

    // ------------------------------------
    // When append a third image after the gifFrameRate
    // (so arrives late enough to be added)

    i = makeBufferedImage("3");
    ImageIO.write(i, "png", new File(path + "-3.png"));

    when(timer.elapsedMilisecond()).thenReturn(new Double(gifFrameDelayMs + 1));
    gif.export(i);

    // Then number of submitted image did not grow after a short delay
    sleep(50);
    Assert.assertEquals(2, gif.getNumberSubmittedImages().intValue());
    Assert.assertEquals(1, gif.getNumberOfSkippedImages().intValue());

    // ------------------------------------
    // When append a fourth image way too long after the gifFrameRate
    // then the image is repeated to keep the frame rate constant

    i = makeBufferedImage("4");
    ImageIO.write(i, "png", new File(path + "-4.png"));

    int nFrameLate = 4;
    when(timer.elapsedMilisecond()).thenReturn(new Double(gifFrameDelayMs * nFrameLate));
    gif.export(i);

    // Then number of submitted image did not grow after a short delay
    sleep(50);
    Assert.assertEquals(2 + nFrameLate, gif.getNumberSubmittedImages().intValue());
    Assert.assertEquals(1, gif.getNumberOfSkippedImages().intValue());


    // ------------------------------------
    // When terminating with a timeout

    boolean success = gif.terminate(100, TimeUnit.MILLISECONDS);

    // Then the last submited image is added (for implementation reasons
    Assert.assertEquals(2 + nFrameLate + 1, gif.getNumberSubmittedImages().intValue());

    // Then, Can properly flush all image and get a file
    Assert.assertTrue("Could flush all gif images before timeout", success);
    Assert.assertTrue(outputFile.exists());

  }

  @Test
  public void whenNoGlobalDelay_ThenAllImagesAreExportedWithPerFrameDelay() throws IOException {
    TicToc timer = mock(TicToc.class);


    String path = "./target/TestGifExporterIrregular.gif";
    File outputFile = new File(path);

    // ------------------------------------
    // Given an exporter with no global inter frame delay
    GifExporter gif = new GifExporter(outputFile, -1);
    gif.setTimer(timer);

    Assert.assertFalse(gif.isGlobalDelay());

    // ------------------------------------
    // When append the first image

    BufferedImage i = makeBufferedImage("1");
    ImageIO.write(i, "png", new File(path + "-1.png"));

    when(timer.elapsedMilisecond()).thenReturn(new Double(0));
    gif.export(i);

    // Then number of submitted image grows after a short delay
    sleep(50);
    Assert.assertEquals(0, gif.getDelay());
    Assert.assertEquals(1, gif.getNumberSubmittedImages().intValue()); // current not exported yet
    Assert.assertEquals(0, gif.getNumberOfSkippedImages().intValue());

    // ------------------------------------
    // When append a second image after 10 ms

    i = makeBufferedImage("2");
    ImageIO.write(i, "png", new File(path + "-2.png"));

    when(timer.elapsedMilisecond()).thenReturn(new Double(100)); // elapsed 10ms
    gif.export(i);

    // Then number of submitted image did not grow after a short delay
    sleep(50);

    Assert.assertEquals(100, gif.getDelay());
    Assert.assertEquals(2, gif.getNumberSubmittedImages().intValue()); // current not exported yet
    Assert.assertEquals(0, gif.getNumberOfSkippedImages().intValue());

    // ------------------------------------
    // When append a second image after 50 ms

    i = makeBufferedImage("3");
    ImageIO.write(i, "png", new File(path + "-3.png"));

    when(timer.elapsedMilisecond()).thenReturn(new Double(500));
    gif.export(i);

    // Then number of submitted image did not grow after a short delay
    sleep(50);

    Assert.assertEquals(500, gif.getDelay());
    Assert.assertEquals(3, gif.getNumberSubmittedImages().intValue()); // current not exported yet
    Assert.assertEquals(0, gif.getNumberOfSkippedImages().intValue());

    // ------------------------------------
    // When append a fourth image way too long after the gifFrameRate
    // then the image is repeated to keep the frame rate constant

    i = makeBufferedImage("4");
    ImageIO.write(i, "png", new File(path + "-4.png"));

    when(timer.elapsedMilisecond()).thenReturn(new Double(3000));
    gif.export(i);

    // Then number of submitted image did not grow after a short delay
    sleep(50);
    Assert.assertEquals(3000, gif.getDelay());
    Assert.assertEquals(4, gif.getNumberSubmittedImages().intValue()); // current not exported yet
    Assert.assertEquals(0, gif.getNumberOfSkippedImages().intValue());


    // ------------------------------------
    // When terminating with a timeout

    boolean success = gif.terminate(100, TimeUnit.MILLISECONDS);

    // Then the last submited image is added (for implementation reasons)
    Assert.assertEquals(4+1 , gif.getNumberSubmittedImages().intValue());

    // Then, Can properly flush all image and get a file
    Assert.assertTrue("Could flush all gif images before timeout", success);
    Assert.assertTrue(outputFile.exists());

  }

  @Test
  public void whenTerminationTimoutTooShort_ThenReturnNoSuccess() {
    // Configure to have exactly 10 image per second, spaced by 100ms
    int gifFrameRateMs = 100; // ms

    File outputFile = new File("./target/TestGifExporter.gif");
    GifExporter gif = new GifExporter(outputFile, gifFrameRateMs);

    gif.export(makeBufferedImage("1"));


    // When terminating with a timeout
    boolean success = gif.terminate(0, TimeUnit.MILLISECONDS);

    // Can properly
    Assert.assertFalse("Could flush all gif images before timeout", success);

  }

  private BufferedImage makeBufferedImage(String string) {
    int width = 400;
    int height = 100;

    BufferedImage i = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

    Graphics2D g2d = (Graphics2D) i.getGraphics();

    AWTGraphicsUtils.configureRenderingHints(g2d);

    g2d.setColor(Color.white);
    g2d.fillRect(0, 0, width, height);
    g2d.setColor(Color.BLACK);
    g2d.drawString(string, 10, height / 2);
    g2d.dispose();

    return i;
  }

  private void sleep(long mili) {
    try {
      Thread.sleep(mili);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
