package org.jzy3d.maths;

public class RotationMatrix {

  public RotationMatrix(float a, Coord3d c) {
    this.a = a;
    this.x = c.x;
    this.y = c.y;
    this.z = c.z;
  }

  // TODO normalize x, y, z
  public RotationMatrix(float a, float x, float y, float z) {
    this.a = a;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  protected void prepare() {
    c = (float) Math.cos(a);
    s = (float) Math.sin(a);
  }

  public Coord3d rotate(Coord3d c) {
    Coord3d out = c.clone();
    return rotateSelf(c);
  }

  /**
   * glRotate produces a rotation of angle degrees around the vector (x,y,z). The current matrix
   * (see glMatrixMode) is multiplied by a rotation matrix with the product replacing the current
   * matrix, as if glMultMatrix were called with the following matrix as its argument:
   * 
   * ( xx(1-c)+c xy(1-c)-zs xz(1-c)+ys 0 ) | | | yx(1-c)+zs yy(1-c)+c yz(1-c)-xs 0 | | xz(1-c)-ys
   * yz(1-c)+xs zz(1-c)+c 0 | | | ( 0 0 0 1 ) Where c = cos(angle), s = sine(angle), and ||( x,y,z
   * )|| = 1 (if not, the GL will normalize this vector).
   * 
   * @param c
   * @return
   */
  public Coord3d rotateSelf(Coord3d c) {
    return c;
  }

  // protected float

  protected float a = 0;
  protected float x = 0;
  protected float y = 0;
  protected float z = 0;

  protected float c = 0;
  protected float s = 0;
}
