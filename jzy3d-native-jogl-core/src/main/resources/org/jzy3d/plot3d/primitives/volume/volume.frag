#version 110

varying vec4 vVaryingColor;
uniform sampler3D volumeTexture;
uniform sampler1D transfer;
uniform vec4 eye;
uniform vec2 minMax;


vec4 UnderCompositing(vec4 src, vec4 dst) {
	vec4 result = dst;
	result.rgb   -= src.rgb * (1.0-dst.a)*src.a;
	result.a     += src.a   * (1.0-dst.a);
	return result;
}

vec4 TopCompositing(vec4 initial, vec4 sample) {
	vec4 result = initial;
	result.rgb   +=  ((1.0-initial.a)*sample.rgb*sample.a);
	result.a     += ((1.0-initial.a)*sample.a);

	return result;
}

void main() {
	vec4 test = vVaryingColor;
	vec4 value = texture3D(volumeTexture,(test.xyz));

	float val = clamp((value.r-minMax.x)/(minMax.y-minMax.x),0.0,1.0);
	vec4 t =texture1D(transfer,val);
	value = t;

	value.a = val*val;

	for (int i = 1; i < 300; i++) {
		vec3 tmp = test.xyz;
		tmp = tmp - eye.xyz*0.005*float(i);
		if (tmp.x > 1.0 || tmp.y > 1.0 || tmp.z > 1.0 || tmp.x < 0.0 || tmp.y < 0.0 || tmp.z < 0.0) {
			break;
		}
		vec4 v = texture3D(volumeTexture,tmp);
		float val0 = clamp((v.r-minMax.x)/(minMax.y-minMax.x),0.0,1.0);
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


