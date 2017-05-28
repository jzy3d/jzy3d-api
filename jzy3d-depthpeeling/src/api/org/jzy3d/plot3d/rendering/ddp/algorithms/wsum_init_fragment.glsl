//--------------------------------------------------------------------------------------
// Order Independent Transparency with Weighted Sums
//
// Author: Louis Bavoil
// Email: sdkfeedback@nvidia.com
//
// Copyright (c) NVIDIA Corporation. All rights reserved.
//--------------------------------------------------------------------------------------

vec4 ShadeFragment();

void main(void)
{
	vec4 color = ShadeFragment();
	gl_FragColor = vec4(color.rgb * color.a, color.a);
}
