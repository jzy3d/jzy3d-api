//--------------------------------------------------------------------------------------
// Order Independent Transparency with Depth Peeling
//
// Author: Louis Bavoil
// Email: sdkfeedback@nvidia.com
//
// Copyright (c) NVIDIA Corporation. All rights reserved.
//--------------------------------------------------------------------------------------

uniform sampler2DRect ColorTex;
uniform vec3 BackgroundColor;

void main(void)
{
	// the final image is opaque : define its alpha, otherwise left to the GL implementation
	gl_FragColor.a = 1.0;
	vec4 frontColor = texture2DRect(ColorTex, gl_FragCoord.xy);
	gl_FragColor.rgb = frontColor.rgb + BackgroundColor * frontColor.a;

	// initial
	//gl_FragColor.rgb = frontColor + BackgroundColor * frontColor.a;

}
