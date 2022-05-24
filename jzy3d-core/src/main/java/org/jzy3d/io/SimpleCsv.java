package org.jzy3d.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import au.com.bytecode.opencsv.CSVWriter;

public class SimpleCsv {

  private SimpleCsv() {}

  /**
   * @deprecated use {@link #write(List, String, char, Function)} instead
   */
  @Deprecated//(since = "2.0.0", forRemoval = true)
  public static <T> void write(List<T> entities, String file, char separator, ToLine<T> toLine)
      throws IOException {
    write(entities, file, separator, toLine::toLine);
  }

  public static <T> void write(List<T> entities, String file, char separator,
      Function<T, String[]> toLine) throws IOException {
    try (CSVWriter writer = createWriter(file, separator)) {
      for (T entity : entities) {
        writer.writeNext(toLine.apply(entity));
      }
    }
  }

  public static void write(List<String[]> lines, String file, char separator) throws IOException {
    try (CSVWriter writer = createWriter(file, separator)) {
      writer.writeAll(lines);
    }
  }

  public static void writeLines(List<String> lines, String file, char separator)
      throws IOException {
    write(lines, file, separator, s -> new String[] {s});
  }

  private static CSVWriter createWriter(String file, char separator) throws IOException {
    Path path = Paths.get(file);
    SimpleFile.createParentFoldersIfNotExist(path.toFile());
    return new CSVWriter(Files.newBufferedWriter(path), separator);
  }
}
