package org.jzy3d.contour;

import org.jzy3d.plot3d.primitives.contour.ContourMesh;

public interface IContourMeshGenerator extends IContourGenerator {
  public ContourMesh getContourMesh(IContourColoringPolicy policy, int xRes, int yRes, int nLevels,
      float planeAxe, boolean writeText);

  public ContourMesh getContourMesh(IContourColoringPolicy policy, int xRes, int yRes,
      double sortedLevels[], float planeAxe, boolean writeText);
}
