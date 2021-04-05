
package org.jzy3d.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SimpleDir {

  private SimpleDir() {}

  public static List<File> getAllFolders(File file) throws IOException {
    if (!file.exists()) {
      throw new IOException("File does not exist:" + file);
    }
    List<File> output = new ArrayList<>();
    File[] folders = listDir(file);
    Collections.addAll(output, folders);
    return output;
  }

  public static File[] listFile(File dir) {
    return dir.listFiles(File::isFile);
  }

  public static File[] listDir(File dir) {
    return dir.listFiles(File::isDirectory);
  }

  public static List<File> getAllFiles(List<File> files) throws IOException {
    if (files.isEmpty()) {
      return Collections.emptyList();
    }
    if (files.size() == 1) {
      return getAllFiles(files.get(0));
    }
    List<File> out = new ArrayList<>();
    for (File f : files) {
      out.addAll(getAllFiles(f));
    }
    return out;
  }

  public static List<File> getAllFiles(File file) throws IOException {
    if (!file.exists()) {
      throw new IOException("File does not exist:" + file);
    }
    return Collections.unmodifiableList(Arrays.asList(file.listFiles()));
  }
}
