package org.jzy3d.plot3d.primitives.axis.layout.providers;


/**
 * A tick provider for musical notation.
 * 
 * Select frequency values for all A notes between A0 and A7 (approximately a piano range)
 * 
 * @author Martin Pernollet
 */
public class PitchTickProvider extends StaticTickProvider {
  public PitchTickProvider() {
    super(pitchValues(8));
  }

  public PitchTickProvider(int octaves) {
    super(pitchValues(octaves));
  }

  private static double[] pitchValues(int octaves) {
    if (octaves == 2) {
      double[] array = {0.0, /* A0 */27.5, /* A1 */55.0};
      return array;
    } else if (octaves == 3) {
      double[] array = {0.0, /* A0 */27.5, /* A1 */55.0, /* A2 */110.0};
      return array;
    } else if (octaves == 4) {
      double[] array = {0.0, /* A0 */27.5, /* A1 */55.0, /* A2 */110.0, /* A3 */220.0};
      return array;
    } else if (octaves == 5) {
      double[] array =
          {0.0, /* A0 */27.5, /* A1 */55.0, /* A2 */110.0, /* A3 */220.0, /* A4 */440.0};
      return array;
    } else if (octaves == 6) {
      double[] array = {0.0, /* A0 */27.5, /* A1 */55.0, /* A2 */110.0, /* A3 */220.0,
          /* A4 */440.0, /* A5 */880.0};
      return array;
    } else if (octaves == 7) {
      double[] array = {0.0, /* A0 */27.5, /* A1 */55.0, /* A2 */110.0, /* A3 */220.0,
          /* A4 */440.0, /* A5 */880.0, /* A6 */1760.0};
      return array;
    } else { // 8
      double[] array = {0.0, /* A0 */27.5, /* A1 */55.0, /* A2 */110.0, /* A3 */220.0,
          /* A4 */440.0, /* A5 */880.0, /* A6 */1760.0, /* A7 */3520.0};
      return array;
    }
  }

}
