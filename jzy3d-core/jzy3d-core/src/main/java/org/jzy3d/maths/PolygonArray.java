package org.jzy3d.maths;


public class PolygonArray {
  public PolygonArray(float x[], float y[], float z[]) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public int length() {
    return x.length;
  }

  public Coord3d getBarycentre() {
    if (barycentre == null)
      barycentre = new Coord3d(Statistics.mean(x), Statistics.mean(y), Statistics.mean(z));
    return barycentre;
  }

  public float x[];
  public float y[];
  public float z[];
  protected Coord3d barycentre;
}
