package org.jzy3d.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import au.com.bytecode.opencsv.CSVWriter;

public class SimpleCsv {
  public static <T> void write(List<T> entities, String file, char separator, ToLine<T> toLine)
      throws IOException {
    File parent = (new File(file)).getParentFile();
    if (parent != null && !parent.exists())
      parent.mkdirs();
    FileWriter fw = new FileWriter(file);
    CSVWriter writer = new CSVWriter(fw, separator);
    for (T entity : entities) {
      writer.writeNext(toLine.toLine(entity));
    }
    writer.close();
  }

  /* */

  public static void write(List lines, String file, char separator) throws IOException {
    File parent = (new File(file)).getParentFile();
    if (parent != null && !parent.exists())
      parent.mkdirs();
    FileWriter fw = new FileWriter(file);
    CSVWriter writer = new CSVWriter(fw, separator);
    writer.writeAll(lines);
    writer.close();
  }

  public static void writeLines(List lines, String file, char separator) throws IOException {
    File parent = (new File(file)).getParentFile();
    if (parent != null && !parent.exists())
      parent.mkdirs();
    FileWriter fw = new FileWriter(file);
    CSVWriter writer = new CSVWriter(fw, separator);
    writer.writeAll(convert(lines));
    writer.close();
  }

  protected static List convert(List lines) {
    List out = new ArrayList(lines.size());
    String content[];
    for (Iterator i$ = lines.iterator(); i$.hasNext(); out.add(content)) {
      String line = (String) i$.next();
      content = new String[1];
      content[0] = line;
    }

    return out;
  }
}
