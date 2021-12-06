varying vec4 transfertColor;


void main(void)
{
	gl_Position = gl_ProjectionMatrix * gl_ModelViewMatrix * gl_Vertex;

	// output: passing geometrical informations to the pixel shader
	float diffuse = abs(normalize(gl_NormalMatrix * gl_Normal).z);
	gl_TexCoord[0].xyz = vec3(gl_Vertex.xy, diffuse);
	
	transfertColor = gl_Color;
}
