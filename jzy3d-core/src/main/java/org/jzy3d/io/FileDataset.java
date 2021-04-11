package org.jzy3d.io;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jzy3d.maths.Coord3d;
import au.com.bytecode.opencsv.CSVReader;

public class FileDataset {
  public static Coord3d[] loadArray(String filename) throws IOException {
    int size = readNLines(filename);
    float x;
    float y;
    float z;

    Coord3d[] coords = new Coord3d[size];

    // Load file
    CSVReader reader = new CSVReader(new FileReader(filename));
    String[] nextLine;
    int k = 0;
    while ((nextLine = reader.readNext()) != null) {
      if (nextLine.length < 3)
        continue;
      x = Float.parseFloat(nextLine[0]);
      y = Float.parseFloat(nextLine[1]);
      z = Float.parseFloat(nextLine[2]);
      coords[k++] = new Coord3d(x, y, z);
    }
    reader.close();
    return coords;
  }

  public static List<Coord3d> loadList(String filename) throws IOException {
    return loadList(filename, 0, 1, 2);
  }

  public static List<Coord3d> loadList(String filename, int xColumn, int yColumn, int zColumn)
      throws IOException {
    float x;
    float y;
    float z;

    List<Coord3d> coords = new ArrayList<Coord3d>();

    // Load file
    CSVReader reader = new CSVReader(new FileReader(filename));
    String[] nextLine;
    while ((nextLine = reader.readNext()) != null) {
      if (nextLine.length < 3)
        continue;
      x = Float.parseFloat(nextLine[xColumn]);
      y = Float.parseFloat(nextLine[yColumn]);
      z = Float.parseFloat(nextLine[zColumn]);
      coords.add(new Coord3d(x, y, z));
    }
    reader.close();
    return coords;
  }

  /**********************************************/

  protected static int readNLines(String filename) throws IOException {
    CSVReader reader = new CSVReader(new FileReader(filename));
    int n = 0;
    while (reader.readNext() != null)
      n++;
    reader.close();
    return n;
  }

  public static Coord3d[] toArray(List<Coord3d> list) {
    Coord3d[] points = new Coord3d[list.size()];
    int k = 0;
    for (Coord3d c : list) {
      points[k++] = c;
    }
    return points;
  }
}
