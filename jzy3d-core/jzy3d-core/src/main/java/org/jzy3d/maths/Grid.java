package org.jzy3d.maths;

public class Grid {
  public Grid() {}

  public Grid(float[] x, float[] y, float[][] z) {
    setData(x, y, z);
  }

  public Grid(int[] x, int[] y, int[][] z) {
    setData(x, y, z);
  }

  /*********************************************/

  public void setData(float[] x, float[] y, float[][] z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public void setData(int[] x, int[] y, int[][] z) {
    this.x = toFloatArray(x);
    this.y = toFloatArray(y);
    this.z = toFloatArray(z);
  }

  public float[] getX() {
    return x;
  }

  public float[] getY() {
    return y;
  }

  public float[][] getZ() {
    return z;
  }

  public BoundingBox3d getBounds() {
    if (bounds == null) {
      bounds = new BoundingBox3d(Statistics.min(x), Statistics.max(x), Statistics.min(y),
          Statistics.max(y), Statistics.min(z), Statistics.max(z));
    }
    return bounds;
  }

  /*********************************************/

  protected float[] toFloatArray(int[] input) {
    float[] out = new float[input.length];
    for (int i = 0; i < input.length; i++)
      out[i] = input[i];
    return out;
  }

  protected float[][] toFloatArray(int[][] input) {
    float[][] out = new float[input.length][input[0].length];

    for (int i = 0; i < input.length; i++)
      for (int j = 0; j < input[i].length; j++)
        out[i][j] = input[i][j];
    return out;
  }

  protected float[] x;
  protected float[] y;
  protected float[][] z;

  protected BoundingBox3d bounds;
}
