
package org.jzy3d.io;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimpleDir {

  public SimpleDir() {}

  public static List<File> getAllFolders(File file) throws IOException {
    if (!file.exists())
      throw new IOException(
          (new StringBuilder()).append("File does not exist:").append(file).toString());
    List<File> output = new ArrayList<File>();
    File folders[] = listDir(file);
    File arr$[] = folders;
    int len$ = arr$.length;
    for (int i$ = 0; i$ < len$; i$++) {
      File f = arr$[i$];
      output.add(f);
    }

    return output;
  }

  public static File[] listFile(File dir) {
    FileFilter fileFilter = new FileFilter() {

      @Override
      public boolean accept(File file) {
        return !file.isDirectory();
      }

    };
    return dir.listFiles(fileFilter);
  }

  public static File[] listDir(File dir) {
    FileFilter fileFilter = new FileFilter() {

      @Override
      public boolean accept(File file) {
        return file.isDirectory();
      }

    };
    return dir.listFiles(fileFilter);
  }

  public static List<File> getAllFiles(List<File> file) throws IOException {
    if (file.size() == 0)
      return new ArrayList<File>(0);
    if (file.size() == 1)
      return getAllFiles(file.get(0));
    List<File> out = new ArrayList<File>();
    File f;
    for (Iterator<File> i$ = file.iterator(); i$.hasNext(); out.addAll(getAllFiles(f)))
      f = i$.next();

    return out;
  }

  public static List<File> getAllFiles(File file) throws IOException {
    if (!file.exists())
      throw new IOException(
          (new StringBuilder()).append("File does not exist:").append(file).toString());
    File files[] = listFile(file);
    File folders[] = listDir(file);
    List<File> out = new ArrayList<File>();
    File arr$[] = files;
    int len$ = arr$.length;
    for (int i$ = 0; i$ < len$; i$++) {
      File f = arr$[i$];
      out.add(f);
    }

    arr$ = folders;
    len$ = arr$.length;
    for (int i$ = 0; i$ < len$; i$++) {
      File f = arr$[i$];
      out.addAll(getAllFiles(f));
    }

    return out;
  }
}
