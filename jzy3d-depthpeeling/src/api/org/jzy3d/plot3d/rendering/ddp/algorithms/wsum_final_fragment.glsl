//--------------------------------------------------------------------------------------
// Order Independent Transparency with Weighted Sums
//
// Author: Louis Bavoil
// Email: sdkfeedback@nvidia.com
//
// Copyright (c) NVIDIA Corporation. All rights reserved.
//--------------------------------------------------------------------------------------

uniform samplerRECT ColorTex;
uniform vec3 BackgroundColor;

// Sum(A_i * C_i) + C_bg * (1 - Sum(A_i))
void main(void)
{
	vec4 S = textureRect(ColorTex, gl_FragCoord.xy);
	gl_FragColor.rgb = S.rgb + BackgroundColor * (1.0 - S.a);
}
