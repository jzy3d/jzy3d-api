uniform float mandel_x;
uniform float mandel_y;
uniform float mandel_width;
uniform float mandel_height; 
uniform float mandel_iterations;

void main()
{
	gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_Position = ftransform();
}
