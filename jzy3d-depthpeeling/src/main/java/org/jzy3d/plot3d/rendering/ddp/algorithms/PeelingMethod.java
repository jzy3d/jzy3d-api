package org.jzy3d.plot3d.rendering.ddp.algorithms;

/**
 * Status of peeling methods on {macOS 10.12 + NVidia GPU} {Ubuntu 20.04 + Intel Iris GPU}
 * <ul>
 * <li>OK - F2B_PEELING_MODE
 * <li>OK - DUAL_PEELING_MODE
 * <li>OK - WEIGHTED_AVERAGE_MODE
 * <li>KO - WEIGHTED_SUM_MODE : no compilation problem BUT overlapping parts (translucent or opaque)
 * are black
 * </ul>
 * 
 * Status of peeling methods on macOS 11.4 + M1 (Silicon)
 * <ul>
 * <li>OK - DUAL_PEELING_MODE
 * <li>KO - WEIGHTED_AVERAGE_MODE : renders correctly BUT make opaque object appear translucent
 * (e.g. the blue cube of this demo)
 * <li>KO - WEIGHTED_SUM_MODE : no compilation problem BUT overlapping parts (translucent or opaque)
 * are black
 * <li>KO - F2B_PEELING_MODE : Hangs before display (reproduce with chart.get
 * </ul>
 *
 * @author martin
 *
 */
public enum PeelingMethod {
  DUAL_PEELING_MODE, F2B_PEELING_MODE, WEIGHTED_AVERAGE_MODE, WEIGHTED_SUM_MODE;
}
