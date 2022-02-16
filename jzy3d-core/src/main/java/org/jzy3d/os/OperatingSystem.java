package org.jzy3d.os;

public class OperatingSystem {

  protected String name = System.getProperty("os.name").toLowerCase();
  protected boolean windows = (name.indexOf("win") >= 0);
  protected boolean mac = (name.indexOf("mac") >= 0);
  protected boolean unix =
      (name.indexOf("nix") >= 0 || name.indexOf("nux") >= 0 || name.indexOf("aix") > 0);
  protected boolean solaris = (name.indexOf("sunos") >= 0);

  public OperatingSystem() {
    this(System.getProperty("os.name"));  
  }
  
  /** Mainly for test purpose.*/
  public OperatingSystem(String name) {
    this.name = name.toLowerCase();
    
    this.windows = (name.indexOf("win") >= 0);
    this.mac = (name.indexOf("mac") >= 0);
    this.unix =
        (name.indexOf("nix") >= 0 || name.indexOf("nux") >= 0 || name.indexOf("aix") > 0);
    this.solaris = (name.indexOf("sunos") >= 0);    
  }
  
  public String getName() {
    return name;
  }

  public boolean isWindows() {
    return windows;
  }

  public boolean isMac() {
    return mac;
  }

  public boolean isUnix() {
    return unix;
  }

  public boolean isSolaris() {
    return solaris;
  }



  public static void main(String[] args) {
    OperatingSystem os = new OperatingSystem();

    System.out.println("os.name: " + os.getName());

    if (os.isWindows()) {
      System.out.println("This is Windows");
    } else if (os.isMac()) {
      System.out.println("This is Mac");
    } else if (os.isUnix()) {
      System.out.println("This is Unix or Linux");
    } else if (os.isSolaris()) {
      System.out.println("This is Solaris");
    } else {
      System.out.println("Your OS is not support!!");
    }
  }

}
