//--------------------------------------------------------------------------------------
// Order Independent Transparency with Weighted Sums
//
// Author: Louis Bavoil
// Email: sdkfeedback@nvidia.com
//
// Copyright (c) NVIDIA Corporation. All rights reserved.
//--------------------------------------------------------------------------------------


// A Rectangle Texture is a Texture that contains a single 2D image with no mipmaps.
// It has no power-of-two restrictions on its size.
// Texture coordinates for accessing this texture must be texel values (floating-point),
// representing texels within the texture, rather than normalized texture coordinates.
//
// OpenGL : GL_TEXTURE_RECTANGLE
//
//
uniform sampler2DRect ColorTex; // Java : g_accumulationTexId[0]

//
uniform vec3 BackgroundColor; // Java : g_backgroundColor

// Sum(A_i * C_i) + C_bg * (1 - Sum(A_i))
void main(void)
{
	// Use the texture coordinate gl_FragCoord.xy to do a texture lookup in the
    // rectangle texture currently bound to sampler
	vec4 S = texture2DRect(ColorTex, gl_FragCoord.xy);


	// Original formulae
	gl_FragColor.rgb = S.rgb + BackgroundColor * (1.0 - S.a);

	// Opaque values become black, alpha values remains alpha
	//gl_FragColor = S + vec4(BackgroundColor * (1.0 - S.a), 1.0);


	//gl_FragColor = S + vec4(BackgroundColor, 1.0);


	//gl_FragColor.a = 1.0;
}
