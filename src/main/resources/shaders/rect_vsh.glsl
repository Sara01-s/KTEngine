#version 450 core

layout (location = 0) in vec2 a_Position;
layout (location = 1) in vec2 a_TexCoords;
layout (location = 2) in vec4 a_Color;

uniform mat4 u_MVP;

out vec4 v_Color;
out vec2 v_TexCoords;

void main() {
    v_Color = a_Color;
    v_TexCoords = a_TexCoords;

    gl_Position = u_MVP * vec4(a_Position, 0.0, 1.0);
}