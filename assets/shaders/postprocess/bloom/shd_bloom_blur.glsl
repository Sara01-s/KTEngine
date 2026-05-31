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

uniform sampler2D _BloomTexture;
uniform vec2 _Direction; // (1.0/width, 1.0/height)
uniform float _BlurSize = 1.5;

void main() {
    vec2 texelSize = _Direction * _BlurSize;
    vec3 color = texture(_BloomTexture, v_uv).rgb * 4.0;

    color += texture(_BloomTexture, v_uv + vec2(texelSize.x, texelSize.y)).rgb;
    color += texture(_BloomTexture, v_uv + vec2(texelSize.x, -texelSize.y)).rgb;
    color += texture(_BloomTexture, v_uv + vec2(-texelSize.x, texelSize.y)).rgb;
    color += texture(_BloomTexture, v_uv + vec2(-texelSize.x, -texelSize.y)).rgb;

    fragColor = vec4(color / 8.0, 1.0);
}