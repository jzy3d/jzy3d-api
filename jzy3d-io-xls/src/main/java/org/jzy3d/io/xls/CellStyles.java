package org.jzy3d.io.xls;

import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class CellStyles {
  Map<String, CellStyle> styles = new HashMap<>();

  public CellStyle getStyle(String user) {
    return this.styles.get(user);
  }

  public void setStyle(String name, CellStyle style) {
    this.styles.put(name, style);
  }

  public void load(Sheet sheet) {
 // Analyze all projects
    boolean read = true;
    int i = 0;
    while (read) {
      Row row = sheet.getRow(i);
      if (row == null) {
        read = false;
        break;
      }

      // Read line
      Cell c = row.getCell(0);
      
      if(c!=null) {
        String name = c.getStringCellValue();        
        CellStyle style = c.getCellStyle();
        setStyle(name, style);
      }
      
      i++;
    }
  }
}
