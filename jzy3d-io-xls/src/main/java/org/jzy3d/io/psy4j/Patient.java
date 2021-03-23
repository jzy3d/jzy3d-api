package org.jzy3d.io.psy4j;

import java.util.HashMap;
import java.util.Map;

public class Patient {
  protected String name;
  protected Map<String,String> answers;
  
  public Patient(String name) {
    this.name = name;
    this.answers = new HashMap<>();
  }
  
  public void add(String question, String answer) {
    answers.put(question, answer);
  }

  public String getAnswer(String question) {
    return answers.get(question);
  }

  public String getName() {
    return name;
  }

  public Map<String, String> getAnswers() {
    return answers;
  }
}
