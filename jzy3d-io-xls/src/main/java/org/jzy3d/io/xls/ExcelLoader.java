package org.jzy3d.io.xls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public abstract class ExcelLoader<T> {
  public ExcelLoader() {
    super();
  }
  
  public abstract T readRow(Row row);
  
  /** Might be refine by subclasses that may use this method as a line filter. */
  protected boolean accepts(Row row) {
    return !isNull(row);
  }
  
  protected boolean isNull(Row row) {
    return row == null;
  }

  /** Stops when a maximum number of expected lines has been reached. 
   * 
   * Ignore rows that do not fullfill {@link accepts}.
   */
  public List<T> load(String file, int from, int to) throws IOException {
    List<T> tasks = new ArrayList<T>();
    
    ExcelBuilder xls = new ExcelBuilder(file);
    
    Sheet sheet = xls.getCurrentSheet();
    
    
    for (int j = from; j < to; j++) {
      Row row = sheet.getRow(j);
      if (accepts(row)) {
        T task = readRow(row);
        
        if(task!=null)
          tasks.add(task);
      }
      else {
        //System.out.println(j + ":" + readCellString(row, 0) );
      }
    }
    
    return tasks;
  }
  
  /** Stops when a row is null. */
  public List<T> load(String file, int from) throws IOException {
    List<T> tasks = new ArrayList<T>();
    
    ExcelBuilder xls = new ExcelBuilder(file);
    
    Sheet sheet = xls.getCurrentSheet();
    
    int j = from;
    while(true) {
      Row row = sheet.getRow(j);
      if(!isNull(row)) {
        if (accepts(row)) {
          T task = readRow(row);
          
          if(task!=null)
            tasks.add(task);
        }
        else {
          //System.out.println(j + ":" + readCellString(row, 0) );
          //break;
        }
      }
      else {
        //System.out.println(j + ": STOP LOADING" );
        break;
      }

      j++;
    }
    
    return tasks;
  }
    
  public String readCellString(Row row, int cellId) {
    Cell c = row.getCell(cellId);
    if(c!=null) {
      if(CellType.STRING.equals(c.getCellType())) {
        return c.getStringCellValue();                
      }
      else if(CellType.NUMERIC.equals(c.getCellType())) {
        return c.getNumericCellValue()+"";                
      }
      else {
        return c.getStringCellValue();  // let POI handle to string conversion
      }
    }
    else {
      return null;
    }
  }
  
  public Double readCellDouble(Row row, int cellId) {
    Cell c = row.getCell(cellId);
    if(c!=null) {
      return c.getNumericCellValue();        
    }
    else {
      return new Double(0);
    }
  }
}