uniform float mandel_x;
uniform float mandel_y;
uniform float mandel_width;
uniform float mandel_height;
uniform float mandel_iterations;

float calculateMandelbrotIterations(float x, float y) {
	float xx = 0.0;
    float yy = 0.0;
    float iter = 0.0;
    float xxXxCache = 0.0;
    float yyYyCache = 0.0;
    while ((xxXxCache = xx * xx) + (yyYyCache=yy * yy) <= 4.0 && iter<mandel_iterations) {
        float temp = xxXxCache - yyYyCache + x;
        yy = 2.0*xx*yy + y;

        xx = temp;

        iter ++;
    }
    return iter;
}

const vec3 blue = vec3(0.0,0.0,1.0);
const vec3 white = vec3(1.0,1.0,1.0);
const vec3 yellow = vec3(1.0,1.0,0.0);
const vec3 red = vec3(1.0,0.0,0.0);
const float colorResolution = 16.0; // how many iterations the first color band should use (2nd use the double amount)

vec3 getColorByIndex(float index){
	float i = mod(index,4.0);
	if (i<0.5){
		return blue;
	}
	if (i<1.5){
		return white;
	}
	if (i<2.5){
		return yellow;
	}
	return red;
}

vec4 getColor(float iterations) {
    	if (iterations==mandel_iterations){
     	return vec4(0.0,0.0,0.0,1.0);
     }
	float colorIndex = 0.0;
	float iterationsFloat = iterations;
	float colorRes = colorResolution;
	while (iterationsFloat>colorRes){
		iterationsFloat -= colorRes;
		colorRes = colorRes*2.0;
		colorIndex ++;
	}
	float fraction = iterationsFloat/colorRes;
	vec3 from = getColorByIndex(colorIndex);
	vec3 to = getColorByIndex(colorIndex+1.0);
	vec3 res = mix(from,to,fraction);
	return vec4(res.x,res.y,res.z,1.0);
}

void main()
{
	float x = mandel_x+gl_TexCoord[0].x*mandel_width;
	float y = mandel_y+gl_TexCoord[0].y*mandel_height;
	float iterations = calculateMandelbrotIterations(x,y);
	gl_FragColor = getColor(iterations);
}
