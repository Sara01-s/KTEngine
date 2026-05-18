#version 450 core

out vec4 o_FragColor;

uniform sampler2D u_Texture;

in vec4 v_Color;
in vec2 v_TexCoords;

void main() {
    o_FragColor = texture(u_Texture, v_TexCoords) * v_Color;
}