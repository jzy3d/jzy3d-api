//--------------------------------------------------------------------------------------
// Order Independent Transparency Vertex Shader
//
// Author: Louis Bavoil
// Email: sdkfeedback@nvidia.com
//
// Copyright (c) NVIDIA Corporation. All rights reserved.
//--------------------------------------------------------------------------------------

varying vec4 transfertColor;

// --------------------------------------------------------------
// CODE TO HAVE DRAGON EXAMPLE WORKING
#if 0
vec3 ShadeVertex()
{
	float diffuse = abs(normalize(gl_NormalMatrix * gl_Normal).z);
	return vec3(gl_Vertex.xy, diffuse);
}

// --------------------------------------------------------------
// CODE TO HAVE JZY3d SCENE GRAPH CONTENT KEEPING ORIGINAL COLORS
#else
vec3 ShadeVertex()
{
	transfertColor = gl_Color; // STORE GL CURRENT COLOR TO BE PASSED TO FRAGMENT SHADER
	float diffuse = abs(normalize(gl_NormalMatrix * gl_Normal).z);
	return vec3(gl_Vertex.xy, diffuse);
}
#endif
