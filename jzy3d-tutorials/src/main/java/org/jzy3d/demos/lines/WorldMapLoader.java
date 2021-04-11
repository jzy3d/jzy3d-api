package org.jzy3d.demos.lines;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.CroppableLineStrip;
import org.jzy3d.plot3d.primitives.Point;
import com.opencsv.CSVReader;

public class WorldMapLoader {

  public List<CroppableLineStrip> lineStrips = new ArrayList<CroppableLineStrip>();
  String newline = System.getProperty("line.separator");

  public List<CroppableLineStrip> parseFile(String filename) {
    try {

      // Get world map csv location
      File worldMapFile = new File(filename);

      // Create file reader and CSV reader from world map file
      FileReader fileReader = new FileReader(worldMapFile);
      CSVReader reader = new CSVReader(fileReader);

      // Create row holder and line number counter
      String[] rowHolder;
      int lineNumber = 1;

      // Create local line strip and set color
      CroppableLineStrip lineStrip = new CroppableLineStrip();
      lineStrip.setWireframeColor(Color.BLACK);

      // Loop through rows while a next row exists
      while ((rowHolder = reader.readNext()) != null) {

        switch (rowHolder.length) {
          case 1:
            if (rowHolder[0].equals("")) {

              // If row is blank, add line strip to list of line
              // strips and clear line strip
              lineStrips.add(lineStrip);
              lineStrip = new CroppableLineStrip();
              lineStrip.setWireframeColor(Color.BLACK);
              break;
            } else {

              // Throw error if a map point only has one coordinate
              String oneCoordinateError =
                  "Error on line: " + lineNumber + newline + "The row contains only 1 coordinate";
              JOptionPane.showMessageDialog(null, oneCoordinateError,
                  "Incorrect number of coordinates", JOptionPane.ERROR_MESSAGE);
              System.exit(-1);
            }
          case 2:
            try {

              // Add the map point to the line strip
              lineStrip.add(new Point(
                  new Coord3d(Float.valueOf(rowHolder[0]), Float.valueOf(rowHolder[1]), 0.0)));
            } catch (NumberFormatException e) {

              // Throw error if a map point coordinate cannot be
              // converted to a Float
              String malformedCoordinateError =
                  "Error on line: " + lineNumber + newline + "Coordinate is incorrectly formatted";
              JOptionPane.showMessageDialog(null, malformedCoordinateError, "Incorrect Format",
                  JOptionPane.ERROR_MESSAGE);
              e.printStackTrace();
              System.exit(-1);
            }
            break;
          default:

            // Throw error if the map point has more than three
            // coordinates
            String numCoordinateError = "Error on line: " + lineNumber + newline
                + "The row contains " + rowHolder.length + " coordinates";
            JOptionPane.showMessageDialog(null, numCoordinateError,
                "Incorrect number of coordinates", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        // Add the final lineStrip after while loop is complete.
        lineStrips.add(lineStrip);
        lineNumber++;
      }
      reader.close();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.out.println("WARNING: World map file not found");

    } catch (IOException e) {
      e.printStackTrace();
    }
    return lineStrips;
  }
}
