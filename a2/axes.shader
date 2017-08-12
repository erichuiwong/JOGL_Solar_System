#version 430


uniform mat4 mv_matrix;
uniform mat4 proj_matrix;
uniform float axesColor[4];

out vec4 varyingColor;

const vec4 vertices[6] = vec4[6]
	(vec4(-100.0,0.0,0.0, 1.0),
	vec4( 100.0,0.0,0.0, 1.0),
	vec4( 0.0,-100.0,0.0, 1.0),
	vec4( 0.0,100.0,0.0, 1.0),
	vec4( 0.0,0.0,-100.0, 1.0),
	vec4( 0.0,0.0,100.0, 1.0));

void main(void) {
		gl_Position = mv_matrix * proj_matrix * vertices[gl_VertexID];
		varyingColor = vec4(axesColor[0], axesColor[1], axesColor[2], axesColor[3]);
}

