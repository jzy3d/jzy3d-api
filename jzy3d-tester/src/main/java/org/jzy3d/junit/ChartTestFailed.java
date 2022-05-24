package org.jzy3d.junit;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import org.jzy3d.maths.IntegerCoord2d;


public class ChartTestFailed extends Exception {
  private static final long serialVersionUID = 3514756078633688965L;

  protected BufferedImage actual;
  protected BufferedImage expected;
  
  protected List<IntegerCoord2d> diffs = new ArrayList<IntegerCoord2d>();


  public ChartTestFailed() {
    super();
  }

  public ChartTestFailed(String message, Throwable cause) {
    super(message, cause);
  }

  public ChartTestFailed(String message) {
    super(message);
  }

  public ChartTestFailed(Throwable cause) {
    super(cause);
  }

  public ChartTestFailed(String message, BufferedImage actual, BufferedImage expected) {
    super(message);
    this.actual = actual;
    this.expected = expected;
  }

  public BufferedImage getActualImage() {
    return actual;
  }

  public BufferedImage getExpectedImage() {
    return expected;
  }

  public void addDiffCoordinates(int i, int j) {
    diffs.add(new IntegerCoord2d(i, j));
  }

  public List<IntegerCoord2d> getDiffCoordinates() {
    return diffs;
  }

  public boolean isSameImageSize() {
    int i1W = getActualImage().getWidth();
    int i1H = getActualImage().getHeight();
    int i2W = getExpectedImage().getWidth();
    int i2H = getExpectedImage().getHeight();
    
    return (i1W == i2W && i1H == i2H);
  }
  
  public String getImageSizeDifferenceMessage() {
    int i1W = getActualImage().getWidth();
    int i1H = getActualImage().getHeight();
    int i2W = getExpectedImage().getWidth();
    int i2H = getExpectedImage().getHeight();

    return "image size differ: actual={" + i1W + "," + i1H + "} expected={" + i2W + "," + i2H + "}";
  }

}

