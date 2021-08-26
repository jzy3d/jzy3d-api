package org.jzy3d.io;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class SimpleFile {
  public static void write(String content, String file) throws Exception {
    createParentFoldersIfNotExist(file);
    Writer out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
    out.write(content);
    out.close();
  }

  public static void createParentFoldersIfNotExist(String file) {
    File parent = (new File(file)).getParentFile();
    if (parent != null && !parent.exists())
      parent.mkdirs();
  }

  public static List<String> readFile(File file) throws IOException {
    return read(file.getAbsolutePath());
  }

  public static List<String> read(String filename) throws IOException {
    List<String> output = new ArrayList<String>();
    File file = new File(filename);
    FileInputStream fis = new FileInputStream(file);
    BufferedInputStream bis = new BufferedInputStream(fis);
    DataInputStream dis = new DataInputStream(bis);

    while (dis.available() != 0) {
      output.add(dis.readLine());
    }


    fis.close();
    bis.close();
    dis.close();
    return output;
  }

  public static String readAsString(String filename) throws Exception {
    return readAsString(filename, "\n");
  }

  public static String readAsString(String filename, String newLineString) throws IOException {
    StringBuffer sb = new StringBuffer();
    File file = new File(filename);
    FileInputStream fis = new FileInputStream(file);
    BufferedInputStream bis = new BufferedInputStream(fis);
    DataInputStream dis = new DataInputStream(bis);
    while (dis.available() != 0) {
      StringBuilder sb2 = new StringBuilder().append(dis.readLine());
      if(newLineString!=null) {
        sb2.append(newLineString);
      }
      sb.append(sb2);
    }
    fis.close();
    bis.close();
    dis.close();
    return sb.toString();
  }

  /**
   * Return true if file1 is younger than file2, meaning it was last modified after file2. Always
   * return false if file2 does not exist. Always return true if file1 does not exist.
   */
  public static boolean isYounger(String file1, String file2) throws IOException {
    File f1 = new File(file1);
    File f2 = new File(file2);
    if (!f2.exists())
      return false;
    if (!f1.exists())
      return true;
    else
      return f1.lastModified() > f2.lastModified();
  }
}
