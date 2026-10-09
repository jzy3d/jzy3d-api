#version 110

attribute vec4 vt;
uniform sampler1D transfer;
uniform vec3 min_max;
varying vec4 vVaryingColor;

void main() {

	gl_Position=gl_ProjectionMatrix*gl_ModelViewMatrix*vt;

	if (min_max.z == 0.0) {
		float val = clamp((vt.z-min_max.x)/(min_max.y-min_max.x),0.0,1.0);
		vec4 vMappedColor=texture1D(transfer,(vt.z-min_max.x)/(min_max.y-min_max.x));
		vVaryingColor=texture1D(transfer,val);
	} else {
		vVaryingColor = vec4(1);
	}
}


