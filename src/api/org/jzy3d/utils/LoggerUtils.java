package org.jzy3d.utils;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
/**
 * Pattern Layout parameters
 * <ul>
 * <li>%c   class
 * <li>%t   thread
 * <li>%p   level (INFO, DEBUG, ERROR)
 * <li>%-5p include the word in a 5-character width column.
 * <li>%d   date
 * <li>%t   time
 * <li>%n   carriage return
 * </ul>
 * 
 * see http://robertmaldon.blogspot.com/2007/09/programmatically-configuring-log4j-and.html
 * @author Martin Pernollet
 */
public class LoggerUtils {
    public static void minimal() {
        Logger root = Logger.getRootLogger();
        //root.addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));
        root.addAppender(new ConsoleAppender(new PatternLayout("%-5p [%t][%c]: %m%n")));
        level(Level.ERROR);
    }
    
    public static void level(Level level){
        Logger root = Logger.getRootLogger();
        Logger pkgLogger = root.getLoggerRepository().getLogger("com.jzy3d");
        pkgLogger.setLevel(level);
    }
    
    public static void setFileLogger(String file) throws IOException{
    	Logger root = Logger.getRootLogger();
    	root.addAppender(new FileAppender(new PatternLayout("%-5p [%t][%c]: %m%n"), new File(file).getAbsolutePath(), false));
    }
}
