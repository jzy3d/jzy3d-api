package org.jzy3d.io;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jzy3d.maths.Coord3d;
import au.com.bytecode.opencsv.CSVReader;

public class FileDataset {

  private FileDataset() {} // static use only

  public static Coord3d[] loadArray(String filename) throws IOException {
    return toArray(loadList(filename));
  }

  public static List<Coord3d> loadList(String filename) throws IOException {
    return loadList(filename, 0, 1, 2);
  }

  public static List<Coord3d> loadList(String filename, int xColumn, int yColumn, int zColumn)
      throws IOException {
    List<Coord3d> coords = new ArrayList<>();
    // Load file
    try (CSVReader reader = new CSVReader(new FileReader(filename))) {
      for (String[] nextLine; (nextLine = reader.readNext()) != null;) {
        if (nextLine.length < 3) {
          continue;
        }
        float x = Float.parseFloat(nextLine[xColumn]);
        float y = Float.parseFloat(nextLine[yColumn]);
        float z = Float.parseFloat(nextLine[zColumn]);
        coords.add(new Coord3d(x, y, z));
      }
    }
    return coords;
  }

  /**********************************************/


  @Deprecated//(forRemoval = true)
  protected static int readNLines(String filename) throws IOException {
    try (CSVReader reader = new CSVReader(new FileReader(filename))) {
      int n = 0;
      while (reader.readNext() != null) {
        n++;
      }
      return n;
    }
  }

  public static Coord3d[] toArray(List<Coord3d> list) {
    return list.toArray(new Coord3d[list.size()]);
  }
}
