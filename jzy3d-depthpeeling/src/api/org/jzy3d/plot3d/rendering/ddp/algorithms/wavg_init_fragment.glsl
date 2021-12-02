//--------------------------------------------------------------------------------------
// Order Independent Transparency with Average Color
//
// Author: Louis Bavoil
// Email: sdkfeedback@nvidia.com
//
// Copyright (c) NVIDIA Corporation. All rights reserved.
//--------------------------------------------------------------------------------------

//#version 120

//#extension ARB_draw_buffers : require

uniform float Alpha;

#if 1
vec4 ShadeFragment();

void main(void)
{
	vec4 color = ShadeFragment();
	color.a = Alpha;
	gl_FragData[0] = vec4(color.rgb * color.a, color.a); 
	gl_FragData[1] = vec4(1.0);
}
#else
vec4 ShadeFragment(); // to have alpha used somewhere

void main(void)
{
	gl_FragColor = transfertColor;
}
#endif
