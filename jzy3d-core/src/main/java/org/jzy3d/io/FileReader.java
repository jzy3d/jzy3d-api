package org.jzy3d.io;

import java.io.IOException;
import java.util.List;

@Deprecated//(since = "2.0.0", forRemoval = true)
public class FileReader {

  /**
   * @deprecated use {@link SimpleFile#read(String)} instead
   */
  @Deprecated//(since = "2.0.0", forRemoval = true)
  public static List<String> read(String file) throws IOException {
    return SimpleFile.read(file);
  }
}
