#version 110

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;
attribute vec4 vt;
varying vec4 vVaryingColor;

void main() {
	vVaryingColor=gl_Color;
	gl_Position=gl_ProjectionMatrix*gl_ModelViewMatrix*vt;
}
