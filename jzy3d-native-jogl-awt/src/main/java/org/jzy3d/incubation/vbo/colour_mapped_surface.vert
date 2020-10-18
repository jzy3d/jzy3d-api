#version 110

attribute vec4 vt;
uniform sampler1D transfer;
uniform vec4 min_max;
varying vec4 vVaryingColor;

void main() {

	gl_Position=gl_ProjectionMatrix*gl_ModelViewMatrix*vt;
	float val = clamp((vt.z-min_max.x)/(min_max.y-min_max.x),0.0,1.0);
	vVaryingColor=texture1D(transfer,val);

}
