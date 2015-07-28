#ifdef GL_ES
	precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float u_fadeout;

void main()
{
	gl_FragColor = v_color * vec4(u_fadeout, u_fadeout, u_fadeout, 1) * texture2D(u_texture, v_texCoords);
}