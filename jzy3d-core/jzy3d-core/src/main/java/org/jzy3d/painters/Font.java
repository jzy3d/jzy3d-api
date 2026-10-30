package org.jzy3d.painters;

/**
 * A Font subset supported both by OpenGL 1 and AWT. These fonts can be used both for charts based
 * on native OpenGL and charts based on raw AWT.
 * 
 * Painters such as {@link NativeDesktopPainter} use OpenGL for font rendering and address a font
 * name AND size through a font ID (e.g {@link IPainter#BITMAP_HELVETICA_10},
 * {@link IPainter#BITMAP_HELVETICA_12}, etc).
 * 
 * Painters such as {@link EmulGLPainter} use AWT for font rendering and may support more font names
 * and size than those provided as default to fit OpenGL 1 spec. To use other fonts than the
 * defaults (e.g. {@link Font#Helvetica_12}), simply build them as follow
 * 
 * <code>
 * Font font = new org.jzy3d.painters.Painter.Font("Arial", 11);
 * </code>
 * 
 * Font names not supported by OpenGL 1 will be ignored. Instead, the default font
 * {@link IPainter#BITMAP_HELVETICA_12} will apply.
 */
public class Font {
  
  private static final int UNDEFINED = -1;
  // Font constants below are picked from GLU object in JOGL
  protected static final int STROKE_ROMAN = 0;
  protected static final int STROKE_MONO_ROMAN = 1;
  protected static final int BITMAP_9_BY_15 = 2;
  protected static final int BITMAP_8_BY_13 = 3;
  protected static final int BITMAP_TIMES_ROMAN_10 = 4;
  protected static final int BITMAP_TIMES_ROMAN_24 = 5;
  protected static final int BITMAP_HELVETICA_10 = 6;
  protected static final int BITMAP_HELVETICA_12 = 7;
  protected static final int BITMAP_HELVETICA_18 = 8;

  // OpenGL 1 Fonts, allowing to use glutBitmapString(code,size)
  public static final Font Helvetica_10 = new Font(BITMAP_HELVETICA_10, 10);
  public static final Font Helvetica_12 = new Font(BITMAP_HELVETICA_12, 12);
  public static final Font Helvetica_18 = new Font(BITMAP_HELVETICA_18, 18);
  public static final Font TimesRoman_10 = new Font(BITMAP_TIMES_ROMAN_10, 10);
  public static final Font TimesRoman_24 = new Font(BITMAP_TIMES_ROMAN_24, 24);

  // Font names, allowing to build font by name with related GL code
  protected static final String TIMES_NEW_ROMAN = "Times New Roman";
  protected static final String HELVETICA = "Helvetica";


  protected String name;
  protected int code;
  protected int height;
  protected int style;

  protected Font() {
  }
  
  public Font(int code, int height) {
    this.code = code;
    this.style = UNDEFINED;
    this.height = height;

    detectFontNameFromOpenGLCode(code);
  }

  public Font(String name, int height) {
    this(name, UNDEFINED, height);
  }

  /**
   * Build a font from its name.
   * 
   * Its OpenGL code will be guessed to ensure the font is usable with OpenGL 1 renderer supporting
   * a limited font set. If the font name is not recognized among such set, the default
   * {@link #BITMAP_HELVETICA_12} will be loaded.
   * 
   * See also {@link AWTFont.toAWT(font)} to convert to AWT an font.
   * 
   * @param name
   * @param style a style value. May not be supported by the text renderer
   * @param height
   */
  public Font(String name, int style, int height) {
    this.name = name;
    this.style = style;
    this.height = height;

    detectOpenGLFontCodeFromName(name, height);
  }

  public String getName() {
    return name;
  }

  public int getCode() {
    return code;
  }

  public int getHeight() {
    return height;
  }

  public int getStyle() {
    return style;
  }
  
  public void setHeight(int height) {
    this.height = height;
  }

  protected void detectFontNameFromOpenGLCode(int code) {
    if (code == BITMAP_HELVETICA_10 || code == BITMAP_HELVETICA_12 || code == BITMAP_HELVETICA_18) {
      this.name = HELVETICA;
    } else if (code == BITMAP_TIMES_ROMAN_10 || code == BITMAP_TIMES_ROMAN_24) {
      this.name = TIMES_NEW_ROMAN;
    }
  }

  protected void detectOpenGLFontCodeFromName(String name, int height) {
    if (HELVETICA.equalsIgnoreCase(name.toLowerCase())) {
      switch (height) {
        case 10:
          code = BITMAP_HELVETICA_10;
          break;
        case 12:
          code = BITMAP_HELVETICA_12;
          break;
        case 18:
          code = BITMAP_HELVETICA_18;
          break;
        default:
          code = BITMAP_HELVETICA_12;
          break;
      }
    } else if (TIMES_NEW_ROMAN.equalsIgnoreCase(name.toLowerCase())) {
      switch (height) {
        case 10:
          code = BITMAP_TIMES_ROMAN_10;
          break;
        case 24:
          code = BITMAP_TIMES_ROMAN_24;
          break;
        default:
          code = BITMAP_TIMES_ROMAN_10;
          break;
      }
    } else { // default
      code = BITMAP_HELVETICA_12;
    }
  }
  
  /** Load a font from its ID */
  public static Font getById(int id) {
    switch (id) {
      case BITMAP_HELVETICA_10:
        return Helvetica_10;
      case BITMAP_HELVETICA_12:
        return Helvetica_12;
      case BITMAP_HELVETICA_18:
        return Helvetica_18;
      case BITMAP_TIMES_ROMAN_10:
        return TimesRoman_10;
      case BITMAP_TIMES_ROMAN_24:
        return TimesRoman_24;
      default:
        return null;
    }
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + code;
    result = prime * result + height;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + style;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Font other = (Font) obj;
    if (code != other.code)
      return false;
    if (height != other.height)
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (style != other.style)
      return false;
    return true;
  }
  
  
  @Override
  public String toString() {
    return "Font: '" + name + "' height:" + height + " code:" + code + " style:" + style;
  }
  
  public Font clone() {
    Font f = new Font();
    f.name = name;
    f.code = code;
    f.style = style;
    f.height = height;
    return f;
  }
  
}
