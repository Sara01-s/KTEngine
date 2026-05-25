#type vertex
#version 450 core

layout(location = 0) in vec3 a_position;
layout(location = 1) in vec2 a_uv;

uniform mat4 _MVP;

out vec2 uv;

void main() {
    uv = a_uv;
    gl_Position = _MVP * vec4(a_position, 1.0);
}

#type fragment
#version 450 core

out vec4 fragColor;

uniform sampler2D _MainTex;
uniform vec4 _ColorTint;

in vec2 uv;

void main() {
    fragColor = texture(_MainTex, uv) * _ColorTint;
}