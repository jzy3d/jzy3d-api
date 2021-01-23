package org.jzy3d.maths;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
  /**
   * Convert a number into a string, with <code>precision</code> number of meaningfull digits.
   * 
   * 'd' integral The result is formatted as a decimal integer 'o' integral The result is formatted
   * as an octal integer 'x', 'X' integral The result is formatted as a hexadecimal integer 'e', 'E'
   * floating point The result is formatted as a decimal number in computerized scientific notation
   * 'f' floating point The result is formatted as a decimal number 'g', 'G' floating point The
   * result is formatted using computerized scientific notation or decimal format, depending on the
   * precision and the value after rounding.
   * 
   * @see http ://java.sun.com/j2se/1.5.0/docs/api/java/util/Formatter.html#syntax
   * @see String.format
   */
  public static String num2str(char parseMode, double num, int precision) {
    return String.format("%." + precision + parseMode, new Double(num));
  }

  /**
   * Same as {@link num2str(char parseMode, double num, int precision)} but does not query
   * precision.
   */
  public static String num2str(char parseMode, double num) {
    return String.format("%" + parseMode, new Double(num));
  }

  public static String num2str(double num, int precision) {
    return num2str('g', num, precision);
  }

  /** Convert a number into a string. */
  public static String num2str(double num) {
    return Double.toString(num);
  }

  /*************************************************************/

  /** Convert a date to the format "dd/MM/yyyy HH:mm:ss". */
  public static String dat2str(Date date) {
    return dat2str(date, "dd/MM/yyyy HH:mm:ss");
  }

  /**
   * Some example format dd.MM.yy 09.04.98 yyyy.MM.dd G 'at' hh:mm:ss z 1998.04.09 AD at 06:15:55
   * PDT EEE, MMM d, ''yy Thu, Apr 9, '98 h:mm a 6:15 PM H:mm 18:15 H:mm:ss:SSS 18:15:55:624 K:mm
   * a,z 6:15 PM,PDT yyyy.MMMMM.dd GGG hh:mm aaa 1998.April.09 AD 06:15 PM
   * 
   * @see http ://java.sun.com/docs/books/tutorial/i18n/format/simpleDateFormat. html
   * @param date
   * @param format
   * @return
   */
  public static String dat2str(Date date, String format) {
    if (date == null)
      return "";
    SimpleDateFormat formatter;
    formatter = new SimpleDateFormat(format, Locale.getDefault());
    return formatter.format(date);
  }

  /*************************************************************/

  public static long dat2num(Date date) {
    if (date == null)
      return 0;
    return date.getTime();
  }

  public static Date num2dat(long value) {
    return new Date(value);
  }

  public static String time2str(long milli) {
    StringBuilder buf = new StringBuilder(20);
    String sgn = "";

    if (milli < 0) {
      sgn = "-";
      milli = Math.abs(milli);
    }

    append(buf, sgn, 0, (milli / 3600000));
    append(buf, ":", 2, ((milli % 3600000) / 60000));
    append(buf, ":", 2, ((milli % 60000) / 1000));
    append(buf, ".", 3, (milli % 1000));
    return buf.toString();
  }

  /**
   * Append a right-aligned and zero-padded numeric value to a `StringBuilder`.
   */
  private static void append(StringBuilder tgt, String pfx, int dgt, long val) {
    tgt.append(pfx);
    if (dgt > 1) {
      int pad = (dgt - 1);
      for (long xa = val; xa > 9 && pad > 0; xa /= 10) {
        pad--;
      }
      for (int xa = 0; xa < pad; xa++) {
        tgt.append('0');
      }
    }
    tgt.append(val);
  }

  public static String blanks(int length) {
    String b = "";
    for (int i = 0; i < length; i++)
      b += " ";
    return b;
  }

  /*****************************************************************************/

  /**
   * Computes the absolute values of an array of doubles.
   * 
   * @param values
   * @return the sum of input values
   */
  public static double[] abs(double[] values) {
    double[] output = new double[values.length];

    for (int i = 0; i < values.length; i++)
      output[i] = Math.abs(values[i]);
    return output;
  }

  /**
   * Computes the sum of an array of doubles. NaN values are ignored during the computation.
   * 
   * @param values
   * @return the sum of input values
   */
  public static double sum(double[] values) {
    if (values.length == 0)
      throw new IllegalArgumentException("Input array must have a length greater than 0");

    double total = 0;

    for (int i = 0; i < values.length; i++)
      if (!Double.isNaN(values[i]))
        total += values[i];
    return total;
  }

  /**
   * Computes the sum of an array of doubles. NaN values are ignored during the computation.
   * 
   * @param values
   * @return the sum of input values
   */
  public static int sum(int[] values) {
    if (values.length == 0)
      throw new IllegalArgumentException("Input array must have a length greater than 0");

    int total = 0;
    for (int i = 0; i < values.length; i++)
      total += values[i];
    return total;
  }

  /**
   * Generate a vector containing regular increasing values from min to max, with an offset of
   * nstep.
   * 
   * @param min
   * @param max
   * @param nstep
   * @return
   */
  public static double[] vector(double min, double max, int nstep) {
    double step = (max - min) / (nstep - 1);
    double[] grid = new double[nstep];

    for (int i = 0; i < grid.length; i++) {
      grid[i] = min + i * step;
    }
    return grid;
  }

  /**
   * Generate a vector containing regular increasing values from min to max, with an offset 1.
   * 
   * @param min
   * @param max
   * @return
   */
  public static double[] vector(double min, double max) {
    return vector(min, max, (int) Math.abs(max - min) + 1);
  }

  /**
   * Generate a vector containing regular increasing values from min to max, with an offset of
   * nstep.
   * 
   * @param min
   * @param max
   * @param nstep
   * @return
   */
  public static int[] vector(int min, int max, int nstep) {
    int step = (max - min) / (nstep - 1);
    int[] grid = new int[nstep];

    for (int i = 0; i < grid.length; i++) {
      grid[i] = min + i * step;
    }
    return grid;
  }

  /**
   * Generate a vector containing regular increasing values from min to max, with an offset 1.
   * 
   * @param min
   * @param max
   * @return
   */
  public static int[] vector(int min, int max) {
    return vector(min, max, Math.abs(max - min) + 1);
  }

  /**
   * Return the minimal date of an array
   * 
   * @param dates
   * @return
   */
  public static Date min(Date[] dates) {
    if (dates.length == 0)
      throw new RuntimeException("input array is empty");
    Date min = new Date(Long.MAX_VALUE);

    for (int i = 0; i < dates.length; i++)
      if (min.after(dates[i]))
        min = dates[i];
    return min;
  }

  /**
   * Return the maximal date of an array
   * 
   * @param dates
   * @return
   */
  public static Date max(Date[] dates) {
    if (dates.length == 0)
      throw new RuntimeException("input array is empty");
    Date max = new Date(-Long.MAX_VALUE);

    for (int i = 0; i < dates.length; i++)
      if (max.before(dates[i]))
        max = dates[i];
    return max;
  }

  public static Date str2date(String string) throws ParseException {
    DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
    Date date = format.parse(string);
    return date;
  }
}
