package org.jzy3d.io.psy4j;

import java.util.HashSet;
import java.util.Set;

public class PatientCleaner implements Cleaner {

  public PatientCleaner() {}

  @Override
  public String clean(String input) {
    String cleaned = escapeCharacters(input);
    cleaned = replaceMonthNameById(cleaned);
    cleaned = formatBirthDate(cleaned);
    
    return cleaned;
  }

  private String formatBirthDate(String cleaned) {
    if(cleaned.length()==9){
      // abc/MM/YYYY
      return cleaned.substring(0, 5) + cleaned.substring(7);
    }
    return cleaned;
  }

  private String escapeCharacters(String input) {
    return input.replace(" ", "").replace("é", "e").replace("è", "e").replace("û", "u").replace("/", "");
  }

  private String replaceMonthNameById(String cleaned) {
    cleaned = cleaned.replace("janvier", "01");
    cleaned = cleaned.replace("fevrier", "02");
    cleaned = cleaned.replace("mars", "03");
    cleaned = cleaned.replace("avril", "04");
    cleaned = cleaned.replace("mai", "05");
    cleaned = cleaned.replace("juin", "06");
    cleaned = cleaned.replace("juillet", "07");
    cleaned = cleaned.replace("aout", "08");
    cleaned = cleaned.replace("septembre", "09");
    cleaned = cleaned.replace("octobre", "10");
    cleaned = cleaned.replace("novembre", "11");
    cleaned = cleaned.replace("decembre", "12");
    return cleaned;
  }

  @Override
  public Set<String> clean(Set<String> input) {
    Set<String> set = new HashSet<>();
    for (String s : input) {
      set.add(clean(s));
    }
    return set;
  }

}
