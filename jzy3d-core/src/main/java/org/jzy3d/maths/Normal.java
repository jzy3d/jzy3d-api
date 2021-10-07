package org.jzy3d.maths;

import java.util.List;
import org.jzy3d.plot3d.primitives.Point;

public class Normal {
  /**
   * Indicates how the normal of a vertex is computed
   * <ul>
   * <li>SHARED: as a mean of all polygons that the vertex belongs to.
   * <li>REPEATED : the normal of a point if the normal of 1 polygon it belongs to.
   * </ul>
   * 
   * See also {@link NormalPer}
   */
  public enum NormalMode {
    SHARED, REPEATED
  }

  /**
   * Indicate if normals are defined per point or per geometry.
   * 
   * See also {@link NormalMode}
   */
  public enum NormalPer {
    POINT, GEOMETRY;
  }

  /**
   * Compute the normal for the three points which is a vector perpendicular to the plane formed by the three input points.
   */
  public static Coord3d compute(Coord3d p0, Coord3d p1, Coord3d p2) {
    return compute(p0, p1, p2, true);
  }
  
  /**
   * Compute the normal for the three points which is a vector perpendicular to the plane formed by the three input points.
   * 
   * @param normalize if true, will divise normal components by the length of the normal
   */
  public static Coord3d compute(Coord3d p0, Coord3d p1, Coord3d p2, boolean normalize) {
    Vector3d v1 = new Vector3d(p0, p1);
    Vector3d v2 = new Vector3d(p1, p2);
    Coord3d norm = v1.cross(v2);
    
    if(normalize) {
      double d = norm.distance(Coord3d.ORIGIN);
      norm.x /= d;
      norm.y /= d;
      norm.z /= d;
    }
    return norm;
  }
  
  /**
   * Compute the normal for the input list of points.
   * 
   * @param points
   * @param normalize if true, will divise normal components by the length of the normal
   * @param averageNormals if true and if points.size() > 3, will compute a normal for each triangle and then process an average normal of all triangles. If false, simply process normal out of the three first points, hence assuming coplanarity of the points.
   * @return
   */
  public static Coord3d compute(List<Point> points, boolean normalize, boolean averageNormals) {
    
    if(points.size()==3) {
      return compute(points.get(0).xyz, points.get(1).xyz, points.get(2).xyz, normalize);
    }
    else if(points.size()>3) {
      if(averageNormals) {
        return compute(points.get(0).xyz, points.get(1).xyz, points.get(2).xyz, normalize);
      }
      else {
        int triangles = points.size()-2;
        
        Coord3d averagedNormal = new Coord3d();
        for (int t = 0; t < triangles; t++) {
          averagedNormal.addSelf(compute(points.get(0).xyz, points.get(t+1).xyz, points.get(t+2).xyz, normalize));
        }
        averagedNormal.divSelf(triangles);
        return averagedNormal;
      }
    }
    else {
      throw new IllegalArgumentException("Need at least 3 points");
    }
  }
  
  
}
