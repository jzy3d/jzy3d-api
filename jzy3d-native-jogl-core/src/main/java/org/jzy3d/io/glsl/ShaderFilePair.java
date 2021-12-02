package org.jzy3d.io.glsl;

import java.io.InputStream;
import java.net.URL;

/**
 * Store a pair of URL for a vertex and fragment shader, relative to a class {@link Package}.
 * 
 * @author Martin Pernollet
 */
public class ShaderFilePair {
  protected static char separator = '/';// File.separatorChar;


  protected String packaje;
  protected String vertexName;
  protected String fragmentName;
  
  public ShaderFilePair(Class<?> clazz, String vertexName, String fragmentName) {
    this(packageName(clazz.getPackage()), vertexName, fragmentName);
  }

  public ShaderFilePair(Package packaje, String vertexName, String fragmentName) {
    this(packageName(packaje), vertexName, fragmentName);
  }

  protected ShaderFilePair(String packaje, String vertexName, String fragmentName) {
    this.packaje = packaje;
    this.vertexName = vertexName;
    this.fragmentName = fragmentName;
  }

  public String getVertexPath() {
    return getPackagePath() + vertexName;
  }

  public String getFragmentPath() {
    return getPackagePath() + fragmentName;
  }

  public String getPackagePath() {
    return separator + packaje + separator;
  }

  public URL getVertexURL() {
    // URL out = Thread.currentThread().getContextClassLoader().getResource(getVertexPath());
    // URL out = this.getClass().getResource(getVertexPath());
    URL out = ShaderFilePair.class.getResource(getVertexPath());
    if (out == null)
      throw new RuntimeException("vertex: unable to open URL to:'" + getVertexPath() + "'");
    return out;
  }

  public InputStream getVertexStream() {
    InputStream out = ShaderFilePair.class.getResourceAsStream(getVertexPath());
    if (out == null)
      throw new RuntimeException("vertex: unable to open stream to:'" + getVertexPath() + "'");
    return out;
  }

  public URL getFragmentURL() {
    // Does not work on linux?
    // URL out = Thread.currentThread().getContextClassLoader().getResource(getFragmentPath());

    URL out = ShaderFilePair.class.getResource(getFragmentPath());
    if (out == null)
      throw new RuntimeException("fragment : unable to open url to:'" + getFragmentPath() + "'");
    return out;
  }

  public InputStream getFragmentStream() {
    InputStream out = ShaderFilePair.class.getResourceAsStream(getFragmentPath());
    if (out == null)
      throw new RuntimeException(
          "fragment : unable to find to open stream to:'" + getFragmentPath() + "'");
    return out;
  }

  /* */

  public String getVertexName() {
    return vertexName;
  }

  public void setVertexName(String vertexName) {
    this.vertexName = vertexName;
  }

  public String getFragmentName() {
    return fragmentName;
  }

  public void setFragmentName(String fragmentName) {
    this.fragmentName = fragmentName;
  }

  protected static String packageName(Class<?> clazz) {
    return packageName(clazz.getPackage());
  }

  protected static String packageName(Package p) {
    return p.getName().replace('.', separator);
  }
}
