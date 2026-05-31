#type vertex
#version 430 core

layout (location = 0) in vec3 a_position;
layout (location = 2) in vec2 a_uv;

out vec2 v_uv;

void main() {
    v_uv = a_uv;
    gl_Position = vec4(a_position.xy, 0.0, 1.0);
}

#type fragment
#version 430 core

out vec4 fragColor;
in vec2 v_uv;

uniform sampler2D _SceneTexture;
const float _Threshold = 0.1;

void main() {
    vec3 color = texture(_SceneTexture, v_uv).rgb;

    float luminosity = dot(color, vec3(0.2126, 0.7152, 0.0722));

    if (luminosity < _Threshold) {
        fragColor = vec4(0.0, 0.0, 0.0, 1.0);
    } else {
        fragColor = vec4(color, 1.0);
    }
}