package org.jzy3d.io.glsl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.io.BufferUtil;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;

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
 * <li>link(gl) links the compiled shaders
 * <ul>
 * <li>glCreateProgram
 * <li>glAttachShader
 * <li>glLinkProgram
 * <li>glGetProgramiv (verify status)
 * <li>glGetProgramInfoLog (log errors)
 * <li>glValidateProgram
 * </ul>
 * <li>bind(gl) mount the program @ rendering
 * <ul>
 * <li>glUseProgram
 * </ul>
 * <li>{@link bindTextureRECT(gl)}
 * <li>unbind(gl) unmount the program @ rendering
 * <ul>
 * <li>glUseProgram(0)
 * </ul>
 * <li>destroy(gl)
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
  
  protected static Logger log = LogManager.getLogger(GLSLProgram.class);

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

  /**
   * Create a program and attach previously loaded and compiled shaders. Performs validation and
   * warn according to program strictness.
   */
  public void link(GL2 gl) {
    programId = gl.glCreateProgram();
    for (int i = 0; i < vertexShaders_.size(); i++) {
      gl.glAttachShader(programId, vertexShaders_.get(i));
    }

    for (int i = 0; i < fragmentShaders_.size(); i++) {
      gl.glAttachShader(programId, fragmentShaders_.get(i));
    }

    gl.glLinkProgram(programId);
    verifyLinkStatus(gl, programId);

    // validation
    validateProgram(gl);
  }

  public void bind(GL2 gl) {
    gl.glUseProgram(programId);
  }

  public void unbind(GL2 gl) {
    gl.glUseProgram(0);
  }

  public void destroy(GL2 gl) {
    for (int i = 0; i < vertexShaders_.size(); i++) {
      gl.glDeleteShader(vertexShaders_.get(i));
    }
    for (int i = 0; i < fragmentShaders_.size(); i++) {
      gl.glDeleteShader(fragmentShaders_.get(i));
    }
    if (programId != 0) {
      gl.glDeleteProgram(programId);
    }
  }

  /* UNIFORM SETTING */

  public void setUniform(GL2 gl, String name, float value) {
    int id = gl.glGetUniformLocation(programId, name);
    gl.glUniform1f(id, value);
  }

  public void setUniform(GL2 gl, String name, float[] values, int count) {
    int id = gl.glGetUniformLocation(programId, name);
    if (id == -1) {
      warn("Uniform parameter not found in program: " + name, GLSLWarnType.UNIFORM_NOT_FOUND);
      return;
    }
    switch (count) {
      case 1:
        gl.glUniform1fv(id, 1, values, 0);
        break;
      case 2:
        gl.glUniform2fv(id, 1, values, 0);
        break;
      case 3:
        gl.glUniform3fv(id, 1, values, 0);
        break;
      case 4:
        gl.glUniform4fv(id, 1, values, 0);
        break;
    }
  }

  /* TEXTURES */

  public void setTextureUnit(GL2 gl, String texname, int texunit) {
    int[] params = new int[] {0};
    gl.glGetProgramiv(programId, GL2.GL_LINK_STATUS, params, 0);
    if (params[0] != 1) {
      throw new RuntimeException("Error: setTextureUnit needs program to be linked.");
    }
    int id = gl.glGetUniformLocation(programId, texname);
    if (id == -1) {
      warn("Invalid texture " + texname, GLSLWarnType.UNDEFINED);
      return;
    }
    gl.glUniform1i(id, texunit);
  }

  public void bindTexture(GL2 gl, int target, String texname, int texid, int texunit) {
    gl.glActiveTexture(GL2.GL_TEXTURE0 + texunit);
    gl.glBindTexture(target, texid);
    setTextureUnit(gl, texname, texunit);
    gl.glActiveTexture(GL2.GL_TEXTURE0);
  }

  public void bindTexture2D(GL2 gl, String texname, int texid, int texunit) {
    bindTexture(gl, GL2.GL_TEXTURE_2D, texname, texid, texunit);
  }

  public void bindTexture3D(GL2 gl, String texname, int texid, int texunit) {
    bindTexture(gl, GL2.GL_TEXTURE_3D, texname, texid, texunit);
  }

  public void bindTextureRECT(GL2 gl, String texname, int texid, int texunit) {
    bindTexture(gl, GL2.GL_TEXTURE_RECTANGLE_ARB, texname, texid, texunit);
  }

  /* LOAD */

  public void loadAndCompileShaders(GL2 gl, ShaderFilePair files) {
    loadAndCompileVertexShader(gl, files.getVertexStream(), files.getVertexURL());
    loadAndCompileFragmentShader(gl, files.getFragmentStream(), files.getFragmentURL());
  }

  public void loadAndCompileVertexShader(GL2 gl, URL fileURL) {
    if (fileURL != null) {
      try {
        InputStream stream = fileURL.openStream();
        loadAndCompileVertexShader(gl, stream, fileURL);
      } catch (IOException e) {
        throw new RuntimeException("Problem reading the shader file " + fileURL.getPath());
      }
    } else {
      throw new RuntimeException("input url is null");
    }
  }

  public void loadAndCompileVertexShader(GL2 gl, InputStream stream) {
    loadAndCompileVertexShader(gl, stream, null);
  }

  /**
   * 
   * @param gl
   * @param stream shader source code ressource
   * @param infoURL only used as information for warnings if shader does not compile properly
   */
  public void loadAndCompileVertexShader(GL2 gl, InputStream stream, URL infoURL) {
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
    compileVertexShader(gl, infoURL, content);
  }

  public void loadAndCompileFragmentShader(GL2 gl, URL fileURL) {
    if (fileURL != null) {
      InputStream stream;
      try {
        stream = fileURL.openStream();
        loadAndCompileFragmentShader(gl, stream, fileURL);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

    } else {
      throw new RuntimeException("Null shader file!");
    }
  }

  public void loadAndCompileFragmentShader(GL2 gl, InputStream stream) {
    loadAndCompileFragmentShader(gl, stream, null);
  }

  public void loadAndCompileFragmentShader(GL2 gl, InputStream stream, URL infoURL) {
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

    compileFragmentShader(gl, infoURL, content);
  }

  /* COMPILE */

  public void compileVertexShader(GL2 gl, URL infoURL, String content) {
    int iID = gl.glCreateShader(GL2.GL_VERTEX_SHADER);

    int count = 1;
    
    String[] programText = new String[1];
    // find and replace program name with "main"
    programText[0] = content;
    
    int[] programLength = new int[1];
    programLength[0] = programText[0].length();
    
    
    gl.glShaderSource(iID, count, programText, programLength, 0);
    gl.glCompileShader(iID);

    verifyShaderCompiled(gl, infoURL, iID, content);
    vertexShaders_.add(iID);
  }

  public void compileFragmentShader(GL2 gl, URL infoURL, String content) {
    int iID = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);

    int count = 1;

    String[] programText = new String[count];
    // find and replace program name with "main"
    programText[0] = content;
    
    int[] programLength = new int[count];
    programLength[0] = programText[0].length();
    
    
    gl.glShaderSource(iID, count, programText, programLength, 0);
    gl.glCompileShader(iID);
    
    verifyShaderCompiled(gl, infoURL, iID, content);
    
    fragmentShaders_.add(iID);
  }

  /* VERIFICATIONS */

  public void verifyShaderCompiled(GL2 gl, URL fileURL, int programId, String content) {
    int[] compileStatus = new int[] {0};
    int[] logLength = new int[] {0};

    gl.glGetShaderiv(programId, GL2.GL_COMPILE_STATUS, compileStatus, 0);
    gl.glGetShaderiv(programId, GL2.GL_INFO_LOG_LENGTH, logLength, 0);

    if (compileStatus[0] != GL2.GL_TRUE) {
      warnScript(gl, fileURL, readErrors(gl, programId), compileStatus[0], logLength[0], content);
    }
  }

  public void verifyLinkStatus(GL2 gl, int programId) {
    int[] linkStatus = new int[] {0};
    int[] logLength = new int[] {0};
    
    gl.glGetProgramiv(programId, GL2.GL_LINK_STATUS, linkStatus, 0);
    gl.glGetProgramiv(programId, GL2.GL_INFO_LOG_LENGTH, logLength, 0);

    if (linkStatus[0] != 1) {
      warnLink(gl, readErrors(gl, programId), linkStatus[0], logLength[0]);
    }
  }

  public String readErrors(GL2 gl, int iID) {
    int ERROR_BUFFER_SIZE = 8192;
    byte[] errorBuffer = new byte[ERROR_BUFFER_SIZE];
    int[] messageLength = new int[1];
    gl.glGetShaderInfoLog(iID, ERROR_BUFFER_SIZE, messageLength, 0, errorBuffer, 0);
    return new String(errorBuffer);
  }

  public void validateProgram(GL2 gl) {
    gl.glValidateProgram(programId);
    checkShaderLogInfo(gl, programId);
  }

  /**
   * read logs and either throw exception, print to console or append to error log according to
   * the configured {@link Strictness}
   */
  protected void checkShaderLogInfo(GL2 inGL, int shaderObjectID) {
    IntBuffer logLengthBuffer = Buffers.newDirectIntBuffer(1);
    
    inGL.glGetObjectParameterivARB(shaderObjectID, GL2.GL_OBJECT_INFO_LOG_LENGTH_ARB,
        logLengthBuffer);
    
    int logLength = logLengthBuffer.get();
    if (logLength <= 1) {
      return;
    }

    // Get logs
    ByteBuffer shaderLogBuffer = Buffers.newDirectByteBuffer(logLength);
    BufferUtil.flip(shaderLogBuffer);
    BufferUtil.limit(shaderLogBuffer, logLength);
    
    inGL.glGetInfoLogARB(shaderObjectID, logLength, logLengthBuffer, shaderLogBuffer);
    
    
    byte[] shaderLogBytes = new byte[logLength];
    shaderLogBuffer.get(shaderLogBytes);
    
    // Read logs and warn
    String shaderValidationLog = new String(shaderLogBytes);
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

  protected void warnScript(GL2 gl, URL fileURL, String error, int compileStatus, int logLength,
      String content) {
    if (fileURL != null)
      warn(fileURL.getPath(), GLSLWarnType.UNDEFINED);
    else
      warn("unknown file", GLSLWarnType.UNDEFINED);
    warn("compile status: " + compileStatus + " (GL_TRUE=" + GL2.GL_TRUE + ", GL_FALSE="
        + GL2.GL_FALSE + ")", GLSLWarnType.UNDEFINED);
    warn("log length: " + logLength, GLSLWarnType.UNDEFINED);
    warn(error, GLSLWarnType.UNDEFINED);

    if (WARN_SHOW_SHADER_SOURCE)
      warn(content, GLSLWarnType.UNDEFINED);
  }

  protected void warnLink(GL2 gl, String error, int linkStatus, int logLength) {
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
