package org.jzy3d.io.xls;

import org.apache.poi.ss.usermodel.Row;

public interface XLSObjectIO<T> {
  public void write(T object, int currentLine);
  public T read(Row row);

}
