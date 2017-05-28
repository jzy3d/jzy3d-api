package org.jzy3d.io.glsl;

import java.io.InputStream;
import java.net.URL;

/**
 * Store a pair of URL for a vertex and fragment shader, relative to a class {@link Package}.
 * 
 * @author Martin Pernollet
 */
public class ShaderFilePair {
    public ShaderFilePair(Class<?> c, String vertexName, String fragmentName) {
        this(packageName(c.getPackage()), vertexName, fragmentName);
    }
    
    public ShaderFilePair(Package p, String vertexName, String fragmentName) {
        this(packageName(p), vertexName, fragmentName);
    }
    
    protected ShaderFilePair(String pack, String vertexName, String fragmentName) {
        this.pack = pack;
        this.vertexName = vertexName;
        this.fragmentName = fragmentName;
    }
    
    public String getVertexPath() {
        return getPath()+vertexName;
    }
    
    public String getFragmentPath() {
        return getPath()+fragmentName;
    }
    
    public String getPath(){
        return c+pack+c;
    }
    
    public URL getVertexURL() {
        //URL out = Thread.currentThread().getContextClassLoader().getResource(getVertexPath());
    	//URL out = this.getClass().getResource(getVertexPath());
        URL out = ShaderFilePair.class.getResource(getVertexPath());
        if(out==null)
            throw new RuntimeException("vertex: unable to open URL to:'"+getVertexPath()+"'");
        return out;
    }
    
    public InputStream getVertexStream() {
        InputStream out = ShaderFilePair.class.getResourceAsStream(getVertexPath());
        if(out==null)
            throw new RuntimeException("vertex: unable to open stream to:'"+getVertexPath()+"'");
        return out;
    }
    
    /*public InputStream getVertexInputStream() {
        InputStream stream = getClass().getResourceAsStream(getVertexPath());
        if(stream==null)
            throw new RuntimeException("unable to find shader InputStream for :'"+getVertexPath()+"'");
        return stream;
    }*/
    
    
    
    public URL getFragmentURL() {
    	// Does not work on linux?
        //URL out = Thread.currentThread().getContextClassLoader().getResource(getFragmentPath());
        
    	
    	URL out = ShaderFilePair.class.getResource(getFragmentPath());
        if(out==null)
            throw new RuntimeException("fragment : unable to open url to:'"+getFragmentPath()+"'");
        return out;
    }
    
    public InputStream getFragmentStream() {
        InputStream out = ShaderFilePair.class.getResourceAsStream(getFragmentPath());
        if(out==null)
            throw new RuntimeException("fragment : unable to find to open stream to:'"+getFragmentPath()+"'");
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
    
    protected static String packageName(Class<?> clazz){
        return packageName(clazz.getPackage());
    }
    
    protected static String packageName(Package p){
        return p.getName().replace('.', c);
    }
    
    protected static char c = '/';//File.separatorChar;


    protected String pack;
    protected String vertexName;
    protected String fragmentName;
}
