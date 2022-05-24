package org.jzy3d.io.psy4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.jzy3d.io.xls.ExcelBuilder;

public class Psy4jIO {
  private static final int COL_PATIENT = 1;
  private static final int HEADER = 0;

  public static void main(String[] args) throws IOException {
    Psy4jIO p = new Psy4jIO();
    p.load("/Users/Martin/Datasets/psy/");
    
  }

  /* *********************************************** */
  
  protected Map<String, Patient> patients;
  //protected BiMap<Pair<Integer,Integer>, String> questions;
  protected Map<Pair<Integer,Integer>, String> questions;
  
  public Psy4jIO() {}

  public void load(String path) throws IOException {
    patients = new HashMap<>();
    //questions = HashBiMap.create();
    questions = new HashMap<>();
    
    // Search XLS files at path
    List<File> psyFiles = getFiles(path);

    // ----------------------------------------
    // Load XLS files
    //Set<String> questions = new TreeSet<>();
    //Set<String> patientNames = new TreeSet<>();

    PatientCleaner patientFormatter = new PatientCleaner();
    
    int fileId = 0;

    // ----------------------------------------
    // For each XLS file
    for (File formFile : psyFiles) {
      System.out.println(formFile.getAbsolutePath());
      ExcelBuilder formXls = new ExcelBuilder(formFile.getAbsolutePath());

      //questions.addAll(getLineStrings(formXls, HEADER));
      //patientNames.addAll(patientFormatter.clean(getColumnStrings(formXls, COL_PATIENT)));
      //patients.addAll(getColumnStrings(b, 1));
      
      // ----------------------------------------
      // Load and index questions of the form
      int col = 0;
      Cell nextCell = formXls.getCell(HEADER, col);


      while (nextCell != null) {
        String question = nextCell.getStringCellValue();
        questions.put(new Pair<>(fileId, col), question);
        nextCell = formXls.getCell(HEADER, ++col);
      }
      
      
      // ----------------------------------------
      // For each patient
      int line = HEADER+1;
      
      nextCell = formXls.getCell(line, COL_PATIENT);
      
      while (nextCell != null) {
        // Get patient
        String patientName = patientFormatter.clean(nextCell.getStringCellValue().toLowerCase());

        Patient patient = patients.get(patientName);

        if (patient == null) {
          patient = new Patient(patientName);
          patients.put(patientName, patient);
        }

        col = COL_PATIENT + 1;
        nextCell = formXls.getCell(line, col);


        while (nextCell != null) {
          String answer = "";
          if(org.apache.poi.ss.usermodel.CellType.STRING.equals(nextCell.getCellType())) {
            answer = nextCell.getStringCellValue().toLowerCase();
          }
          else {
            answer = nextCell.toString();
          }
          String question = questions.get(new Pair<>(fileId, col));
          //System.out.println(new Pair<>(fileId, col) + " "  + question);
          patient.add(question, answer);

          nextCell = formXls.getCell(line, ++col);
        }
        
        // iterate over line
        nextCell = formXls.getCell(++line, COL_PATIENT);
      }
      
      fileId++;
    }
    // ExcelBuilder b = new ExcelBuilder("/Users/Martin/Datasets/psy/lien1.xlsx");
    // questions.addAll(getLineStrings(b, 0));
    // patients.addAll(getColumnStrings(b, 1));

    /*for (String patient : patientNames) {
      System.out.println(patient);
    }*/

    for (Patient patient : patients.values()) {
      System.out.println(patient.getName()  +"\t" + patient.getAnswers().size());
    }
    
    // Set<String> questions = getPatiens(b);
System.out.println("-------------------");
    System.out.println(questions.size() + " questions");
    System.out.println(patients.size() + " patients");
    System.out.println(psyFiles.size() + " files");

  }

  private List<File> getFiles(String path) {
    File folder = new File(path);

    List<File> psyFiles = new ArrayList<>();

    for (final File fileEntry : folder.listFiles()) {
      if (fileEntry.isDirectory()) {
        // listFilesForFolder(fileEntry);
      } else {
        System.out.println(fileEntry.getName());

        if (fileEntry.getName().endsWith(".xlsx") && !fileEntry.getName().startsWith("~$")) {
          psyFiles.add(fileEntry);
        }
      }
    }
    return psyFiles;
  }

  protected Set<String> getLineStrings(ExcelBuilder b, int line) {
    int i = 0;
    Cell nextCell = b.getCell(line, i);

    Set<String> questions = new TreeSet<>();

    while (nextCell != null) {
      String value = nextCell.getStringCellValue();
      questions.add(value);
      nextCell = b.getCell(line, ++i);
    }
    return questions;
  }

  protected Set<String> getColumnStrings(ExcelBuilder b, int column) {
    int i = 0;
    Cell nextCell = b.getCell(i, column);

    Set<String> patients = new TreeSet<>();

    while (nextCell != null) {
      String value = cleanPatient(nextCell.getStringCellValue().toLowerCase());
      patients.add(value);

      nextCell = b.getCell(++i, column);
    }
    return patients;
  }


  protected String cleanPatient(String original) {
    return original;// .replace(" ", "");
  }

}
