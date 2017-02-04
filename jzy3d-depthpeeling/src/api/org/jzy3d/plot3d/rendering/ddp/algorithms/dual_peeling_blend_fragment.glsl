//--------------------------------------------------------------------------------------
// Order Independent Transparency with Dual Depth Peeling
//
// Author: Louis Bavoil
// Email: sdkfeedback@nvidia.com
//
// Copyright (c) NVIDIA Corporation. All rights reserved.
//--------------------------------------------------------------------------------------

uniform samplerRECT TempTex;

void main(void)
{
	gl_FragColor = textureRect(TempTex, gl_FragCoord.xy);
	// for occlusion query
	if (gl_FragColor.a == 0) discard;
}
