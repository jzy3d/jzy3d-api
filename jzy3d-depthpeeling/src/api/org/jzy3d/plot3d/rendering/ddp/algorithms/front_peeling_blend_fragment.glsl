//--------------------------------------------------------------------------------------
// Order Independent Transparency with Depth Peeling
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
}
