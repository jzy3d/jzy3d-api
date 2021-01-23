package org.jzy3d.io;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileReader {
  public static List<String> read(String file) throws IOException {
    FileInputStream fstream = new FileInputStream(file);
    DataInputStream in = new DataInputStream(fstream);
    BufferedReader br = new BufferedReader(new InputStreamReader(in));

    List<String> out = new ArrayList<String>();
    String strLine;
    while ((strLine = br.readLine()) != null) {
      out.add(strLine);
    }
    in.close();
    return out;
  }
}
