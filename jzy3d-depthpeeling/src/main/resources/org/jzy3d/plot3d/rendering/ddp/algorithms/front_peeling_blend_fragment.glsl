//--------------------------------------------------------------------------------------
// Order Independent Transparency with Depth Peeling
//
// Author: Louis Bavoil
// Email: sdkfeedback@nvidia.com
//
// Copyright (c) NVIDIA Corporation. All rights reserved.
//--------------------------------------------------------------------------------------

uniform sampler2DRect TempTex;

void main(void)
{
	gl_FragColor = texture2DRect(TempTex, gl_FragCoord.xy);
}
