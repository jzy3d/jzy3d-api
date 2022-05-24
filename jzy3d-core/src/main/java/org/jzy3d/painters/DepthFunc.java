package org.jzy3d.painters;

public enum DepthFunc {
  // Never passes.
  GL_NEVER,
  // Passes if the incoming depth value is less than the stored depth value.
  GL_LESS,
  // Passes if the incoming depth value is equal to the stored depth value.
  GL_EQUAL,
  // Passes if the incoming depth value is less than or equal to the stored depth value.
  GL_LEQUAL,
  // Passes if the incoming depth value is greater than the stored depth value.
  GL_GREATER,
  // Passes if the incoming depth value is not equal to the stored depth value.
  GL_NOTEQUAL,
  // Passes if the incoming depth value is greater than or equal to the stored depth value.
  GL_GEQUAL,
  // Always passes.
  GL_ALWAYS;
}
