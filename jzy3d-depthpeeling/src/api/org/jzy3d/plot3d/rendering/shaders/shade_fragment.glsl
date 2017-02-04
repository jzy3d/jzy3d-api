

uniform float Alpha;

varying vec4 transfertColor;

#define COLOR_FREQ 0.2
#define ALPHA_FREQ 0.2

vec4 ShadeFragment()
{
	float xWorldPos = gl_TexCoord[0].x;
	float yWorldPos = gl_TexCoord[0].y;
	float diffuse = gl_TexCoord[0].z;

	vec4 color;
	float i = floor(xWorldPos * COLOR_FREQ);
	float j = floor(yWorldPos * ALPHA_FREQ);
	color.rgb = (fmod(i, 2.0) == 0) ? vec3(.4,.85,.90) : vec3(1.0);
	color.a = Alpha;

	color.rgb *= diffuse;
	return color;
}

void main(void)
{
	vec4 color = ShadeFragment();
	color.a = Alpha;
	//gl_FragData[0] = vec4(color.rgb * color.a, color.a);
	//gl_FragData[1] = vec4(1.0);
	
	// Seems to be the only way to reinject original color:
	//gl_BackColor = gl_Color; // pass color
	//gl_FragColor = gl_Color; // make black content
	//gl_FragData[0] = gl_Color; //make black content
	
	gl_FragColor = transfertColor;
}

