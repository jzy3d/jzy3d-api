#version 110

varying vec4 vVaryingColor;
uniform sampler3D volumeTexture;
uniform sampler1D transfer;
uniform vec4 eye;
uniform vec2 minMax;


vec4 UnderCompositing(vec4 src, vec4 dst) {
	vec4 result = dst;
	result.rgb -= src.rgb * (1.0-dst.a)*src.a;
	result.a   += src.a   * (1.0-dst.a);

	return result;
}

vec4 TopCompositing(vec4 initial, vec4 sample) {
	vec4 result = initial;
	result.rgb +=  ((1.0-initial.a)*sample.rgb*sample.a);
	result.a   += ((1.0-initial.a)*sample.a);

	return result;
}

void main() {
	// ???????????????
	vec4 test = vVaryingColor;

	// Retrieve the value for the current voxel
	vec4 value = texture3D(volumeTexture, test.xyz);

	// Normalize the RED component in [0;1].
	// RED only because colormap is given as a 1D texture
	float val = clamp((value.r - minMax.x) / (minMax.y - minMax.x),0.0,1.0);

	// Retrieve the color for this value in the colormap-texture
	vec4 t = texture1D(transfer, val);

	// Reset "value" to the colormap value
	value = t;

	// Raise alpha to a power of two to make alpha it decrease faster
	value.a = val*val;

	//value.a = 0.999;//val;


	for (int i = 1; i < 300; i++) {

		// The actual ray casting, so we are maximum taking 300 steps of 0.005 (of the unit cube) in the direction of the eye vector,
		// getting the value from the texture at each step along this line and compositing them together.
		// Really the longest distance in the unit cube would be sqrt 3, so the 300*0.005 steps (1.5)
		// is a bit short for the longest cube distance (1.73) but that would only really show in some edge cases.

		vec3 eyeShifted = eye.xyz * 0.005 * float(i);
		vec3 dist = test.xyz - eyeShifted;


		if (dist.x > 1.0 || dist.y > 1.0 || dist.z > 1.0 || dist.x < 0.0 || dist.y < 0.0 || dist.z < 0.0) {
			break;
		}


		vec4 v = texture3D(volumeTexture, dist);

		float val0 = clamp((v.r - minMax.x) / (minMax.y - minMax.x), 0.0, 1.0);

		vec4 t0 =texture1D(transfer,val0);



		v = t0;
		v.a = val0*val0;

		//			value = UnderCompositing(v,value);
		value = TopCompositing(value,v);

		if (value.a > 0.99) {
			gl_FragColor = value;
			return;
		}

	}

	gl_FragColor = value;
}


