package org.jzy3d.plot3d.primitives.vbo.drawable;

import java.nio.Buffer;

public class BufferUtil {
  /**
   * A simple utility to upcast buffers to invoke some of their methods without hitting compatibility issues between Java < 9 and Java 9+
   * 
   * https://stackoverflow.com/questions/61267495/exception-in-thread-main-java-lang-nosuchmethoderror-java-nio-bytebuffer-flip
   * 
   * Useful if compiled with Java >9 and app run with Java <9
   */
  public static void rewind(Buffer buffer) {
    buffer.rewind();
  }
  
  /**
   * A simple utility to upcast buffers to invoke some of their methods without hitting compatibility issues between Java < 9 and Java 9+
   * 
   * https://stackoverflow.com/questions/61267495/exception-in-thread-main-java-lang-nosuchmethoderror-java-nio-bytebuffer-flip
   * 
   * Useful if compiled with Java >9 and app run with Java <9
   */
  public static int capacity(Buffer buffer) {
    return buffer.capacity();
  }
}
