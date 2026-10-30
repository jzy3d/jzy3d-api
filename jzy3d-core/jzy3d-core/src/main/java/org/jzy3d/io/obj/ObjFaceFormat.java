package org.jzy3d.io.obj;


public class ObjFaceFormat {
  /** f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3 */
  public boolean vertexTextureNormal(String line) {
    return false;
  }

  /** f v1//vn1 v2//vn2 v3//vn3 */
  public static boolean vertexNormal(String line) {
    return line.indexOf("//") != -1;
  }

  /** f v1 v2 v3 */
  public static boolean vertex(String line) {
    return !line.contains("/");
  }
}
