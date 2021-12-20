package org.jzy3d.io.ply;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.maths.Coord3d;
import org.smurn.jply.Element;
import org.smurn.jply.ElementReader;
import org.smurn.jply.PlyReaderFile;

public class Ply {
  static Logger LOGGER = LogManager.getLogger(Ply.class);

  public static List<Coord3d> read(String filename) throws IOException {
    return read(new FileInputStream(filename));
  }

  public static List<Coord3d> read(InputStream is) throws IOException {
    PlyReaderFile reader = new PlyReaderFile(is);
    List<Coord3d> coords = read(reader);
    reader.close();
    return coords;
  }

  public static List<Coord3d> read(PlyReaderFile reader) throws IOException {
    ElementReader elementReader = reader.nextElementReader();

    List<Coord3d> coords = new ArrayList<>();

    while (elementReader != null) {
      LOGGER.info("Will load " + elementReader.getCount() + " " + elementReader.getElementType());
      if (!elementReader.getElementType().getName().contains("vertex")) {
        LOGGER.warn("Parsing not implemented for " + elementReader.getElementType().getName());
      }


      Element element = elementReader.readElement();

      while (element != null) {
        if ("vertex".equals(element.getType().getName())) {
          Coord3d c = vertex(element);
          coords.add(c);
        } else {
          // LOGGER.warn("not parsed " + element);
        }

        // get next
        element = elementReader.readElement();
      }

      elementReader.close();
      elementReader = reader.nextElementReader();
    }
    return coords;
  }

  public static Coord3d vertex(Element element) {
    double x = element.getDouble("x");
    double y = element.getDouble("y");
    double z = element.getDouble("z");
    Coord3d c = new Coord3d(x, y, z);
    return c;
  }
}
