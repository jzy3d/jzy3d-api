//--------------------------------------------------------------------------------------
// Order Independent Transparency with Dual Depth Peeling
//
// Author: Louis Bavoil
// Email: sdkfeedback@nvidia.com
//
// Copyright (c) NVIDIA Corporation. All rights reserved.
//--------------------------------------------------------------------------------------

uniform samplerRECT DepthBlenderTex;
uniform samplerRECT FrontBlenderTex;
uniform samplerRECT BackBlenderTex;

void main(void)
{
	vec4 frontColor = textureRect(FrontBlenderTex, gl_FragCoord.xy);
	vec3 backColor = textureRect(BackBlenderTex, gl_FragCoord.xy).rgb;
	float alphaMultiplier = 1.0 - frontColor.w;

	// front + back
	gl_FragColor.rgb = frontColor + backColor * alphaMultiplier;
	
	// front blender
	//gl_FragColor.rgb = frontColor + vec3(alphaMultiplier);
	
	// back blender
	//gl_FragColor.rgb = backColor;
}
