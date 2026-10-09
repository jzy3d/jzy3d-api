package org.jzy3d.io.glsl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.jzy3d.painters.GLConstants;
import org.jzy3d.painters.IPainter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Below is description of the GLSL program lifecycle, with Jzy3d methods, and underlying OpenGL
 * methods.
 * <ul>
 * <li>load shaders with {@link attachVertexShader()} and {@link attachFragmentShader()}
 * <ul>
 * <li>glCreateShader
 * <li>glShaderSource
 * <li>glCompileShader
 * <li>glGetShaderiv (verify status)
 * <li>glGetShaderInfoLog (log errors)
 * </ul>
 * <li>link(painter) links the compiled shaders
 * <ul>
 * <li>glCreateProgram
 * <li>glAttachShader
 * <li>glLinkProgram
 * <li>glGetProgramiv (verify status)
 * <li>glGetProgramInfoLog (log errors)
 * <li>glValidateProgram
 * </ul>
 * <li>bind(painter) mount the program @ rendering
 * <ul>
 * <li>glUseProgram
 * </ul>
 * <li>{@link bindTextureRECT(painter)}
 * <li>unbind(painter) unmount the program @ rendering
 * <ul>
 * <li>glUseProgram(0)
 * </ul>
 * <li>destroy(painter)
 * <ul>
 * <li>glDeleteShader
 * <li>glDeleteProgram
 * </ul>
 * </ul>
 *
 */
public class GLSLProgram {
  /**
   * Control the behaviour of a GLSL program with errors (throwing exceptions, create warnings, etc)
   */
  public enum Strictness {
    /** Let the GLSL program throw {@link RuntimeException}s on warnings. */
    MAXIMAL,
    /** Let the GLSL program be verbose through {@link System.out.println()}. */
    CONSOLE,
    /**
     * Let the GLSL program be verbose through {@link System.out.println()}, unless the warning is
     * due to a uniform that is set by GL but not actually used by the compiled shader.
     */
    CONSOLE_NO_WARN_UNIFORM_NOT_FOUND,
    /** Let the GLSL program push warnings to a {@link StringBuffer} to be read. */
    BUFFER,
    /** Keeps the GLSL program quiet on warnings */
    NONE
  }

  public static Strictness DEFAULT_STRICTNESS = Strictness.CONSOLE;

  public static boolean WARN_SHOW_SHADER_SOURCE = true;
  
  protected static Logger log = LoggerFactory.getLogger(GLSLProgram.class);

  protected Integer programId;
  protected List<Integer> vertexShaders_ = new ArrayList<Integer>();
  protected List<Integer> fragmentShaders_ = new ArrayList<Integer>();
  protected StringBuffer warnBuffer;
  protected Strictness strictness;
  
  
  public GLSLProgram() {
    this(DEFAULT_STRICTNESS);
  }

  public GLSLProgram(Strictness strictness) {
    this.strictness = strictness;
    this.programId = 0; // will be defined @ link stage by GL
    if (strictness == Strictness.BUFFER)
      warnBuffer = new StringBuffer();
  }

  public void link(IPainter painter) {
    link(painter, true);
  }
  
  /**
   * Create a program and attach previously loaded and compiled shaders. Performs validation and
   * warn according to program strictness.
   */
  public void link(IPainter painter, boolean validateImmediatly) {
    programId = painter.glCreateProgram();
    for (int i = 0; i < vertexShaders_.size(); i++) {
      painter.glAttachShader(programId, vertexShaders_.get(i));
    }

    for (int i = 0; i < fragmentShaders_.size(); i++) {
      painter.glAttachShader(programId, fragmentShaders_.get(i));
    }

    painter.glLinkProgram(programId);
    verifyLinkStatus(painter, programId);

    // validation
    if(validateImmediatly)
      validateProgram(painter);
  }

  public void bind(IPainter painter) {
    painter.glUseProgram(programId);
  }

  public void unbind(IPainter painter) {
    painter.glUseProgram(0);
  }

  public void destroy(IPainter painter) {
    for (int i = 0; i < vertexShaders_.size(); i++) {
      painter.glDeleteShader(vertexShaders_.get(i));
    }
    for (int i = 0; i < fragmentShaders_.size(); i++) {
      painter.glDeleteShader(fragmentShaders_.get(i));
    }
    if (programId != 0) {
      painter.glDeleteProgram(programId);
    }
  }

  /* UNIFORM SETTING */

  public void setUniform(IPainter painter, String name, float value) {
    int id = painter.glGetUniformLocation(programId, name);
    painter.glUniform1f(id, value);
  }

  public void setUniform(IPainter painter, String name, float[] values, int count) {
    int id = painter.glGetUniformLocation(programId, name);
    if (id == -1) {
      warn("Uniform parameter not found in program: " + name, GLSLWarnType.UNIFORM_NOT_FOUND);
      return;
    }
    switch (count) {
      case 1:
        painter.glUniform1fv(id, 1, values, 0);
        break;
      case 2:
        painter.glUniform2fv(id, 1, values, 0);
        break;
      case 3:
        painter.glUniform3fv(id, 1, values, 0);
        break;
      case 4:
        painter.glUniform4fv(id, 1, values, 0);
        break;
    }
  }

  /* TEXTURES */

  public void setTextureUnit(IPainter painter, String texname, int texunit) {
    int[] params = new int[] {0};
    painter.glGetProgramiv(programId, GLConstants.GL_LINK_STATUS, params, 0);
    if (params[0] != 1) {
      throw new RuntimeException("Error: setTextureUnit needs program to be linked.");
    }
    int id = painter.glGetUniformLocation(programId, texname);
    if (id == -1) {
      warn("Invalid texture " + texname, GLSLWarnType.UNDEFINED);
      return;
    }
    painter.glUniform1i(id, texunit);
  }

  public void bindTexture(IPainter painter, int target, String texname, int texid, int texunit) {
    painter.glActiveTexture(GLConstants.GL_TEXTURE0 + texunit);
    painter.glBindTexture(target, texid);
    setTextureUnit(painter, texname, texunit);
    painter.glActiveTexture(GLConstants.GL_TEXTURE0);
  }

  public void bindTexture2D(IPainter painter, String texname, int texid, int texunit) {
    bindTexture(painter, GLConstants.GL_TEXTURE_2D, texname, texid, texunit);
  }

  public void bindTexture3D(IPainter painter, String texname, int texid, int texunit) {
    bindTexture(painter, GLConstants.GL_TEXTURE_3D, texname, texid, texunit);
  }

  public void bindTextureRECT(IPainter painter, String texname, int texid, int texunit) {
    bindTexture(painter, GLConstants.GL_TEXTURE_RECTANGLE_ARB, texname, texid, texunit);
  }

  /* LOAD */

  public void loadAndCompileShaders(IPainter painter, ShaderFilePair files) {
    loadAndCompileVertexShader(painter, files.getVertexStream(), files.getVertexURL());
    loadAndCompileFragmentShader(painter, files.getFragmentStream(), files.getFragmentURL());
  }

  public void loadAndCompileVertexShader(IPainter painter, URL fileURL) {
    if (fileURL != null) {
      try {
        InputStream stream = fileURL.openStream();
        loadAndCompileVertexShader(painter, stream, fileURL);
      } catch (IOException e) {
        throw new RuntimeException("Problem reading the shader file " + fileURL.getPath());
      }
    } else {
      throw new RuntimeException("input url is null");
    }
  }

  public void loadAndCompileVertexShader(IPainter painter, InputStream stream) {
    loadAndCompileVertexShader(painter, stream, null);
  }

  /**
   * 
   * @param painter
   * @param stream shader source code ressource
   * @param infoURL only used as information for warnings if shader does not compile properly
   */
  public void loadAndCompileVertexShader(IPainter painter, InputStream stream, URL infoURL) {
    String content = "";
    BufferedReader input = new BufferedReader(new InputStreamReader(stream));
    String line = null;

    try {
      while ((line = input.readLine()) != null) {
        content += line + "\n";
      }
    } catch (IOException kIO) {
      throw new RuntimeException("Problem reading the shader stream (" + infoURL + ")");
    } finally {
      try {
        if (input != null) {
          input.close();
        }
      } catch (IOException closee) {
      }
    }
    compileVertexShader(painter, infoURL, content);
  }

  public void loadAndCompileFragmentShader(IPainter painter, URL fileURL) {
    if (fileURL != null) {
      InputStream stream;
      try {
        stream = fileURL.openStream();
        loadAndCompileFragmentShader(painter, stream, fileURL);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

    } else {
      throw new RuntimeException("Null shader file!");
    }
  }

  public void loadAndCompileFragmentShader(IPainter painter, InputStream stream) {
    loadAndCompileFragmentShader(painter, stream, null);
  }

  public void loadAndCompileFragmentShader(IPainter painter, InputStream stream, URL infoURL) {
    String content = "";
    BufferedReader input = new BufferedReader(new InputStreamReader(stream));
    String line = null;

    try {
      while ((line = input.readLine()) != null) {
        content += line + "\n";
      }
    } catch (IOException kIO) {
      throw new RuntimeException("Problem reading the shader file " + infoURL);
    } finally {
      try {
        if (input != null) {
          input.close();
        }
      } catch (IOException closee) {
      }
    }

    compileFragmentShader(painter, infoURL, content);
  }

  /* COMPILE */

  public void compileVertexShader(IPainter painter, URL infoURL, String content) {
    int iID = painter.glCreateShader(GLConstants.GL_VERTEX_SHADER);

    painter.glShaderSource(iID, new String[] {content});
    painter.glCompileShader(iID);

    verifyShaderCompiled(painter, infoURL, iID, content);
    vertexShaders_.add(iID);
  }

  public void compileFragmentShader(IPainter painter, URL infoURL, String content) {
    int iID = painter.glCreateShader(GLConstants.GL_FRAGMENT_SHADER);

    painter.glShaderSource(iID, new String[] {content});
    painter.glCompileShader(iID);
    
    verifyShaderCompiled(painter, infoURL, iID, content);
    
    fragmentShaders_.add(iID);
  }

  /* VERIFICATIONS */

  public void verifyShaderCompiled(IPainter painter, URL fileURL, int programId, String content) {
    int[] compileStatus = new int[] {0};
    int[] logLength = new int[] {0};

    painter.glGetShaderiv(programId, GLConstants.GL_COMPILE_STATUS, compileStatus, 0);
    painter.glGetShaderiv(programId, GLConstants.GL_INFO_LOG_LENGTH, logLength, 0);
    //System.out.println(content); 
    
    if (compileStatus[0] != GLConstants.GL_TRUE) {
      warnScript(painter, fileURL, readErrors(painter, programId), compileStatus[0], logLength[0], content);
    }
  }

  public void verifyLinkStatus(IPainter painter, int programId) {
    int[] linkStatus = new int[] {0};
    int[] logLength = new int[] {0};
    
    painter.glGetProgramiv(programId, GLConstants.GL_LINK_STATUS, linkStatus, 0);
    painter.glGetProgramiv(programId, GLConstants.GL_INFO_LOG_LENGTH, logLength, 0);

    if (linkStatus[0] != 1) {
      warnLink(painter, readProgramErrors(painter, programId), linkStatus[0], logLength[0]);
    }
  }

  public String readErrors(IPainter painter, int iID) {
    return painter.glGetShaderInfoLog(iID);
  }

  public String readProgramErrors(IPainter painter, int programId) {
    return painter.glGetProgramInfoLog(programId);
  }

  public void validateProgram(IPainter painter) {
    painter.glValidateProgram(programId);
    checkShaderLogInfo(painter, programId);
  }

  /**
   * read logs and either throw exception, print to console or append to error log according to
   * the configured {@link Strictness}
   */
  protected void checkShaderLogInfo(IPainter painter, int programObjectID) {
    int[] logLength = new int[] {0};
    painter.glGetProgramiv(programObjectID, GLConstants.GL_INFO_LOG_LENGTH, logLength, 0);

    if (logLength[0] <= 1) {
      return;
    }

    // Read logs and warn
    String shaderValidationLog = painter.glGetProgramInfoLog(programObjectID);
    StringReader reader = new StringReader(shaderValidationLog);
    LineNumberReader lineNumberReader = new LineNumberReader(reader);
    
    String currentLine;
    try {
      while ((currentLine = lineNumberReader.readLine()) != null) {
        if (currentLine.trim().length() > 0) {
          warn("GLSL VALIDATION: " + currentLine.trim(), GLSLWarnType.UNDEFINED);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /* WARNINGS */

  protected void warnScript(IPainter painter, URL fileURL, String error, int compileStatus, int logLength,
      String content) {
    if (fileURL != null)
      warn(fileURL.getPath(), GLSLWarnType.UNDEFINED);
    else
      warn("unknown file", GLSLWarnType.UNDEFINED);
    warn("compile status: " + compileStatus + " (GL_TRUE=" + GLConstants.GL_TRUE + ", GL_FALSE="
        + GLConstants.GL_FALSE + ")", GLSLWarnType.UNDEFINED);
    warn("log length: " + logLength, GLSLWarnType.UNDEFINED);
    warn(error, GLSLWarnType.UNDEFINED);

    if (WARN_SHOW_SHADER_SOURCE)
      warn(content, GLSLWarnType.UNDEFINED);
  }

  protected void warnLink(IPainter painter, String error, int linkStatus, int logLength) {
    warn("link status: " + linkStatus, GLSLWarnType.UNDEFINED);
    warn("log length: " + logLength, GLSLWarnType.UNDEFINED);
    warn(error, GLSLWarnType.UNDEFINED);
  }

  protected void warn(String info, GLSLWarnType type) {

    if (strictness == Strictness.MAXIMAL)
      throw new RuntimeException(info);
    else if (strictness == Strictness.CONSOLE)
      System.err.println(this.getClass().getSimpleName() + ": " + info);
    else if (strictness == Strictness.CONSOLE_NO_WARN_UNIFORM_NOT_FOUND
        && type != GLSLWarnType.UNIFORM_NOT_FOUND) {
      System.err.println(this.getClass().getSimpleName() + ": " + info);
    } else if (strictness == Strictness.BUFFER)
      warnBuffer.append(info + "\n");
    else if (strictness == Strictness.NONE)
      ; // do nothing
  }

  public enum GLSLWarnType {
    UNDEFINED, UNIFORM_NOT_FOUND
  }

  /* */

  public Integer getProgramId() {
    return programId;
  }


}
