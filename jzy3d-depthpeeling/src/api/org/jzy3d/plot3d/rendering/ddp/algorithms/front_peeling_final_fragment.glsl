//--------------------------------------------------------------------------------------
// Order Independent Transparency with Depth Peeling
//
// Author: Louis Bavoil
// Email: sdkfeedback@nvidia.com
//
// Copyright (c) NVIDIA Corporation. All rights reserved.
//--------------------------------------------------------------------------------------

uniform samplerRECT ColorTex;
uniform vec3 BackgroundColor;

void main(void)
{
	vec4 frontColor = textureRect(ColorTex, gl_FragCoord.xy);
	gl_FragColor.rgb = frontColor + BackgroundColor * frontColor.a;
}
