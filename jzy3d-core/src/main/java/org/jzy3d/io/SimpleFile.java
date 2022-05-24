package org.jzy3d.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleFile {

  private SimpleFile() {}

  public static void write(String content, String file) throws IOException {
    Path path = Paths.get(file);
    createParentFoldersIfNotExist(path.toFile());
    try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
      writer.write(content);
    }
  }

  public static void createParentFoldersIfNotExist(String file) {
    createParentFoldersIfNotExist(new File(file));
  }

  public static void createParentFoldersIfNotExist(File file) {
    File parent = file.getAbsoluteFile().getParentFile();
    if (parent != null && !parent.exists()) {
      parent.mkdirs();
    }
  }

  public static List<String> readFile(File file) throws IOException {
    return Files.readAllLines(file.toPath());
  }

  public static List<String> read(String filename) throws IOException {
    return readFile(new File(filename));
  }

  public static String readAsString(String filename) throws IOException {
    return readAsString(filename, "\n");
  }

  public static String readAsString(String filename, String newLineString) throws IOException {
    return Files.readAllLines(Paths.get(filename)).stream()
        .collect(Collectors.joining(newLineString));
  }

  /**
   * Return true if file1 is younger than file2, meaning it was last modified after file2. Always
   * return false if file2 does not exist. Always return true if file1 does not exist.
   */
  public static boolean isYounger(String file1, String file2) {
    File f1 = new File(file1);
    File f2 = new File(file2);
    if (!f2.exists()) {
      return false;
    }
    if (!f1.exists()) {
      return true;
    } else {
      return f1.lastModified() > f2.lastModified();
    }
  }
}
