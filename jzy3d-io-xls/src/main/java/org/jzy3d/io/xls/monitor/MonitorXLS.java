package org.jzy3d.io.xls.monitor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.poi.ss.usermodel.Sheet;
import org.jzy3d.io.xls.ExcelBuilder;
import org.jzy3d.io.xls.ExcelBuilder.Type;
import org.jzy3d.monitor.IMonitorable;
import org.jzy3d.monitor.Measure;
import org.jzy3d.monitor.Monitor;

public class MonitorXLS extends Monitor {
  final static String timestamp = "timestamp";
  
  public MonitorXLS() {
    super();
  }

  @Override
  public void load(File file) throws IOException {
    ExcelBuilder xls = new ExcelBuilder(file.getAbsolutePath());
    
    for(Sheet s: xls.getAllSheets()) {
      System.out.println(s);
      
      //for(xls.getCell(i, j))
    }
    
  }
  

  
  
  @Override
  public void dump(File file) throws IOException {
    ExcelBuilder xls = new ExcelBuilder(Type.XLSX);


    for (IMonitorable monitorable : observations.keySet()) {
      Sheet s = xls.newSheet(monitorable.getLabel());
      xls.setCurrentSheet(s);

      List<Measure> measures = observations.get(monitorable);


      // -----------------------
      // CASE OF NO MEASURE
      if (measures.size() > 0) {
        int line = 0;

        //---------------------
        // HEADER
        Map<String, Integer> columnToId = new HashMap<>();
        
        // timestamp on first column
        columnToId.put(timestamp, 0);
        xls.setCell(line, 0, timestamp);

        // all meta dynamically based on first measurement (assume all other have same columns
        Set<String> columns = measures.get(line).getObservationsOrdered();

        int k = 1;
        for (String col : columns) {
          columnToId.put(col, k);
          xls.setCell(line, k, col);
          k++;
        }
        line++;

        //---------------------
        // MEASURES
        for (Measure measure : measures) {
          xls.setCell(line, 0, measure.getTimestamp());
          for (Map.Entry<String, Object> fact : measure.getEntries()) {
            xls.setCell(line, columnToId.get(fact.getKey()), fact.getValue().toString());
          }
          line++;
        }
      } 
      // -----------------------
      // CASE OF NO MEASURE
      else {
        xls.setCell(0, 0, "no entry for " + monitorable.getFullname());
      }


    }

    xls.save(file);
  }

}
